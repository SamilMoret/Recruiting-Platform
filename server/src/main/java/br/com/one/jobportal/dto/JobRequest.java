package br.com.one.jobportal.dto;

import br.com.one.jobportal.entity.EmploymentType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    @NotBlank(message = "Requisitos são obrigatórios")
    private String requirements;

    @NotBlank(message = "Localização é obrigatória")
    private String location;

    @NotNull(message = "Tipo de emprego é obrigatório")
    @JsonProperty("employmentType")
    private EmploymentType employmentType;

    @NotNull(message = "Salário mínimo é obrigatório")
    @Positive(message = "Salário mínimo deve ser positivo")
    private Double salaryMin;

    @NotNull(message = "Salário máximo é obrigatório")
    @Positive(message = "Salário máximo deve ser positivo")
    private Double salaryMax;

    @NotNull(message = "Status é obrigatório")
    private Boolean active;
    
    // Add this method to help with form data binding
    public void setEmploymentType(String employmentType) {
        if (employmentType != null) {
            this.employmentType = EmploymentType.fromString(employmentType);
        }
    }
}
