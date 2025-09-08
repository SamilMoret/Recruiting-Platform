package br.com.one.jobportal.controller;

import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;

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
            // Cria uma especificação para filtrar apenas vagas ativas
            Specification<Job> spec = (root, query, criteriaBuilder) -> 
                criteriaBuilder.equal(root.get("active"), true);
            
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
                spec = spec.and((root, query, criteriaBuilder) -> 
                    criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("employmentType")), 
                        employmentType.toLowerCase()
                    )
                );
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
            // Retorna a lista de tipos de emprego disponíveis
            List<String> employmentTypes = List.of(
                "Tempo Integral",
                "Meio Período",
                "Contrato",
                "Temporário",
                "Estágio",
                "Freelance",
                "Terceirizado",
                "Home Office",
                "Trabalho Híbrido"
            );
            
            return ResponseEntity.ok(employmentTypes);
            
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

    @GetMapping("/recruiter")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getRecruiterJobs(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Usuário não autenticado");
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails == null) {
                return ResponseEntity.status(401).body("Detalhes do usuário não encontrados");
            }

            String recruiterEmail = userDetails.getUsername();
            if (recruiterEmail == null || recruiterEmail.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email do recrutador não pode ser vazio");
            }

            try {
                List<Job> jobs = jobService.getJobsByRecruiterEmail(recruiterEmail);
                return ResponseEntity.ok(jobs);
            } catch (EntityNotFoundException e) {
                return ResponseEntity.status(404).body(e.getMessage());
            } catch (Exception e) {
                System.err.println("Erro ao buscar vagas do recrutador: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.internalServerError().body("Erro ao processar a solicitação");
            }
        } catch (Exception e) {
            System.err.println("Erro inesperado ao processar requisição: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno do servidor");
        }
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> createJob(@Valid @RequestBody Job job, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Não autenticado");
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails == null) {
                return ResponseEntity.status(401).body("Detalhes do usuário não encontrados");
            }

            String recruiterEmail = userDetails.getUsername();
            if (recruiterEmail == null || recruiterEmail.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email do recrutador não pode ser vazio");
            }

            // Validação básica do objeto Job
            if (job == null) {
                return ResponseEntity.badRequest().body("Dados da vaga não fornecidos");
            }

            try {
                Job createdJob = jobService.createJob(job, recruiterEmail);
                return ResponseEntity.status(201).body(createdJob);
            } catch (RuntimeException e) {
                // Log do erro para depuração
                System.err.println("Erro ao criar vaga: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body("Erro ao processar a solicitação: " + e.getMessage());
            }
        } catch (Exception e) {
            // Log de erros inesperados
            System.err.println("Erro inesperado ao processar requisição: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno do servidor");
        }
    }

    @PutMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> updateJob(@PathVariable Long id, @Valid @RequestBody Job job,
                                     @AuthenticationPrincipal User recruiter) {
        try {
            if (recruiter == null) {
                return ResponseEntity.status(401).body("Usuário não autenticado");
            }

            if (job == null) {
                return ResponseEntity.badRequest().body("Dados da vaga não fornecidos");
            }

            try {
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
                Job job = jobService.toggleJobStatus(id, recruiter, active);
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