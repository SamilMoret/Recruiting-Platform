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
import org.springframework.security.access.prepost.PreAuthorize;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@CrossOrigin
@Transactional(readOnly = true)
@Tag(name = "Vagas", description = "Gerenciamento de vagas de emprego")
public class JobController {

    private final JobService jobService;

    @Operation(summary = "Listar vagas com filtros",
               description = "Retorna uma lista paginada de vagas com possibilidade de filtros",
               parameters = {
                   @Parameter(name = "title", description = "Título da vaga (contendo o texto)", example = "desenvolvedor"),
                   @Parameter(name = "location", description = "Localização da vaga", example = "São Paulo"),
                   @Parameter(name = "employmentType", description = "Tipo de contrato (FULL_TIME, PART_TIME, CONTRACT, TEMPORARY, INTERNSHIP, VOLUNTEER)", example = "FULL_TIME"),
                   @Parameter(name = "minSalary", description = "Salário mínimo", example = "3000"),
                   @Parameter(name = "maxSalary", description = "Salário máximo", example = "10000"),
                   @Parameter(name = "page", description = "Número da página (0-based)", example = "0"),
                   @Parameter(name = "size", description = "Tamanho da página", example = "10"),
                   @Parameter(name = "sort", description = "Ordenação (ex: title,asc)", example = "title,asc")
               },
               responses = {
                   @ApiResponse(responseCode = "200", description = "Lista de vagas retornada com sucesso"),
                   @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
               })
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
    
    @Operation(summary = "Obter vaga por ID",
               description = "Retorna os detalhes de uma vaga específica pelo seu ID",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Vaga encontrada com sucesso"),
                   @ApiResponse(responseCode = "404", description = "Vaga não encontrada"),
                   @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
               })
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
    
    @Operation(summary = "Listar vagas do empregador",
               description = "Retorna as vagas criadas pelo empregador autenticado",
               security = @SecurityRequirement(name = "bearerAuth"),
               responses = {
                   @ApiResponse(responseCode = "200", description = "Lista de vagas retornada com sucesso"),
                   @ApiResponse(responseCode = "401", description = "Não autorizado"),
                   @ApiResponse(responseCode = "403", description = "Acesso negado")
               })
    @GetMapping("/employer/my-jobs")
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> getEmployerJobs(@AuthenticationPrincipal User recruiter) {
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
                    .body("Erro ao buscar vagas do empregador: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Criar nova vaga",
               description = "Cria uma nova vaga de emprego (apenas para empregadores)",
               security = @SecurityRequirement(name = "bearerAuth"),
               responses = {
                   @ApiResponse(responseCode = "201", description = "Vaga criada com sucesso"),
                   @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
                   @ApiResponse(responseCode = "401", description = "Não autorizado"),
                   @ApiResponse(responseCode = "403", description = "Acesso negado")
               })
    private Job convertToEntity(JobRequest jobRequest) {
        if (jobRequest == null) {
            return null;
        }
        
        Job job = new Job();
        job.setTitle(jobRequest.getTitle());
        job.setDescription(jobRequest.getDescription());
        job.setRequirements(jobRequest.getRequirements());
        job.setLocation(jobRequest.getLocation());
        job.setType(jobRequest.getEmploymentType());
        job.setSalaryMin(jobRequest.getSalaryMin());
        job.setSalaryMax(jobRequest.getSalaryMax());
        job.setIsClosed(jobRequest.getActive() != null ? !jobRequest.getActive() : false);
        
        return job;
    }
    
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> createJob(@Valid @RequestBody JobRequest jobRequest, @AuthenticationPrincipal User recruiter) {
        if (recruiter == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        if (jobRequest == null) {
            return ResponseEntity.badRequest().body("Dados da vaga não fornecidos");
        }

        try {
            Job job = convertToEntity(jobRequest);
            job.setRecruiter(recruiter);
            Job createdJob = jobService.saveJob(job);
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
    
    @Operation(summary = "Atualizar vaga",
               description = "Atualiza os dados de uma vaga existente (apenas o criador da vaga pode atualizar)",
               security = @SecurityRequirement(name = "bearerAuth"),
               responses = {
                   @ApiResponse(responseCode = "200", description = "Vaga atualizada com sucesso"),
                   @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
                   @ApiResponse(responseCode = "401", description = "Não autorizado"),
                   @ApiResponse(responseCode = "403", description = "Acesso negado"),
                   @ApiResponse(responseCode = "404", description = "Vaga não encontrada")
               })
    @PutMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('EMPLOYER')")
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
                // Convertir JobRequest a Job
                Job job = new Job();
                job.setTitle(jobRequest.getTitle());
                job.setDescription(jobRequest.getDescription());
                job.setRequirements(jobRequest.getRequirements());
                job.setLocation(jobRequest.getLocation());
                job.setType(jobRequest.getEmploymentType());
                job.setSalaryMin(jobRequest.getSalaryMin());
                job.setSalaryMax(jobRequest.getSalaryMax());
                job.setIsClosed(!jobRequest.getActive());
                
                Job updatedJob = jobService.updateJob(id, job, recruiter);
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

    @Operation(summary = "Excluir vaga",
               description = "Remove uma vaga do sistema (apenas o criador da vaga pode excluir)",
               security = @SecurityRequirement(name = "bearerAuth"),
               responses = {
                   @ApiResponse(responseCode = "204", description = "Vaga excluída com sucesso"),
                   @ApiResponse(responseCode = "401", description = "Não autorizado"),
                   @ApiResponse(responseCode = "403", description = "Acesso negado"),
                   @ApiResponse(responseCode = "404", description = "Vaga não encontrada")
               })
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('EMPLOYER')")
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
                // Get the existing job
                Job job = jobService.getJobById(id);
                // Update the isClosed status (inverse of active)
                job.setIsClosed(!active);
                // Save the updated job
                Job updatedJob = jobService.saveJob(job);
                return ResponseEntity.ok(updatedJob);
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