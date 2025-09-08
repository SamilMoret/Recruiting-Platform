package br.com.one.jobportal.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    private String password;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^(?:\\(\\d{2}\\)\\s?|\\d{2}[\\s-]?)(?:\\d{4,5}[-\\s]?\\d{4}|9[1-9]\\d{3}[-\\s]?\\d{4})$", 
             message = "Telefone inválido. Use os formatos: (XX) XXXXX-XXXX, (XX) XXXX-XXXX, XX XXXXXXXX, XX XXXXXXXXX, XX-XXXXXXXX, XX-XXXX-XXXX")
    private String phone;
}