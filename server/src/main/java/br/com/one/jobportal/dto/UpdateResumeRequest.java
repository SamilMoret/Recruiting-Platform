package br.com.one.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResumeRequest {
    
    @NotBlank(message = "O link para o currículo é obrigatório")
    private String resumeUrl;
}
