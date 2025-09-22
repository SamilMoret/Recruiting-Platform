package br.com.one.jobportal.repository;

import br.com.one.jobportal.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobCustomRepository extends JpaRepository<Job, Long> {
    
    @Query("SELECT j FROM Job j WHERE j.isSaved = true AND j.recruiter.id = :recruiterId")
    List<Job> findSavedJobsByRecruiter(@Param("recruiterId") Long recruiterId);
    
    @Query("SELECT j FROM Job j WHERE j.applicationStatus = :status AND j.recruiter.id = :recruiterId")
    List<Job> findJobsByStatusAndRecruiter(
        @Param("status") String status, 
        @Param("recruiterId") Long recruiterId
    );
    
    @Query("SELECT j FROM Job j WHERE j.applicationCount > 0 ORDER BY j.applicationCount DESC")
    List<Job> findMostAppliedJobs();
}
