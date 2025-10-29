package br.com.one.jobportal.dto.response;

import br.com.one.jobportal.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String avatar;
    private String resume;
    private String phone;
    private String companyName;
    private String companyDescription;
    private String companyLogo;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserProfileResponse fromEntity(User user) {
        if (user == null) {
            return null;
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .avatar(user.getAvatar())
                .resume(user.getResume())
                .phone(user.getPhone())
                .companyName(user.getCompanyName())
                .companyDescription(user.getCompanyDescription())
                .companyLogo(user.getCompanyLogo())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
