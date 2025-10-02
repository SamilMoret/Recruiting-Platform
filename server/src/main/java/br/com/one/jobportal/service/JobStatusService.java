package br.com.one.jobportal.service;

import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.repository.JobRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobStatusService {

    private final JobRepository jobRepository;

    public JobStatusService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional
    public Job toggleSaveStatus(Long jobId, User user) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));
        
        // Alterna o status de salvamento
        job.setIsSaved(!job.getIsSaved());
        return jobRepository.save(job);
    }

    @Transactional
    public Job updateApplicationStatus(Long jobId, String status, User user) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));
        
        job.setApplicationStatus(status);
        return jobRepository.save(job);
    }

    @Transactional(readOnly = true)
    public boolean isJobSaved(Long jobId, User user) {
        return jobRepository.findById(jobId)
                .map(Job::getIsSaved)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));
    }

    @Transactional
    public void incrementApplicationCount(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));
        
        job.setApplicationCount(job.getApplicationCount() + 1);
        jobRepository.save(job);
    }
}
