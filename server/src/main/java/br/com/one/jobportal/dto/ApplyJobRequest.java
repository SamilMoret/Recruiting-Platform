package br.com.one.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplyJobRequest {
    @NotNull(message = "Job ID is required")
    private Long jobId;
    
    @NotBlank(message = "Cover letter is required")
    private String coverLetter;
    
    private String resumeUrl;
}
