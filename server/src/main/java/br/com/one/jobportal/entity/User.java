package br.com.one.jobportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.Convert;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CascadeType;

@Data
@Entity
@Table(name = "`user`")
public class User implements UserDetails {

    // ENUMS CORRESPONDENTES AO BANCO DE DADOS
    public enum Role {
        JOB_SEEKER, EMPLOYER, ADMIN;

        private static final Map<String, Role> NAME_MAP = new HashMap<>();

        static {
            // Mapeia variações de nomes para os valores do enum
            for (Role role : values()) {
                // Mapeia o nome do enum (ex: JOB_SEEKER)
                NAME_MAP.put(role.name(), role);
                // Mapeia em minúsculas (ex: job_seeker)
                NAME_MAP.put(role.name().toLowerCase(), role);
                // Mapeia em maiúsculas (ex: JOB_SEEKER)
                NAME_MAP.put(role.name().toUpperCase(), role);
                // Mapeia sem underline (ex: jobseeker)
                NAME_MAP.put(role.name().replace("_", "").toLowerCase(), role);
            }
            // Adiciona aliases comuns
            NAME_MAP.put("jobseeker", JOB_SEEKER);
            NAME_MAP.put("employer", EMPLOYER);
            NAME_MAP.put("admin", ADMIN);
        }

        public static Role fromString(String role) {
            if (role == null) {
                return JOB_SEEKER; // Valor padrão
            }
            // Remove espaços e converte para minúsculas para fazer a busca
            String key = role.trim().toLowerCase();
            return NAME_MAP.getOrDefault(key, JOB_SEEKER);
        }

        public String getAuthority() {
            return "ROLE_" + this.name();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Convert(converter = br.com.one.jobportal.converter.RoleConverter.class)
    @Column(nullable = false)
    private Role role = Role.JOB_SEEKER;  // ← valor padrão

    private String avatar;
    private String resume;
    private String companyName;
    private String companyDescription;
    private String companyLogo;
    private String phone;

    @OneToMany(mappedBy = "applicant", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> postedJobs = new ArrayList<>();

    @OneToMany(mappedBy = "jobSeeker", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedJob> savedJobs = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private Analytics analytics;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private boolean active = true;

    // ✅ MÉTODOS OBRIGATÓRIOS da interface UserDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna uma lista contendo a autoridade no formato 'ROLE_<NOME_DA_ROLE>'
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email; // O Spring Security usa o email como username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Conta nunca expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Conta nunca é bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Credenciais nunca expiram
    }

    @Override
    public boolean isEnabled() {
        return active; // Usa o campo 'active' para determinar se usuário está ativo
    }

    // MÉTODOS ÚTEIS PARA VERIFICAÇÃO DE ROLES
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