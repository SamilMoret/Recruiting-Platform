package br.com.one.jobportal.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SavedJobResponse {
    
    private Long id;
    private Long jobId;
    private Long jobSeekerId;
    private String jobTitle;
    private String companyName;
    private String location;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime savedAt;
    
    // Campos adicionais da vaga que podem ser úteis
    private String employmentType;
    private String jobCategory;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime applicationDeadline;
}
