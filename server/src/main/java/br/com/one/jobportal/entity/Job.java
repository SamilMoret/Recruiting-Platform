package br.com.one.jobportal.entity;

import br.com.one.jobportal.entity.EmploymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @Size(max = 255)
    @Column(length = 255)
    private String location;

    @Size(max = 255)
    @Column(length = 255)
    private String category;

    @Convert(converter = br.com.one.jobportal.converter.EmploymentTypeConverter.class)
    @Column(columnDefinition = "ENUM('Full-Time', 'Part-Time', 'Contract', 'Remote', 'Internship') DEFAULT 'Full-Time'")
    private EmploymentType type = EmploymentType.FULL_TIME;

    @Column(name = "salary_min")
    private Integer salaryMin;

    @Column(name = "salary_max")
    private Integer salaryMax;

    @Column(name = "is_closed", nullable = false)
    private boolean isClosed = false;

    @Column(name = "is_saved", nullable = false)
    private Boolean isSaved = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recruiter_id", nullable = false, foreignKey = @ForeignKey(name = "fk_job_recruiter"))
    private User recruiter;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();


    @Column(name = "application_status", length = 50)
    private String applicationStatus = null;

    @Column(name = "application_count", nullable = false)
    private Integer applicationCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    }
