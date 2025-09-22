package br.com.one.jobportal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveJobRequest {
    
    @NotNull(message = "ID da vaga é obrigatório")
    private Long jobId;
    
    // O jobSeekerId será obtido do token JWT, então não é necessário no request
}
