package br.com.one.jobportal.dto;

import br.com.one.jobportal.entity.EmploymentType;
import br.com.one.jobportal.entity.ExperienceLevel;
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

    @NotBlank(message = "Empresa é obrigatória")
    private String company;

    @NotBlank(message = "Localização é obrigatória")
    private String location;

    @NotNull(message = "Tipo de emprego é obrigatório")
    private EmploymentType employmentType;

    @NotNull(message = "Nível de experiência é obrigatório")
    private ExperienceLevel experienceLevel;

    @NotNull(message = "Salário mínimo é obrigatório")
    @Positive(message = "Salário mínimo deve ser positivo")
    private Double salaryMin;

    @NotNull(message = "Salário máximo é obrigatório")
    @Positive(message = "Salário máximo deve ser positivo")
    private Double salaryMax;

    @NotBlank(message = "Moeda é obrigatória")
    private String salaryCurrency;

    @NotNull(message = "Status é obrigatório")
    private Boolean active;
}
