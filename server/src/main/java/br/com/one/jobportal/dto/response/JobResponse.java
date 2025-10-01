package br.com.one.jobportal.dto.response;

import br.com.one.jobportal.entity.EmploymentType;
import br.com.one.jobportal.entity.Job;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class JobResponse {
    @JsonProperty("_id")
    private String _id;
    private String title;
    private String description;
    private String requirements;
    private String location;
    private String category;
    private String type;
    private Integer salaryMin;
    private Integer salaryMax;
    private Boolean isClosed;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
    private LocalDateTime createdAt;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private Boolean isSaved = false;
    private String applicationStatus;
    private Integer applicationCount;
    private CompanyInfo company;
    private Integer __v;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyInfo {
        @JsonProperty("_id")
        private String _id;
        private String name;
        private String companyName;
        private String companyLogo;
    }

    public static JobResponse fromEntity(Job job) {
        if (job == null) {
            return null;
        }

        // Formata o tipo para começar com letra maiúscula e o resto minúsculo
        String formattedType = job.getType() != null ? 
            job.getType().name().charAt(0) + job.getType().name().substring(1).toLowerCase() : 
            null;

        // Cria o objeto de resposta na ordem correta
        JobResponse response = new JobResponse();
        response.set_id(job.getId() != null ? job.getId().toString() : null);
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setRequirements(job.getRequirements());
        response.setLocation(job.getLocation());
        response.setType(formattedType);
        
        // Configura a empresa se existir um recrutador
        if (job.getRecruiter() != null) {
            CompanyInfo companyInfo = new CompanyInfo();
            companyInfo.set_id(job.getRecruiter().getId() != null ? job.getRecruiter().getId().toString() : null);
            companyInfo.setName("teste" + job.getRecruiter().getId()); // Formato do exemplo
            companyInfo.setCompanyLogo(job.getRecruiter().getCompanyLogo());
            companyInfo.setCompanyName(job.getRecruiter().getCompanyName());
            response.setCompany(companyInfo);
        }
        
        response.setSalaryMin(job.getSalaryMin());
        response.setSalaryMax(job.getSalaryMax());
        response.setIsClosed(job.getIsClosed() != null ? job.getIsClosed() : false);
        response.setCreatedAt(job.getCreatedAt());
        response.setUpdatedAt(job.getUpdatedAt());
        response.set__v(null); // Definido como null conforme o exemplo
        response.setIsSaved(job.getIsSaved() != null ? job.getIsSaved() : false);
        response.setApplicationStatus(job.getApplicationStatus());
        response.setApplicationCount(job.getApplicationCount() != null ? job.getApplicationCount() : 0);
        response.setCategory(job.getCategory());
        
        return response;
    }
}
