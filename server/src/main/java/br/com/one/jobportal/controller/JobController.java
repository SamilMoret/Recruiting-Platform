package br.com.one.jobportal.controller;

import br.com.one.jobportal.dto.JobRequest;
import br.com.one.jobportal.dto.response.JobResponse;
import br.com.one.jobportal.entity.EmploymentType;
import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.service.JobService;
import br.com.one.jobportal.service.SavedJobService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@CrossOrigin
@Transactional(readOnly = true)
public class JobController {

    private final JobService jobService;
    private final SavedJobService savedJobService;

    @PostMapping
    @Transactional
    public ResponseEntity<?> createJob(@Valid @RequestBody JobRequest jobRequest, 
                                     @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Usuário não autenticado"));
        }

        if (jobRequest == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Dados da vaga não fornecidos"));
        }

        try {
            Job job = convertToEntity(jobRequest);
            job.setRecruiter(user);
            Job createdJob = jobService.saveJob(job);
            JobResponse response = JobResponse.fromEntity(createdJob);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Erro ao processar a solicitação: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            Job job = jobService.getJobById(id);
            boolean isSaved = user != null && savedJobService.isJobSavedByUser(user.getId(), id);
            return ResponseEntity.ok(JobResponse.fromEntity(job, isSaved));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getActiveJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String employmentType,
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal User user) {
        try {
            // Filtro para buscar vagas ativas
            Specification<Job> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.isFalse(root.get("isClosed")));
                
                if (title != null && !title.isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
                }
                if (location != null && !location.isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
                }
                if (employmentType != null && !employmentType.isEmpty()) {
                    try {
                        EmploymentType type = EmploymentType.fromString(employmentType);
                        predicates.add(cb.equal(root.get("type"), type));
                    } catch (IllegalArgumentException e) {
                        // Tipo de emprego inválido, não filtra por tipo
                    }
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            };
            
            Page<Job> jobs = jobService.searchJobs(spec, pageable);
            
            // Mapeia as vagas para incluir a informação de se estão salvas para o usuário atual
            Page<JobResponse> response = jobs.map(job -> {
                boolean isSaved = user != null && savedJobService.isJobSavedByUser(user.getId(), job.getId());
                return JobResponse.fromEntity(job, isSaved);
            });
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Erro ao buscar vagas: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateJob(@PathVariable Long id,
                                     @Valid @RequestBody JobRequest jobRequest,
                                     @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Usuário não autenticado"));
            }
            
            // Cria um novo objeto Job com os dados atualizados
            Job jobUpdates = convertToEntity(jobRequest);
            
            // Chama o serviço para atualizar a vaga
            Job updatedJob = jobService.updateJob(id, jobUpdates, user);
            return ResponseEntity.ok(JobResponse.fromEntity(updatedJob));
            
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Erro ao atualizar vaga: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteJob(@PathVariable Long id,
                                     @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Usuário não autenticado"));
            }
            
            jobService.deleteJob(id, user);
            return ResponseEntity.noContent().build();
            
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Erro ao excluir vaga: " + e.getMessage()));
        }
    }

    private Job convertToEntity(JobRequest jobRequest) {
        return Job.builder()
                .title(jobRequest.getTitle())
                .description(jobRequest.getDescription())
                .requirements(jobRequest.getRequirements())
                .location(jobRequest.getLocation())
                .category(jobRequest.getCategory())
                .type(EmploymentType.fromString(jobRequest.getType()))
                .salaryMin(jobRequest.getSalaryMin())
                .salaryMax(jobRequest.getSalaryMax())
                .isClosed(false)
                .isSaved(false)
                .applicationCount(0)
                .build();
    }
}
