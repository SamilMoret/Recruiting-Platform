package br.com.one.jobportal.service.impl;

import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.repository.JobRepository;
import br.com.one.jobportal.repository.UserRepository;
import br.com.one.jobportal.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

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
    @Transactional(rollbackFor = Exception.class)
    public Job createJob(Job job, String recruiterEmail) {
        // Validações iniciais
        if (job == null) {
            throw new IllegalArgumentException("O objeto Job não pode ser nulo");
        }
        
        if (recruiterEmail == null || recruiterEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("O email do recrutador não pode ser vazio");
        }

        // Busca o recrutador
        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new EntityNotFoundException("Recrutador não encontrado com o email: " + recruiterEmail));

        try {
            // Associa o recrutador à vaga
            job.setRecruiter(recruiter);
            
            // Salva a vaga
            Job savedJob = jobRepository.save(job);
            
            // Atualiza a lista de vagas do recrutador
            recruiter.getPostedJobs().add(savedJob);
            
            return savedJob;
            
        } catch (Exception e) {
            // Log do erro
            System.err.println("Erro ao criar vaga: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao criar vaga: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> getJobsByRecruiterEmail(String recruiterEmail) {
        if (recruiterEmail == null || recruiterEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("O email do recrutador não pode ser vazio");
        }

        try {
            User recruiter = userRepository.findByEmail(recruiterEmail)
                    .orElseThrow(() -> new EntityNotFoundException("Recrutador não encontrado com o email: " + recruiterEmail));
            
            return jobRepository.findByRecruiter(recruiter);
            
        } catch (Exception e) {
            System.err.println("Erro ao buscar vagas do recrutador: " + e.getMessage());
            throw new RuntimeException("Erro ao buscar vagas: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Job updateJob(Long id, Job jobDetails, User recruiter) {
        if (id == null) {
            throw new IllegalArgumentException("ID da vaga não pode ser nulo");
        }
        
        if (jobDetails == null) {
            throw new IllegalArgumentException("Os detalhes da vaga não podem ser nulos");
        }
        
        if (recruiter == null) {
            throw new IllegalArgumentException("O recrutador não pode ser nulo");
        }

        try {
            Job job = jobRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada com ID: " + id));
            
            // Verifica se o recrutador é o dono da vaga
            if (!job.getRecruiter().getId().equals(recruiter.getId())) {
                throw new SecurityException("Acesso negado: você não tem permissão para atualizar esta vaga");
            }

            // Atualiza apenas os campos permitidos
            job.setTitle(jobDetails.getTitle());
            job.setDescription(jobDetails.getDescription());
            job.setCompany(jobDetails.getCompany());
            job.setLocation(jobDetails.getLocation());
            job.setEmploymentType(jobDetails.getEmploymentType());
            job.setExperienceLevel(jobDetails.getExperienceLevel());
            job.setSalaryMin(jobDetails.getSalaryMin());
            job.setSalaryMax(jobDetails.getSalaryMax());
            job.setActive(jobDetails.isActive());
            
            // Salva as alterações
            return jobRepository.save(job);
            
        } catch (EntityNotFoundException | SecurityException e) {
            // Relança exceções específicas
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar vaga com ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar a vaga: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJob(Long id, User recruiter) {
        if (id == null) {
            throw new IllegalArgumentException("ID da vaga não pode ser nulo");
        }
        
        if (recruiter == null) {
            throw new IllegalArgumentException("O recrutador não pode ser nulo");
        }

        try {
            Job job = jobRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada com ID: " + id));
            
            // Verifica se o recrutador é o dono da vaga
            if (!job.getRecruiter().getId().equals(recruiter.getId())) {
                throw new SecurityException("Acesso negado: você não tem permissão para excluir esta vaga");
            }
            
            // Remove a vaga do banco de dados
            jobRepository.delete(job);
            
        } catch (EntityNotFoundException | SecurityException e) {
            // Relança exceções específicas
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao excluir vaga com ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Erro ao excluir a vaga: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Job getJobById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID da vaga não pode ser nulo");
        }
        
        try {
            return jobRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada com ID: " + id));
        } catch (Exception e) {
            System.err.println("Erro ao buscar vaga com ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Erro ao buscar a vaga: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> getJobsByRecruiter(User recruiter) {
        if (recruiter == null) {
            throw new IllegalArgumentException("O recrutador não pode ser nulo");
        }
        
        try {
            return jobRepository.findByRecruiter(recruiter);
        } catch (Exception e) {
            System.err.println("Erro ao buscar vagas do recrutador com ID " + recruiter.getId() + ": " + e.getMessage());
            throw new RuntimeException("Erro ao buscar vagas do recrutador: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Job> searchJobs(Specification<Job> spec, Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("O objeto Pageable não pode ser nulo");
        }
        
        try {
            return jobRepository.findAll(spec != null ? spec : Specification.anyOf(), pageable);
        } catch (Exception e) {
            System.err.println("Erro ao buscar vagas com os critérios fornecidos: " + e.getMessage());
            throw new RuntimeException("Erro ao buscar vagas: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getJobCountByRecruiter(User recruiter) {
        if (recruiter == null) {
            throw new IllegalArgumentException("O recrutador não pode ser nulo");
        }
        
        try {
            return jobRepository.countByRecruiter(recruiter);
        } catch (Exception e) {
            System.err.println("Erro ao contar vagas do recrutador com ID " + recruiter.getId() + ": " + e.getMessage());
            throw new RuntimeException("Erro ao contar vagas do recrutador: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Job toggleJobStatus(Long id, User recruiter, boolean active) {
        if (id == null) {
            throw new IllegalArgumentException("ID da vaga não pode ser nulo");
        }
        
        if (recruiter == null) {
            throw new IllegalArgumentException("O recrutador não pode ser nulo");
        }

        try {
            Job job = jobRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada com ID: " + id));
            
            // Verifica se o recrutador é o dono da vaga
            if (!job.getRecruiter().getId().equals(recruiter.getId())) {
                throw new SecurityException("Acesso negado: você não tem permissão para alterar o status desta vaga");
            }
            
            // Atualiza o status da vaga
            job.setActive(active);
            
            // Salva as alterações
            return jobRepository.save(job);
            
        } catch (EntityNotFoundException | SecurityException e) {
            // Relança exceções específicas
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao alterar status da vaga com ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Erro ao alterar o status da vaga: " + e.getMessage(), e);
        }
    }
}