package br.com.one.jobportal.controller;

import br.com.one.jobportal.dto.JobRequest;
import br.com.one.jobportal.entity.EmploymentType;
import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Arrays;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@CrossOrigin
@Transactional(readOnly = true) // Transação somente leitura por padrão
public class JobController {

    private final JobService jobService;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllJobs(
            Specification<Job> spec,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<Job> jobs = jobService.searchJobs(spec, pageable);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            System.err.println("Erro ao buscar vagas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao processar a solicitação");
        }
    }
    
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        try {
            Job job = jobService.getJobById(id);
            return ResponseEntity.ok(job);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao buscar vaga com ID " + id + ": " + e.getMessage());
            return ResponseEntity.internalServerError().body("Erro ao buscar a vaga");
        }
    }
    
    
    @GetMapping("/active")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getActiveJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String employmentType,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            // Cria uma especificação para filtrar apenas vagas ativas (não fechadas)
            Specification<Job> spec = (root, query, criteriaBuilder) -> 
                criteriaBuilder.equal(root.get("isClosed"), false);
            
            // Adiciona filtros adicionais se fornecidos
            if (title != null && !title.trim().isEmpty()) {
                spec = spec.and((root, query, criteriaBuilder) -> 
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")), 
                        "%" + title.toLowerCase() + "%"
                    )
                );
            }
            
            if (location != null && !location.trim().isEmpty()) {
                spec = spec.and((root, query, criteriaBuilder) -> 
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("location")), 
                        "%" + location.toLowerCase() + "%"
                    )
                );
            }
            
            if (employmentType != null && !employmentType.trim().isEmpty()) {
                try {
                    EmploymentType type = EmploymentType.fromString(employmentType);
                    spec = spec.and((root, query, criteriaBuilder) -> 
                        criteriaBuilder.equal(root.get("type"), type)
                    );
                } catch (IllegalArgumentException e) {
                    // Se o tipo não for válido, não aplica o filtro
                    System.err.println("Tipo de emprego inválido: " + employmentType);
                }
            }
            
            Page<Job> jobs = jobService.searchJobs(spec, pageable);
            return ResponseEntity.ok(jobs);
            
        } catch (Exception e) {
            System.err.println("Erro ao buscar vagas ativas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao buscar vagas ativas");
        }
    }
    
    @GetMapping("/employment-types")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getEmploymentTypes() {
        try {
            // Retorna a lista de tipos de emprego disponíveis do enum
            List<EmploymentType> employmentTypes = List.of(EmploymentType.values());
            
            // Mapeia para uma lista de objetos com nome e displayName
            List<Map<String, String>> result = employmentTypes.stream()
                .map(type -> Map.of(
                    "name", type.name(),
                    "displayName", type.getDisplayName()
                ))
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("Erro ao buscar tipos de emprego: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao buscar tipos de emprego");
        }
    }
    
    @GetMapping("/locations")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getJobLocations() {
        try {
            // Retorna uma lista de localizações comuns
            List<String> locations = List.of(
                "São Paulo, SP",
                "Rio de Janeiro, RJ",
                "Belo Horizonte, MG",
                "Brasília, DF",
                "Salvador, BA",
                "Fortaleza, CE",
                "Curitiba, PR",
                "Recife, PE",
                "Porto Alegre, RS",
                "Manaus, AM",
                "Belém, PA",
                "Goiânia, GO",
                "Vitória, ES",
                "Florianópolis, SC",
                "Natal, RN",
                "Aracaju, SE",
                "João Pessoa, PB",
                "Maceió, AL",
                "Teresina, PI",
                "Campo Grande, MS",
                "Cuiabá, MT",
                "Porto Velho, RO",
                "Rio Branco, AC",
                "Macapá, AP",
                "Boa Vista, RR",
                "Palmas, TO",
                "Remoto",
                "Híbrido"
            );
            
            return ResponseEntity.ok(locations);
            
        } catch (Exception e) {
            System.err.println("Erro ao buscar localizações: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao buscar localizações");
        }
    }

    // ===== ENDPOINTS PARA CAMPOS BÁSICOS =====
    
    @GetMapping("/title/{title}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getJobsByTitle(@PathVariable String title) {
        try {
            List<Job> jobs = jobService.getJobsByTitle(title);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar vagas por título: " + e.getMessage());
        }
    }
    
    @GetMapping("/search")
    @Transactional(readOnly = true)
    public ResponseEntity<?> searchInDescriptionOrRequirements(@RequestParam String keyword) {
        try {
            List<Job> jobs = jobService.searchInDescriptionOrRequirements(keyword);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar nas descrições: " + e.getMessage());
        }
    }
    
    @GetMapping("/location/{location}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getJobsByLocation(@PathVariable String location) {
        try {
            List<Job> jobs = jobService.getJobsByLocation(location);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar vagas por localização: " + e.getMessage());
        }
    }
    
    @GetMapping("/category/{category}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getJobsByCategory(@PathVariable String category) {
        try {
            List<Job> jobs = jobService.getJobsByCategory(category);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar vagas por categoria: " + e.getMessage());
        }
    }
    
    // ===== ENDPOINTS PARA TIPO DE EMPREGO =====
    
    @GetMapping("/type/{type}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getJobsByType(@PathVariable EmploymentType type) {
        try {
            List<Job> jobs = jobService.getJobsByType(type);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar vagas por tipo: " + e.getMessage());
        }
    }
    
    // ===== ENDPOINTS PARA FAIXA SALARIAL =====
    
    @GetMapping("/salary-range")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getJobsBySalaryRange(
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max) {
        try {
            List<Job> jobs = jobService.getJobsBySalaryRange(min, max);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar vagas por faixa salarial: " + e.getMessage());
        }
    }
    
    // ===== ENDPOINTS PARA STATUS DA VAGA =====
    
    @GetMapping("/status/{isClosed}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getJobsByStatus(@PathVariable boolean isClosed) {
        try {
            List<Job> jobs = jobService.getJobsByStatus(isClosed);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar vagas por status: " + e.getMessage());
        }
    }
    
    // O método getActiveJobs com parâmetros já cobre o caso sem parâmetros
    
    // ===== ENDPOINTS PARA VAGA SALVA =====
    
    @PostMapping("/{id}/toggle-save")
    @Transactional
    public ResponseEntity<?> toggleSaveJob(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            Job job = jobService.toggleSaveStatus(id, user);
            return ResponseEntity.ok(job);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar status de salvamento: " + e.getMessage());
        }
    }
    
    @GetMapping("/saved")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getSavedJobs(@AuthenticationPrincipal User user) {
        try {
            List<Job> jobs = jobService.getSavedJobs(user);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar vagas salvas: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/is-saved")
    @Transactional(readOnly = true)
    public ResponseEntity<?> isJobSaved(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            boolean isSaved = jobService.isJobSaved(id, user);
            return ResponseEntity.ok(Map.of("isSaved", isSaved));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao verificar se a vaga está salva: " + e.getMessage());
        }
    }
    
    // ===== ENDPOINTS PARA RELACIONAMENTO COM RECRUTADOR =====
    
    @GetMapping("/recruiter")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getRecruiterJobs(@AuthenticationPrincipal User recruiter) {
        try {
            if (recruiter == null) {
                return ResponseEntity.status(401).body("Usuário não autenticado");
            }

            List<Job> jobs = jobService.getJobsByRecruiter(recruiter);
            return ResponseEntity.ok(jobs);
            } catch (EntityNotFoundException e) {
                return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar vagas do recrutador: " + e.getMessage());
        }
    }
    
    // ===== ENDPOINTS PARA STATUS DA CANDIDATURA =====
    
    @PutMapping("/{id}/application-status")
    @Transactional
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal User user) {
        try {
            Job job = jobService.updateApplicationStatus(id, status, user);
            return ResponseEntity.ok(job);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar status da candidatura: " + e.getMessage());
        }
    }
    
    @GetMapping("/application-status/{status}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getJobsByApplicationStatus(@PathVariable String status) {
        try {
            List<Job> jobs = jobService.getJobsByApplicationStatus(status);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar vagas por status de candidatura: " + e.getMessage());
        }
    }
    
    @GetMapping("/recruiter/{recruiterId}/application-status/{status}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getJobsByApplicationStatusAndRecruiter(
            @PathVariable Long recruiterId,
            @PathVariable String status) {
        try {
            User recruiter = new User();
            recruiter.setId(recruiterId);
            List<Job> jobs = jobService.getJobsByApplicationStatusAndRecruiter(status, recruiter);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar vagas por status e recrutador: " + e.getMessage());
        }
    }
    
    // ===== ENDPOINTS PARA CONTAGEM DE CANDIDATURAS =====
    
    @PostMapping("/{id}/increment-application")
    @Transactional
    public ResponseEntity<?> incrementApplicationCount(@PathVariable Long id) {
        try {
            jobService.incrementApplicationCount(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao incrementar contador de candidaturas: " + e.getMessage());
        }
    }
    
    @GetMapping("/most-applied")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getMostAppliedJobs() {
        try {
            List<Job> jobs = jobService.getMostAppliedJobs();
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar vagas mais candidatadas: " + e.getMessage());
        }
    }
    
    // ===== ENDPOINTS PARA AUDITORIA =====
    
    @GetMapping("/created-after")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getJobsCreatedAfter(@RequestParam String date) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date);
            List<Job> jobs = jobService.getJobsCreatedAfter(dateTime);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar vagas criadas após a data: " + e.getMessage());
        }
    }
    
    @GetMapping("/updated-after")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getJobsUpdatedAfter(@RequestParam String date) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date);
            List<Job> jobs = jobService.getJobsUpdatedAfter(dateTime);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar vagas atualizadas após a data: " + e.getMessage());
        }
    }
    
    // ===== MÉTODOS DE ATUALIZAÇÃO =====
    
    @PutMapping("/{id}/status")
    @Transactional
    public ResponseEntity<?> updateJobStatus(
            @PathVariable Long id,
            @RequestParam boolean active,
            @AuthenticationPrincipal User user) {
        try {
            Job job = jobService.updateJobStatus(id, user, active);
            return ResponseEntity.ok(job);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar status da vaga: " + e.getMessage());
        }
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> createJob(@Valid @RequestBody JobRequest jobRequest, @AuthenticationPrincipal User recruiter) {
        if (recruiter == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        if (jobRequest == null) {
            return ResponseEntity.badRequest().body("Dados da vaga não fornecidos");
        }

        try {
            Job job = convertToEntity(jobRequest);
            Job createdJob = jobService.createJob(job, recruiter.getEmail());
            return ResponseEntity.status(201).body(createdJob);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao criar vaga: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao processar a solicitação");
        }
    }
    
    private Job convertToEntity(JobRequest jobRequest) {
        Job job = new Job();
        job.setTitle(jobRequest.getTitle());
        job.setDescription(jobRequest.getDescription());
        job.setRequirements(jobRequest.getRequirements());
        job.setLocation(jobRequest.getLocation());
        
        // Handle employmentType conversion safely
        if (jobRequest.getEmploymentType() != null) {
            try {
                job.setType(jobRequest.getEmploymentType());
            } catch (IllegalArgumentException e) {
                // If the value is not a valid enum constant, log a warning and set a default value
                job.setType(EmploymentType.FULL_TIME);
            }
        } else {
            // Set a default value if employmentType is null
            job.setType(EmploymentType.FULL_TIME);
        }
        
        job.setSalaryMin(jobRequest.getSalaryMin() != null ? jobRequest.getSalaryMin().intValue() : null);
        job.setSalaryMax(jobRequest.getSalaryMax() != null ? jobRequest.getSalaryMax().intValue() : null);
        job.setClosed(!Boolean.TRUE.equals(jobRequest.getActive()));
        return job;
    }

    @PutMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> updateJob(@PathVariable Long id, @Valid @RequestBody JobRequest jobRequest,
                                     @AuthenticationPrincipal User recruiter) {
        try {
            if (recruiter == null) {
                return ResponseEntity.status(401).body("Usuário não autenticado");
            }

            if (jobRequest == null) {
                return ResponseEntity.badRequest().body("Dados da vaga não fornecidos");
            }

            try {
                Job updatedJob = jobService.updateJob(id, jobRequest, recruiter);
                return ResponseEntity.ok(updatedJob);
            } catch (EntityNotFoundException e) {
                return ResponseEntity.status(404).body(e.getMessage());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (SecurityException e) {
                return ResponseEntity.status(403).body(e.getMessage());
            } catch (Exception e) {
                System.err.println("Erro ao atualizar vaga: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.internalServerError().body("Erro ao processar a solicitação");
            }
        } catch (Exception e) {
            System.err.println("Erro inesperado ao processar requisição: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno do servidor");
        }
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> deleteJob(@PathVariable Long id, @AuthenticationPrincipal User recruiter) {
        try {
            if (recruiter == null) {
                return ResponseEntity.status(401).body("Usuário não autenticado");
            }

            try {
                jobService.deleteJob(id, recruiter);
                return ResponseEntity.noContent().build();
            } catch (EntityNotFoundException e) {
                return ResponseEntity.status(404).body(e.getMessage());
            } catch (SecurityException e) {
                return ResponseEntity.status(403).body(e.getMessage());
            } catch (Exception e) {
                System.err.println("Erro ao deletar vaga: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.internalServerError().body("Erro ao processar a solicitação");
            }
        } catch (Exception e) {
            System.err.println("Erro inesperado ao processar requisição: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno do servidor");
        }
    }

    @PatchMapping("/{id}/status")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> toggleJobStatus(
            @PathVariable Long id,
            @RequestParam boolean active,
            @AuthenticationPrincipal User recruiter) {
        try {
            if (recruiter == null) {
                return ResponseEntity.status(401).body("Usuário não autenticado");
            }

            try {
                // Usando o método updateJobStatus que aceita os parâmetros corretos
                Job job = jobService.updateJobStatus(id, recruiter, active);
                return ResponseEntity.ok(job);
            } catch (EntityNotFoundException e) {
                return ResponseEntity.status(404).body(e.getMessage());
            } catch (SecurityException e) {
                return ResponseEntity.status(403).body(e.getMessage());
            } catch (Exception e) {
                System.err.println("Erro ao alterar status da vaga: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.internalServerError().body("Erro ao processar a solicitação");
            }
        } catch (Exception e) {
            System.err.println("Erro inesperado ao processar requisição: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno do servidor");
        }
    }
}