package br.com.one.jobportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String name;
    
    @Pattern(regexp = "^\\(?:(?:\\d{2})\\)\\s?\\d{4,5}-?\\d{4}$", 
             message = "Telefone inválido. Use o formato (XX) XXXXX-XXXX")
    private String phone;
    
    private String avatar;
    
    @Size(max = 500, message = "A biografia não pode ter mais que 500 caracteres")
    private String bio;
    
    // Apenas para empregadores
    private String companyName;
    private String companyDescription;
    private String companyLogo;
    private String website;
    
    // Apenas para candidatos
    private String title; // Cargo atual
    private String resume; // Currículo em texto ou base64
    private String skills; // Habilidades separadas por vírgula
}
