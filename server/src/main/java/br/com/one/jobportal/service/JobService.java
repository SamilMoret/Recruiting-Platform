package br.com.one.jobportal.service;

import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.entity.EmploymentType;
import br.com.one.jobportal.dto.JobRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public interface JobService {
    
    // ===== OPERAÇÕES BÁSICAS =====
    Job createJob(Job job, String recruiterEmail);
    Job getJobById(Long id);
    void deleteJob(Long id, User recruiter);
    
    // ===== MÉTODOS DE ATUALIZAÇÃO =====
    Job updateJob(Long id, JobRequest jobRequest, User recruiter);
    Job updateJob(Long id, User recruiter, boolean active);
    Job updateJobStatus(Long id, User recruiter, boolean active);
    
    // ===== MÉTODOS DE CONSULTA POR CAMPO =====
    
    // 1. Campos básicos
    List<Job> getJobsByTitle(String title);
    List<Job> searchInDescriptionOrRequirements(String keyword);
    List<Job> getJobsByLocation(String location);
    List<Job> getJobsByCategory(String category);
    
    // 2. Tipo de emprego
    List<Job> getJobsByType(EmploymentType type);
    
    // 3. Faixa salarial
    List<Job> getJobsBySalaryRange(Integer minSalary, Integer maxSalary);
    
    // 4. Status da vaga
    List<Job> getJobsByStatus(boolean isClosed);
    List<Job> getActiveJobs();
    
    // 5. Vaga salva
    Job toggleSaveStatus(Long jobId, User user);
    List<Job> getSavedJobs(User user);
    boolean isJobSaved(Long jobId, User user);
    
    // 6. Relacionamento com recrutador
    List<Job> getJobsByRecruiter(User recruiter);
    List<Job> getJobsByRecruiterEmail(String recruiterEmail);
    Long getJobCountByRecruiter(User recruiter);
    
    // 7. Status da candidatura
    Job updateApplicationStatus(Long jobId, String status, User user);
    List<Job> getJobsByApplicationStatus(String status);
    List<Job> getJobsByApplicationStatusAndRecruiter(String status, User recruiter);
    
    // 8. Contagem de candidaturas
    void incrementApplicationCount(Long jobId);
    List<Job> getMostAppliedJobs();
    
    // 9. Métodos de auditoria
    List<Job> getJobsCreatedAfter(LocalDateTime date);
    List<Job> getJobsUpdatedAfter(LocalDateTime date);
    
    // ===== MÉTODOS DE PESQUISA AVANÇADA =====
    Page<Job> searchJobs(Specification<Job> spec, Pageable pageable);
}