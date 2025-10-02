package br.com.one.jobportal.service;

import br.com.one.jobportal.dto.ApplyJobRequest;
import br.com.one.jobportal.dto.response.ApplicationResponse;
import br.com.one.jobportal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApplicationService {
    ApplicationResponse applyForJob(ApplyJobRequest request, User applicant);
    
    void withdrawApplication(Long applicationId, User applicant);
    
    ApplicationResponse updateApplicationStatus(Long applicationId, String status, User recruiter);
    
    ApplicationResponse getApplicationById(Long applicationId, User user);
    
    List<ApplicationResponse> getApplicationsByJobSeeker(User jobSeeker);
    
    Page<ApplicationResponse> getApplicationsByJobSeeker(User jobSeeker, Pageable pageable);
    
    List<ApplicationResponse> getApplicationsForEmployer(User employer);
    
    Page<ApplicationResponse> getApplicationsForEmployer(User employer, Pageable pageable);
    
    List<ApplicationResponse> getApplicationsByJobId(Long jobId, User user);
    
    long countApplicationsByJobSeeker(User jobSeeker);
    
    long countApplicationsForEmployer(User employer);
    
    boolean hasApplied(Long jobId, User jobSeeker);
}
