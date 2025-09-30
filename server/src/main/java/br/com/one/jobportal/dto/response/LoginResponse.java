package br.com.one.jobportal.dto.response;

import br.com.one.jobportal.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
    "id",
    "name",
    "email",
    "role",
    "avatar",
    "companyName",
    "companyDescription",
    "companyLogo",
    "resume",
    "token"
})
public class LoginResponse {
    private Long id;
    private String name;
    private String email;
    @JsonProperty("role")
    private String roleName;
    private String avatar;
    private String token;
    
    // Campos específicos para EMPLOYER
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String companyName;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String companyDescription;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String companyLogo;
    
    // Campo específico para JOB_SEEKER
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resume;
    
    public static LoginResponse fromUserAndToken(User user, String token) {
        // Converte o role para minúsculas e remove underlines
        String role = user.getRole().name().toLowerCase().replace("_", "");
        
        LoginResponseBuilder builder = LoginResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roleName(role)
                .avatar(user.getAvatar())
                .token(token);
                
        // Adiciona campos específicos baseados no tipo de usuário
        if (user.getRole() == User.Role.EMPLOYER) {
            builder.companyName(user.getCompanyName())
                   .companyDescription(user.getCompanyDescription())
                   .companyLogo(user.getCompanyLogo())
                   .resume(null); // Garante que resume seja null para EMPLOYER
        } else if (user.getRole() == User.Role.JOB_SEEKER) {
            builder.resume(user.getResume())
                   .companyName(null)
                   .companyDescription(null)
                   .companyLogo(null); // Garante que campos de empresa sejam null para JOB_SEEKER
        }
        
        return builder.build();
    }
}
