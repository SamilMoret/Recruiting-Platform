package br.com.one.jobportal.service.impl;

import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.repository.JobRepository;
import br.com.one.jobportal.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public Job createJob(Job job, User recruiter) {
        job.setRecruiter(recruiter);
        return jobRepository.save(job);
    }

    @Override
    public Job updateJob(Long id, Job jobDetails, User recruiter) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            // Verifica se o recrutador é o dono da vaga
            if (!job.getRecruiter().getId().equals(recruiter.getId())) {
                throw new RuntimeException("Acesso negado: você não é o proprietário desta vaga");
            }

            job.setTitle(jobDetails.getTitle());
            job.setDescription(jobDetails.getDescription());
            job.setCompany(jobDetails.getCompany());
            job.setLocation(jobDetails.getLocation());
            job.setEmploymentType(jobDetails.getEmploymentType());
            job.setExperienceLevel(jobDetails.getExperienceLevel());
            job.setSalaryMin(jobDetails.getSalaryMin());
            job.setSalaryMax(jobDetails.getSalaryMax());

            return jobRepository.save(job);
        }
        throw new RuntimeException("Vaga não encontrada com ID: " + id);
    }

    @Override
    public void deleteJob(Long id, User recruiter) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            if (!job.getRecruiter().getId().equals(recruiter.getId())) {
                throw new RuntimeException("Acesso negado: você não é o proprietário desta vaga");
            }
            jobRepository.deleteById(id);
        } else {
            throw new RuntimeException("Vaga não encontrada com ID: " + id);
        }
    }

    @Override
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada com ID: " + id));
    }

    @Override
    public List<Job> getJobsByRecruiter(User recruiter) {
        return jobRepository.findByRecruiter(recruiter);
    }

    @Override
    public Page<Job> searchJobs(Specification<Job> spec, Pageable pageable) {
        return jobRepository.findAll(spec, pageable);
    }

    @Override
    public Long getJobCountByRecruiter(User recruiter) {
        return jobRepository.countByRecruiter(recruiter);
    }

    @Override
    public Job toggleJobStatus(Long id, User recruiter, boolean active) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            if (!job.getRecruiter().getId().equals(recruiter.getId())) {
                throw new RuntimeException("Acesso negado: você não é o proprietário desta vaga");
            }
            job.setActive(active);
            return jobRepository.save(job);
        }
        throw new RuntimeException("Vaga não encontrada com ID: " + id);
    }
}