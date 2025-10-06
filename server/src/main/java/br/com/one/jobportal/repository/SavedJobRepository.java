package br.com.one.jobportal.repository;

import br.com.one.jobportal.entity.SavedJob;
import br.com.one.jobportal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    
    // Verifica se uma vaga já foi salva por um candidato
    boolean existsByJobSeekerIdAndJobId(Long jobSeekerId, Long jobId);
    
    // Encontra uma vaga salva específica de um candidato
    Optional<SavedJob> findByJobSeekerIdAndJobId(Long jobSeekerId, Long jobId);
    
    // Lista todas as vagas salvas por um candidato
    List<SavedJob> findByJobSeekerId(Long jobSeekerId);
    
    // Lista todas as vagas salvas por um candidato com paginação
    Page<SavedJob> findByJobSeekerId(Long jobSeekerId, Pageable pageable);
    
    // Lista todas as vagas salvas por um candidato com paginação (otimizado com fetch join)
    @Query("SELECT sj FROM SavedJob sj " +
           "LEFT JOIN FETCH sj.job j " +
           "LEFT JOIN FETCH j.recruiter " +
           "WHERE sj.jobSeeker.id = :jobSeekerId")
    Page<SavedJob> findByJobSeekerIdWithJobAndRecruiter(@Param("jobSeekerId") Long jobSeekerId, Pageable pageable);
    
    // Conta quantas vezes uma vaga foi salva
    long countByJobId(Long jobId);
    
    // Conta quantas vagas um candidato salvou
    long countByJobSeekerId(Long jobSeekerId);
    
    // Remove uma vaga salva
    void deleteByJobSeekerIdAndJobId(Long jobSeekerId, Long jobId);
    
    // Remove todas as vagas salvas de um candidato
    void deleteAllByJobSeekerId(Long jobSeekerId);
    
    // Remove todas as ocorrências de uma vaga (quando a vaga é excluída)
    void deleteAllByJobId(Long jobId);
    
    // Verifica se um usuário tem permissão para acessar/modificar uma vaga salva
    @Query("SELECT CASE WHEN COUNT(sj) > 0 THEN true ELSE false END FROM SavedJob sj WHERE sj.id = :savedJobId AND sj.jobSeeker.id = :userId")
    boolean existsByIdAndJobSeekerId(@Param("savedJobId") Long savedJobId, @Param("userId") Long userId);
}
