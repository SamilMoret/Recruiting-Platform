package br.com.one.jobportal.service.impl;

import br.com.one.jobportal.dto.ApplyJobRequest;
import br.com.one.jobportal.dto.response.ApplicationResponse;
import br.com.one.jobportal.dto.response.ApplicationStatsResponse;
import br.com.one.jobportal.entity.Application;
import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.exception.ResourceAlreadyExistsException;
import br.com.one.jobportal.exception.ResourceNotFoundException;
import br.com.one.jobportal.exception.UnauthorizedAccessException;
import br.com.one.jobportal.repository.ApplicationRepository;
import br.com.one.jobportal.repository.JobRepository;
import br.com.one.jobportal.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional
    public ApplicationResponse applyForJob(ApplyJobRequest request, User applicant) {
        // Check if the job exists
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + request.getJobId()));
        
        // Check if the user has already applied for this job
        if (applicationRepository.findByApplicantAndJob(applicant, job).isPresent()) {
            throw new ResourceAlreadyExistsException("You have already applied for this job");
        }
        
        // Create and save the application
        Application application = new Application();
        application.setJob(job);
        application.setApplicant(applicant);
        application.setCoverLetter(request.getCoverLetter());
        application.setResume(request.getResumeUrl());
        application.setStatus(Application.Status.PENDING);
        application.setCreatedAt(LocalDateTime.now());
        
        Application savedApplication = applicationRepository.save(application);
        return ApplicationResponse.fromEntity(savedApplication);
    }

    @Override
    @Transactional
    public void withdrawApplication(Long applicationId, User applicant) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        
        // Check if the applicant is the owner of the application
        if (!application.getApplicant().getId().equals(applicant.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to withdraw this application");
        }
        
        applicationRepository.delete(application);
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplicationStatus(Long applicationId, String status, User recruiter) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        
        // Check if the user is the recruiter who posted the job
        if (!application.getJob().getRecruiter().getId().equals(recruiter.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to update this application");
        }
        
        try {
            Application.Status newStatus = Application.Status.valueOf(status);
            application.setStatus(newStatus);
            Application updatedApplication = applicationRepository.save(application);
            return ApplicationResponse.fromEntity(updatedApplication);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(Long applicationId, User user) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        
        // Check if the user is the applicant or the recruiter
        if (!application.getApplicant().getId().equals(user.getId()) && 
            !application.getJob().getRecruiter().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to view this application");
        }
        
        return ApplicationResponse.fromEntity(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByJobSeeker(User jobSeeker) {
        return applicationRepository.findByApplicant(jobSeeker).stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getApplicationsByJobSeeker(User jobSeeker, Pageable pageable) {
        // Usa query otimizada com fetch joins
        return applicationRepository.findByApplicantWithDetails(jobSeeker, pageable)
                .map(ApplicationResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsForEmployer(User employer) {
        return applicationRepository.findByJobRecruiter(employer).stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getApplicationsForEmployer(User employer, Pageable pageable) {
        // Usa query otimizada com fetch joins
        return applicationRepository.findByJobRecruiterWithDetails(employer, pageable)
                .map(ApplicationResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByJobId(Long jobId, User user) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        // Check if the user is the recruiter who posted the job
        if (!job.getRecruiter().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to view applications for this job");
        }
        
        // Usa query otimizada com fetch joins
        return applicationRepository.findByJobIdWithDetails(jobId).stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countApplicationsByJobSeeker(User jobSeeker) {
        return applicationRepository.countByApplicant(jobSeeker);
    }

    @Override
    @Transactional(readOnly = true)
    public long countApplicationsForEmployer(User employer) {
        // Usa contagem otimizada
        return applicationRepository.countByJobRecruiter(employer);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasApplied(Long jobId, User jobSeeker) {
        // Usa verificação otimizada
        return applicationRepository.existsByApplicantIdAndJobId(jobSeeker.getId(), jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getApplicationsByJobSeeker(User jobSeeker, String status, Pageable pageable) {
        log.info("Buscando candidaturas para usuário: {}, status: {}", jobSeeker.getEmail(), status);
        try {
            Application.Status statusEnum = status != null ? Application.Status.valueOf(status.toUpperCase()) : null;
            Page<ApplicationResponse> result = applicationRepository.findByApplicantAndStatus(jobSeeker, statusEnum, pageable)
                    .map(ApplicationResponse::fromEntity);
            log.info("Encontradas {} candidaturas", result.getTotalElements());
            return result;
        } catch (Exception e) {
            log.error("Erro ao buscar candidaturas: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getApplicationsForEmployer(User employer, String status, Long jobId, Pageable pageable) {
        Application.Status statusEnum = status != null ? Application.Status.valueOf(status.toUpperCase()) : null;
        return applicationRepository.findByRecruiterAndStatusAndJob(employer, statusEnum, jobId, pageable)
                .map(ApplicationResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationStatsResponse getJobSeekerStats(User jobSeeker) {
        log.info("Buscando estatísticas para o candidato: {}", jobSeeker.getEmail());
        
        // Contagem total de candidaturas
        long total = applicationRepository.countByApplicant(jobSeeker);
        
        // Contagem por status
        long pending = applicationRepository.countByApplicantAndStatus(jobSeeker, Application.Status.PENDING);
        long approved = applicationRepository.countByApplicantAndStatus(jobSeeker, Application.Status.APPROVED);
        long rejected = applicationRepository.countByApplicantAndStatus(jobSeeker, Application.Status.REJECTED);
        
        // Agrupar por status
        Map<String, Long> byStatus = new HashMap<>();
        List<Object[]> statusCounts = applicationRepository.countByApplicantGroupByStatus(jobSeeker);
        if (statusCounts != null) {
            for (Object[] row : statusCounts) {
                if (row != null && row.length >= 2) {
                    Application.Status status = (Application.Status) row[0];
                    Long count = (row[1] != null) ? ((Number) row[1]).longValue() : 0L;
                    byStatus.put(status.name(), count);
                }
            }
        }
        
        // Agrupar por mês (exemplo simplificado)
        Map<String, Long> byMonth = new HashMap<>();
        List<Application> applications = applicationRepository.findByApplicant(jobSeeker);
        if (applications != null) {
            byMonth = applications.stream()
                .filter(app -> app.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                    app -> String.valueOf(app.getCreatedAt().getMonthValue()),
                    Collectors.counting()
                ));
        }
        
        return ApplicationStatsResponse.builder()
                .totalApplications(total)
                .pendingApplications(pending)
                .approvedApplications(approved)
                .rejectedApplications(rejected)
                .applicationsByStatus(byStatus)
                .applicationsByMonth(byMonth)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationStatsResponse getEmployerStats(User employer) {
        log.info("Buscando estatísticas para o empregador: {}", employer.getEmail());
        
        // Contagem total de candidaturas
        Long total = applicationRepository.countByJobRecruiter(employer);
        
        // Contagem por status
        Long pending = applicationRepository.countByJobRecruiterAndStatus(employer, Application.Status.PENDING);
        Long approved = applicationRepository.countByJobRecruiterAndStatus(employer, Application.Status.APPROVED);
        Long rejected = applicationRepository.countByJobRecruiterAndStatus(employer, Application.Status.REJECTED);
        
        // Agrupar por status
        Map<String, Long> byStatus = new HashMap<>();
        List<Object[]> statusCounts = applicationRepository.countByRecruiterGroupByStatus(employer);
        if (statusCounts != null) {
            for (Object[] row : statusCounts) {
                if (row != null && row.length >= 2) {
                    Application.Status status = (Application.Status) row[0];
                    Long count = (row[1] != null) ? ((Number) row[1]).longValue() : 0L;
                    byStatus.put(status.name(), count);
                }
            }
        }
        
        // Agrupar por mês (exemplo simplificado)
        Map<String, Long> byMonth = new HashMap<>();
        List<Application> applications = applicationRepository.findByJobRecruiter(employer);
        if (applications != null) {
            byMonth = applications.stream()
                .filter(app -> app.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                    app -> String.valueOf(app.getCreatedAt().getMonthValue()),
                    Collectors.counting()
                ));
        }
        
        return ApplicationStatsResponse.builder()
                .totalApplications(total != null ? total : 0L)
                .pendingApplications(pending != null ? pending : 0L)
                .approvedApplications(approved != null ? approved : 0L)
                .rejectedApplications(rejected != null ? rejected : 0L)
                .applicationsByStatus(byStatus)
                .applicationsByMonth(byMonth)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationStatsResponse getJobStats(Long jobId, User employer) {
        log.info("Buscando estatísticas para a vaga: {}", jobId);
        
        // Verificar se a vaga existe e se o empregador é o dono
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada com o ID: " + jobId));
        
        if (!job.getRecruiter().equals(employer)) {
            throw new UnauthorizedAccessException("Você não tem permissão para acessar esta vaga");
        }
        
        // Contagem total de candidaturas
        Long total = applicationRepository.countByJob(job);
        
        // Contagem por status
        Long pending = applicationRepository.countByJobAndStatus(job, Application.Status.PENDING);
        Long approved = applicationRepository.countByJobAndStatus(job, Application.Status.APPROVED);
        Long rejected = applicationRepository.countByJobAndStatus(job, Application.Status.REJECTED);
        
        // Agrupar por status
        Map<String, Long> byStatus = new HashMap<>();
        List<Object[]> statusCounts = applicationRepository.countByJobIdGroupByStatus(jobId);
        if (statusCounts != null) {
            for (Object[] row : statusCounts) {
                if (row != null && row.length >= 2) {
                    Application.Status status = (Application.Status) row[0];
                    Long count = (row[1] != null) ? ((Number) row[1]).longValue() : 0L;
                    byStatus.put(status.name(), count);
                }
            }
        }
        
        // Agrupar por mês (exemplo simplificado)
        Map<String, Long> byMonth = new HashMap<>();
        List<Application> applications = applicationRepository.findByJob(job);
        if (applications != null) {
            byMonth = applications.stream()
                .filter(app -> app.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                    app -> String.valueOf(app.getCreatedAt().getMonthValue()),
                    Collectors.counting()
                ));
        }
        
        return ApplicationStatsResponse.builder()
                .totalApplications(total != null ? total : 0L)
                .pendingApplications(pending != null ? pending : 0L)
                .approvedApplications(approved != null ? approved : 0L)
                .rejectedApplications(rejected != null ? rejected : 0L)
                .applicationsByStatus(byStatus)
                .applicationsByMonth(byMonth)
                .build();
    }
}
