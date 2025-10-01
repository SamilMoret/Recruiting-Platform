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

    @NotBlank(message = "Categoria é obrigatória")
    private String category;

    @NotBlank(message = "Tipo de emprego é obrigatório")
    private String type;

    @NotNull(message = "Salário mínimo é obrigatório")
    @Positive(message = "Salário mínimo deve ser positivo")
    private Integer salaryMin;

    @NotNull(message = "Salário máximo é obrigatório")
    @Positive(message = "Salário máximo deve ser positivo")
    private Integer salaryMax;

    // Default value for active status
    private Boolean active = true;

    // Helper method to get EmploymentType
    public EmploymentType getEmploymentType() {
        return type != null ? EmploymentType.fromString(type) : null;
    }
}
