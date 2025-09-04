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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@CrossOrigin
public class JobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<Page<Job>> getAllJobs(
            Specification<Job> spec,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(jobService.searchJobs(spec, pageable));
    }

    @GetMapping("/recruiter")
    public ResponseEntity<List<Job>> getRecruiterJobs(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String recruiterEmail = userDetails.getUsername();

            System.out.println("✅ Buscando vagas do recrutador: " + recruiterEmail);
            List<Job> jobs = jobService.getJobsByRecruiterEmail(recruiterEmail); // ← Use o novo método

            System.out.println("✅ Vagas encontradas: " + jobs.size());
            return ResponseEntity.ok(jobs);

        } catch (Exception e) {
            System.out.println("❌ Erro ao buscar vagas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody Job job, Authentication authentication) {
        try {
            System.out.println("✅ Authentication: " + authentication);
            System.out.println("✅ Principal: " + authentication.getPrincipal());

            // Obtenha o usuário do Authentication
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String recruiterEmail = userDetails.getUsername();

            System.out.println("✅ Recrutador email: " + recruiterEmail);

            // Chame o service passando o email em vez do objeto User
            Job createdJob = jobService.createJob(job, recruiterEmail);

            System.out.println("✅ Vaga criada com ID: " + createdJob.getId());
            return ResponseEntity.ok(createdJob);

        } catch (Exception e) {
            System.out.println("❌ ERRO AO CRIAR VAGA: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody Job job,
                                       @AuthenticationPrincipal User recruiter) {
        try {
            System.out.println("✅ Atualizando vaga ID: " + id);
            System.out.println("✅ Recrutador: " + recruiter.getEmail());

            Job updatedJob = jobService.updateJob(id, job, recruiter);
            return ResponseEntity.ok(updatedJob);

        } catch (Exception e) {
            System.out.println("❌ Erro ao atualizar vaga: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id, @AuthenticationPrincipal User recruiter) {
        try {
            System.out.println("✅ Deletando vaga ID: " + id);
            System.out.println("✅ Recrutador: " + recruiter.getEmail());

            jobService.deleteJob(id, recruiter);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            System.out.println("❌ Erro ao deletar vaga: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> toggleJobStatus(@PathVariable Long id, @RequestParam boolean active,
                                             @AuthenticationPrincipal User recruiter) {
        try {
            System.out.println("✅ Alterando status da vaga ID: " + id);
            System.out.println("✅ Novo status: " + (active ? "ATIVA" : "INATIVA"));
            System.out.println("✅ Recrutador: " + recruiter.getEmail());

            Job job = jobService.toggleJobStatus(id, recruiter, active);
            return ResponseEntity.ok(job);

        } catch (Exception e) {
            System.out.println("❌ Erro ao alterar status: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }
}