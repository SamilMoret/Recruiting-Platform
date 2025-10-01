package br.com.one.jobportal.service.impl;

import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
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

import java.util.Objects;

/**
 * Implementação do serviço de gerenciamento de vagas.
 * Fornece operações CRUD e de negócio para a entidade Job.
 */
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
    public Job saveJob(Job job) {
        Objects.requireNonNull(job, "O objeto Job não pode ser nulo");
        return jobRepository.save(job);
    }

    @Override
    @Transactional(readOnly = true)
    public Job getJobById(Long id) throws EntityNotFoundException {
        return jobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada com o ID: " + id));
    }

    @Override
    @Transactional
    public void deleteJob(Long id, User recruiter) throws EntityNotFoundException, AccessDeniedException {
        Objects.requireNonNull(id, "O ID da vaga não pode ser nulo");
        Objects.requireNonNull(recruiter, "O usuário recrutador não pode ser nulo");
        
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada com o ID: " + id));
                
        if (!isJobOwner(id, recruiter)) {
            throw new AccessDeniedException("Acesso negado: você não tem permissão para excluir esta vaga");
        }
        
        jobRepository.delete(job);
    }

    @Override
    @Transactional
    public Job updateJob(Long id, Job jobUpdates, User recruiter) throws EntityNotFoundException, AccessDeniedException {
        Objects.requireNonNull(id, "O ID da vaga não pode ser nulo");
        Objects.requireNonNull(jobUpdates, "Os dados de atualização da vaga não podem ser nulos");
        Objects.requireNonNull(recruiter, "O usuário recrutador não pode ser nulo");
        
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada com o ID: " + id));
        
        if (!isJobOwner(id, recruiter)) {
            throw new AccessDeniedException("Acesso negado: você não tem permissão para atualizar esta vaga");
        }
        
        // Atualiza apenas os campos não nulos
        if (jobUpdates.getTitle() != null) {
            job.setTitle(jobUpdates.getTitle());
        }
        if (jobUpdates.getDescription() != null) {
            job.setDescription(jobUpdates.getDescription());
        }
        if (jobUpdates.getRequirements() != null) {
            job.setRequirements(jobUpdates.getRequirements());
        }
        if (jobUpdates.getLocation() != null) {
            job.setLocation(jobUpdates.getLocation());
        }
        if (jobUpdates.getCategory() != null) {
            job.setCategory(jobUpdates.getCategory());
        }
        if (jobUpdates.getType() != null) {
            job.setType(jobUpdates.getType());
        }
        if (jobUpdates.getSalaryMin() != null) {
            job.setSalaryMin(jobUpdates.getSalaryMin());
        }
        if (jobUpdates.getSalaryMax() != null) {
            job.setSalaryMax(jobUpdates.getSalaryMax());
        }
        
        return jobRepository.save(job);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Job> searchJobs(Specification<Job> spec, Pageable pageable) {
        Objects.requireNonNull(spec, "A especificação de pesquisa não pode ser nula");
        Objects.requireNonNull(pageable, "A configuração de paginação não pode ser nula");
        
        return jobRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isJobOwner(Long jobId, User user) {
        if (jobId == null || user == null) {
            return false;
        }
        return jobRepository.findById(jobId)
                .map(job -> job.getRecruiter() != null && job.getRecruiter().getId().equals(user.getId()))
                .orElse(false);
    }
}
