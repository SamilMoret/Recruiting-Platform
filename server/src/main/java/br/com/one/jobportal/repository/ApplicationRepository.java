package br.com.one.jobportal.repository;

import br.com.one.jobportal.entity.Application;
import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByCandidate(User candidate);
    List<Application> findByJob(Job job);
    List<Application> findByJobRecruiter(User recruiter);
    Optional<Application> findByCandidateAndJob(User candidate, Job job);
    Long countByJob(Job job);
    Long countByCandidate(User candidate);
}
