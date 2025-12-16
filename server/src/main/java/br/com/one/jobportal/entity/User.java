package br.com.one.jobportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('JOB_SEEKER', 'EMPLOYER', 'ADMIN') DEFAULT 'JOB_SEEKER'")
    private Role role = Role.JOB_SEEKER;

    private String avatar;
    private String resume;
    private String phone;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_description", columnDefinition = "TEXT")
    private String companyDescription;

    @Column(name = "company_logo")
    private String companyLogo;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relacionamentos
    @OneToMany(mappedBy = "recruiter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> postedJobs = new ArrayList<>();

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedJob> savedJobs = new ArrayList<>();

    public enum Role {
        JOB_SEEKER, EMPLOYER, ADMIN;

        private static final Map<String, Role> NAME_MAP = new HashMap<>();

        static {
            for (Role role : values()) {
                NAME_MAP.put(role.name(), role);
                NAME_MAP.put(role.name().toLowerCase(), role);
                NAME_MAP.put(role.name().toUpperCase(), role);
                NAME_MAP.put(role.name().replace("_", "").toLowerCase(), role);
            }
            NAME_MAP.put("jobseeker", JOB_SEEKER);
            NAME_MAP.put("employer", EMPLOYER);
            NAME_MAP.put("admin", ADMIN);
        }

        public static Role fromString(String role) {
            if (role == null) {
                return JOB_SEEKER;
            }
            String key = role.trim().toLowerCase();
            return NAME_MAP.getOrDefault(key, JOB_SEEKER);
        }

        public String getAuthority() {
            return "ROLE_" + this.name();
        }
    }

    // Implementação dos métodos do UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority(role.getAuthority()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    // Métodos auxiliares
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isEmployer() {
        return this.role == Role.EMPLOYER;
    }

    public boolean isJobSeeker() {
        return this.role == Role.JOB_SEEKER;
    }
}