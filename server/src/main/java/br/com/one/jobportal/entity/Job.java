package br.com.one.jobportal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "jobs")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String title;

    @NotBlank
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotBlank
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String requirements;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String location;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String category;

    @Convert(converter = EmploymentTypeConverter.class)
    @Column(nullable = false)
    private EmploymentType type;

    @Column(name = "salary_min", nullable = false)
    private Integer salaryMin;

    @Column(name = "salary_max", nullable = false)
    private Integer salaryMax;

    @Column(name = "is_closed", nullable = false)
    private Boolean isClosed = false;

    @Column(name = "is_saved", nullable = false)
    private Boolean isSaved = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    @JsonIgnore
    private User recruiter;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Application> applications = new ArrayList<>();

    @Column(name = "application_status", length = 50)
    private String applicationStatus;

    @Column(name = "application_count", nullable = false)
    private Integer applicationCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Helper method to get company info from recruiter
    public CompanyInfo getCompany() {
        if (recruiter == null) {
            return null;
        }
        return CompanyInfo.builder()
                .id(recruiter.getId())
                .name(recruiter.getCompanyName())
                .companyName(recruiter.getCompanyName())
                .companyLogo(recruiter.getCompanyLogo())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyInfo {
        private Long id;
        private String name;
        private String companyName;
        private String companyLogo;
    }
}
