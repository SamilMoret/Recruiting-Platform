package br.com.one.jobportal.service;

import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface JobService {
    Job createJob(Job job, User recruiter);
    Job updateJob(Long id, Job jobDetails, User recruiter);
    void deleteJob(Long id, User recruiter);
    Job getJobById(Long id);
    List<Job> getJobsByRecruiter(User recruiter);
    Page<Job> searchJobs(Specification<Job> spec, Pageable pageable);
    Long getJobCountByRecruiter(User recruiter);
    Job toggleJobStatus(Long id, User recruiter, boolean active);
}