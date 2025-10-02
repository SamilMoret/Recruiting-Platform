package br.com.one.jobportal.dto.response;

import br.com.one.jobportal.entity.Application;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long candidateId;
    private String candidateName;
    private String resumeUrl;
    private String coverLetter;
    private String status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    private String companyName;
    private String location;
    private String employmentType;
    private String jobCategory;

    public static ApplicationResponse fromEntity(Application application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setJobId(application.getJob().getId());
        response.setJobTitle(application.getJob().getTitle());
        response.setCandidateId(application.getApplicant().getId());
        response.setCandidateName(application.getApplicant().getName());
        response.setResumeUrl(application.getResume());
        response.setCoverLetter(application.getCoverLetter());
        response.setStatus(application.getStatus().name());
        response.setAppliedAt(application.getCreatedAt());
        response.setUpdatedAt(application.getUpdatedAt());
        
        // Job details
        if (application.getJob().getRecruiter() != null) {
            response.setCompanyName(application.getJob().getRecruiter().getCompanyName());
        }
        response.setLocation(application.getJob().getLocation());
        response.setEmploymentType(application.getJob().getType() != null ? application.getJob().getType().name() : null);
        response.setJobCategory(application.getJob().getCategory());
        
        return response;
    }
}
