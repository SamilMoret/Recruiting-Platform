package br.com.one.jobportal.controller;

import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.service.JobStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/jobs/status")
@Tag(name = "Status de Vagas", description = "Endpoints para gerenciar o status de vagas e candidaturas")
public class JobStatusController {

    private final JobStatusService jobStatusService;

    public JobStatusController(JobStatusService jobStatusService) {
        this.jobStatusService = jobStatusService;
    }

    @Operation(summary = "Salvar/Remover vaga dos favoritos",
               description = "Alterna o status de 'salvo' para uma vaga específica",
               security = @SecurityRequirement(name = "bearerAuth"),
               responses = {
                   @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
                   @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                   @ApiResponse(responseCode = "401", description = "Não autorizado")
               })
    @PostMapping("/{jobId}/toggle-save")
    public ResponseEntity<?> toggleSaveStatus(@PathVariable Long jobId, @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(jobStatusService.toggleSaveStatus(jobId, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Atualizar status da candidatura",
               description = "Atualiza o status de uma candidatura para uma vaga específica",
               security = @SecurityRequirement(name = "bearerAuth"),
               parameters = {
                   @Parameter(name = "jobId", description = "ID da vaga", required = true, example = "1"),
                   @Parameter(name = "status", description = "Novo status da candidatura (ex: 'APPLIED', 'INTERVIEW', 'HIRED', 'REJECTED')", 
                             required = true, example = "INTERVIEW")
               },
               responses = {
                   @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
                   @ApiResponse(responseCode = "400", description = "Status inválido"),
                   @ApiResponse(responseCode = "401", description = "Não autorizado")
               })
    @PostMapping("/{jobId}/application-status")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long jobId, 
            @RequestParam String status,
            @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(jobStatusService.updateApplicationStatus(jobId, status, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Verificar se vaga está salva",
               description = "Verifica se uma vaga específica está salva nos favoritos do usuário",
               security = @SecurityRequirement(name = "bearerAuth"),
               responses = {
                   @ApiResponse(responseCode = "200", description = "Status de salvamento retornado"),
                   @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                   @ApiResponse(responseCode = "401", description = "Não autorizado")
               })
    @GetMapping("/{jobId}/is-saved")
    public ResponseEntity<?> isJobSaved(@PathVariable Long jobId, @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(jobStatusService.isJobSaved(jobId, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Incrementar contador de candidaturas",
               description = "Incrementa o contador de candidaturas para uma vaga específica",
               security = @SecurityRequirement(name = "bearerAuth"),
               responses = {
                   @ApiResponse(responseCode = "200", description = "Contador incrementado com sucesso"),
                   @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                   @ApiResponse(responseCode = "401", description = "Não autorizado")
               })
    @PostMapping("/{jobId}/increment-application")
    public ResponseEntity<?> incrementApplicationCount(@PathVariable Long jobId) {
        try {
            jobStatusService.incrementApplicationCount(jobId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
