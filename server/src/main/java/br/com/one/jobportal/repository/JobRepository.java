package br.com.one.jobportal.repository;

import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    List<Job> findByRecruiter(User recruiter);
    List<Job> findByActiveTrue();
    List<Job> findByCompanyContainingIgnoreCase(String company);
    Long countByRecruiter(User recruiter);
}