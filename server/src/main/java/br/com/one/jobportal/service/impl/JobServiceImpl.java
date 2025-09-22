package br.com.one.jobportal.service.impl;

import br.com.one.jobportal.dto.JobRequest;
import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.entity.EmploymentType;
import br.com.one.jobportal.repository.JobRepository;
import br.com.one.jobportal.repository.UserRepository;
import br.com.one.jobportal.service.JobService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobServiceImpl(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Job createJob(Job job, String recruiterEmail) {
        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new EntityNotFoundException("Recrutador não encontrado"));
        
        job.setRecruiter(recruiter);
        return jobRepository.save(job);
    }

    @Override
    public Job updateJob(Long id, JobRequest jobRequest, User recruiter) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));
        
        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new AccessDeniedException("Acesso negado");
        }
        
        // Atualiza os campos da vaga com base no jobRequest
        job.setTitle(jobRequest.getTitle());
        job.setDescription(jobRequest.getDescription());
        job.setLocation(jobRequest.getLocation());
        if (jobRequest.getEmploymentType() != null) {
            job.setType(jobRequest.getEmploymentType());
        }
        
        if (jobRequest.getSalaryMin() != null) {
            job.setSalaryMin(jobRequest.getSalaryMin().intValue());
        }
        if (jobRequest.getSalaryMax() != null) {
            job.setSalaryMax(jobRequest.getSalaryMax().intValue());
        }
        if (jobRequest.getActive() != null) {
            job.setClosed(!jobRequest.getActive());
        }
        
        return jobRepository.save(job);
    }
    
    @Override
    public Job updateJob(Long id, User recruiter, boolean active) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));
                
        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new AccessDeniedException("Acesso negado");
        }
        
        job.setClosed(!active);
        return jobRepository.save(job);
    }

    @Override
    public void deleteJob(Long id, User recruiter) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));
        
        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new AccessDeniedException("Acesso negado");
        }
        
        jobRepository.delete(job);
    }

    @Override
    @Transactional(readOnly = true)
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));
    }

    // ===== MÉTODOS DE CONSULTA POR CAMPO =====
    
    // 1. Campos básicos
    @Override
    @Transactional(readOnly = true)
    public List<Job> getJobsByTitle(String title) {
        return jobRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> searchInDescriptionOrRequirements(String keyword) {
        return jobRepository.searchInDescriptionOrRequirements(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> getJobsByLocation(String location) {
        return jobRepository.findByLocationContainingIgnoreCase(location);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> getJobsByCategory(String category) {
        return jobRepository.findByCategory(category);
    }
    
    // 2. Tipo de emprego
    @Override
    @Transactional(readOnly = true)
    public List<Job> getJobsByType(EmploymentType type) {
        return jobRepository.findByType(type);
    }
    
    // 3. Faixa salarial
    @Override
    @Transactional(readOnly = true)
    public List<Job> getJobsBySalaryRange(Integer minSalary, Integer maxSalary) {
        return jobRepository.findBySalaryMinGreaterThanEqualAndSalaryMaxLessThanEqual(
            minSalary != null ? minSalary : 0,
            maxSalary != null ? maxSalary : Integer.MAX_VALUE
        );
    }
    
    // 4. Status da vaga
    @Override
    @Transactional(readOnly = true)
    public List<Job> getJobsByStatus(boolean isClosed) {
        return jobRepository.findByIsClosed(isClosed);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> getActiveJobs() {
        return jobRepository.findByIsClosedFalse();
    }
    
    // 5. Vaga salva
    @Override
    @Transactional
    public Job toggleSaveStatus(Long jobId, User user) {
        Job job = getJobById(jobId);
        job.setIsSaved(!job.getIsSaved());
        return jobRepository.save(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> getSavedJobs(User user) {
        return jobRepository.findByIsSavedAndRecruiter(true, user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isJobSaved(Long jobId, User user) {
        return jobRepository.findById(jobId)
                .map(job -> job.getIsSaved() && job.getRecruiter().equals(user))
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));
    }
    
    // 6. Relacionamento com recrutador
    @Override
    @Transactional(readOnly = true)
    public List<Job> getJobsByRecruiter(User recruiter) {
        return jobRepository.findByRecruiter(recruiter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> getJobsByRecruiterEmail(String recruiterEmail) {
        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new EntityNotFoundException("Recrutador não encontrado"));
        return jobRepository.findByRecruiter(recruiter);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getJobCountByRecruiter(User recruiter) {
        return jobRepository.countByRecruiter(recruiter);
    }
    
    // 7. Status da candidatura
    @Override
    @Transactional
    public Job updateApplicationStatus(Long jobId, String status, User user) {
        Job job = getJobById(jobId);
        if (!job.getRecruiter().equals(user)) {
            throw new AccessDeniedException("Acesso negado: você não é o recrutador desta vaga");
        }
        job.setApplicationStatus(status);
        return jobRepository.save(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> getJobsByApplicationStatus(String status) {
        return jobRepository.findByApplicationStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> getJobsByApplicationStatusAndRecruiter(String status, User recruiter) {
        return jobRepository.findByApplicationStatusAndRecruiter(status, recruiter);
    }
    
    // 8. Contagem de candidaturas
    @Override
    @Transactional
    public void incrementApplicationCount(Long jobId) {
        jobRepository.incrementApplicationCount(jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> getMostAppliedJobs() {
        return jobRepository.findTop10ByOrderByApplicationCountDesc();
    }
    
    // 9. Métodos de auditoria
    @Override
    @Transactional(readOnly = true)
    public List<Job> getJobsCreatedAfter(LocalDateTime date) {
        return jobRepository.findByCreatedAtAfter(date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> getJobsUpdatedAfter(LocalDateTime date) {
        return jobRepository.findByUpdatedAtAfter(date);
    }
    
    // ===== MÉTODOS DE PESQUISA AVANÇADA =====
    @Override
    @Transactional(readOnly = true)
    public Page<Job> searchJobs(Specification<Job> spec, Pageable pageable) {
        return jobRepository.findAll(spec, pageable);
    }
    
    // ===== MÉTODOS DE ATUALIZAÇÃO =====
    @Override
    public Job updateJobStatus(Long id, User recruiter, boolean active) {
        Job job = getJobById(id);
        if (!job.getRecruiter().equals(recruiter)) {
            throw new AccessDeniedException("Acesso negado: você não é o recrutador desta vaga");
        }
        job.setClosed(!active);
        return jobRepository.save(job);
    }
    
}
