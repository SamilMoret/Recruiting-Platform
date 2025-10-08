package br.com.one.jobportal.repository;

import br.com.one.jobportal.entity.Application;
import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    // Métodos básicos
    List<Application> findByApplicant(User applicant);
    List<Application> findByJob(Job job);
    List<Application> findByJobRecruiter(User recruiter);
    Optional<Application> findByApplicantAndJob(User applicant, Job job);
    Long countByJob(Job job);
    Long countByApplicant(User applicant);
    
    // Contagem de candidaturas por status
    Long countByStatus(Application.Status status);
    
    // Contagem de candidaturas por vaga e status
    Long countByJobAndStatus(Job job, Application.Status status);
    
    // Métodos otimizados com paginação e fetch joins
    @Query("SELECT a FROM Application a " +
           "LEFT JOIN FETCH a.job j " +
           "LEFT JOIN FETCH j.recruiter " +
           "LEFT JOIN FETCH a.applicant " +
           "WHERE a.applicant = :applicant " +
           "ORDER BY a.createdAt DESC")
    Page<Application> findByApplicantWithDetails(@Param("applicant") User applicant, Pageable pageable);
    
    @Query("SELECT a FROM Application a " +
           "LEFT JOIN FETCH a.job j " +
           "LEFT JOIN FETCH a.applicant " +
           "WHERE j.recruiter = :recruiter " +
           "ORDER BY a.createdAt DESC")
    Page<Application> findByJobRecruiterWithDetails(@Param("recruiter") User recruiter, Pageable pageable);
    
    @Query("SELECT a FROM Application a " +
           "LEFT JOIN FETCH a.job j " +
           "LEFT JOIN FETCH j.recruiter " +
           "LEFT JOIN FETCH a.applicant " +
           "WHERE a.job.id = :jobId " +
           "ORDER BY a.createdAt DESC")
    List<Application> findByJobIdWithDetails(@Param("jobId") Long jobId);
    
    // Verificação de existência
    boolean existsByApplicantIdAndJobId(Long applicantId, Long jobId);
    
    // Contagem otimizada
    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.recruiter = :recruiter")
    long countByJobRecruiter(@Param("recruiter") User recruiter);
    
    // Filtros por status
    @Query("SELECT a FROM Application a " +
           "LEFT JOIN FETCH a.job j " +
           "LEFT JOIN FETCH j.recruiter " +
           "LEFT JOIN FETCH a.applicant " +
           "WHERE a.applicant = :applicant " +
           "AND (:status IS NULL OR a.status = :status) " +
           "ORDER BY a.createdAt DESC")
    Page<Application> findByApplicantAndStatus(
            @Param("applicant") User applicant, 
            @Param("status") Application.Status status, 
            Pageable pageable);
    
    @Query("SELECT a FROM Application a " +
           "LEFT JOIN FETCH a.job j " +
           "LEFT JOIN FETCH a.applicant " +
           "WHERE j.recruiter = :recruiter " +
           "AND (:status IS NULL OR a.status = :status) " +
           "AND (:jobId IS NULL OR j.id = :jobId) " +
           "ORDER BY a.createdAt DESC")
    Page<Application> findByRecruiterAndStatusAndJob(
            @Param("recruiter") User recruiter,
            @Param("status") Application.Status status,
            @Param("jobId") Long jobId,
            Pageable pageable);
    
    // Estatísticas
    @Query("SELECT a.status, COUNT(a) FROM Application a " +
           "WHERE a.applicant = :applicant " +
           "GROUP BY a.status")
    List<Object[]> countByApplicantGroupByStatus(@Param("applicant") User applicant);
    
    @Query("SELECT a.status, COUNT(a) FROM Application a " +
           "WHERE a.job.recruiter = :recruiter " +
           "GROUP BY a.status")
    List<Object[]> countByRecruiterGroupByStatus(@Param("recruiter") User recruiter);
    
    @Query("SELECT a.status, COUNT(a) FROM Application a " +
           "WHERE a.job.id = :jobId " +
           "GROUP BY a.status")
    List<Object[]> countByJobIdGroupByStatus(@Param("jobId") Long jobId);
    
    // Contagem por status específico
    long countByApplicantAndStatus(User applicant, Application.Status status);
    long countByJobRecruiterAndStatus(User recruiter, Application.Status status);
    long countByJobIdAndStatus(Long jobId, Application.Status status);
    
}
