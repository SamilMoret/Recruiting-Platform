package br.com.one.jobportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "applications")
public class Application {

    public enum Status {
        PENDING, REVIEWING, INTERVIEW, REJECTED, ACCEPTED, WITHDRAWN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private User candidate;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    private String resumePath; // CV específico para esta aplicação

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime statusChangedAt;

    @Column(columnDefinition = "TEXT")
    private String notes; // Notas do recrutador
}