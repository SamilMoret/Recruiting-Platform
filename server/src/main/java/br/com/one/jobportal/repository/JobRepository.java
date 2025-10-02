package br.com.one.jobportal.repository;

import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.entity.EmploymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job>, JobCustomRepository {
    
    // ===== MÉTODOS DE CONSULTA POR CAMPO =====
    
    // 1. Campos básicos
    List<Job> findByTitleContainingIgnoreCase(String title);
    
    @Query("SELECT j FROM Job j WHERE j.description LIKE %:keyword% OR j.requirements LIKE %:keyword%")
    List<Job> searchInDescriptionOrRequirements(@Param("keyword") String keyword);
    
    List<Job> findByLocationContainingIgnoreCase(String location);
    List<Job> findByCategory(String category);
    
    // 2. Tipo de emprego
    List<Job> findByType(EmploymentType type);
    
    // 3. Faixa salarial
    List<Job> findBySalaryMinGreaterThanEqualAndSalaryMaxLessThanEqual(Integer minSalary, Integer maxSalary);
    
    // 4. Status da vaga
    List<Job> findByIsClosed(boolean isClosed);
    List<Job> findByIsClosedFalse(); // Método de conveniência
    
    // 5. Vaga salva
    @Modifying
    @Query("UPDATE Job j SET j.isSaved = :isSaved WHERE j.id = :jobId")
    void updateSavedStatus(@Param("jobId") Long jobId, @Param("isSaved") Boolean isSaved);
    
    List<Job> findByIsSaved(Boolean isSaved);
    List<Job> findByIsSavedAndRecruiter(Boolean isSaved, User recruiter);
    
    // 6. Relacionamento com recrutador
    List<Job> findByRecruiter(User recruiter);
    Long countByRecruiter(User recruiter);
    
    // 7. Status da candidatura
    @Modifying
    @Query("UPDATE Job j SET j.applicationStatus = :status WHERE j.id = :jobId")
    void updateApplicationStatus(@Param("jobId") Long jobId, @Param("status") String status);
    
    List<Job> findByApplicationStatus(String status);
    List<Job> findByApplicationStatusAndRecruiter(String status, User recruiter);
    
    // 8. Contagem de candidaturas
    @Modifying
    @Query("UPDATE Job j SET j.applicationCount = j.applicationCount + 1 WHERE j.id = :jobId")
    void incrementApplicationCount(@Param("jobId") Long jobId);
    
    List<Job> findTop10ByOrderByApplicationCountDesc();
    
    // 9. Campos de auditoria
    List<Job> findByCreatedAtAfter(LocalDateTime date);
    List<Job> findByUpdatedAtAfter(LocalDateTime date);
    
    // ===== MÉTODOS DE COMPATIBILIDADE =====
    
    /**
     * @deprecated Use {@link #findByRecruiter(User)} instead
     */
    @Deprecated
    default List<Job> findByCompany(User company) {
        return findByRecruiter(company);
    }
    
    /**
     * @deprecated Use {@link #countByRecruiter(User)} instead
     */
    @Deprecated
    default Long countByCompany(User company) {
        return countByRecruiter(company);
    }
}
