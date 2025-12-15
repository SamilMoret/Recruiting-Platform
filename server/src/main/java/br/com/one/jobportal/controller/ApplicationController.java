package br.com.one.jobportal.controller;

import br.com.one.jobportal.dto.ApplyJobRequest;
import br.com.one.jobportal.dto.response.ApplicationResponse;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Candidaturas", description = "Gerenciamento de candidaturas a vagas")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "Candidatar-se a uma vaga",
               description = "Permite que um candidato se candidate a uma vaga de emprego",
               security = @SecurityRequirement(name = "bearerAuth"),
               responses = {
                   @ApiResponse(responseCode = "201", description = "Candidatura realizada com sucesso"),
                   @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
                   @ApiResponse(responseCode = "401", description = "Não autorizado"),
                   @ApiResponse(responseCode = "403", description = "Acesso negado")
               })
    @PostMapping
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApplicationResponse> applyForJob(
            @Valid @RequestBody ApplyJobRequest request,
            @AuthenticationPrincipal User user) {
        ApplicationResponse response = applicationService.applyForJob(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Cancelar candidatura",
               description = "Permite que um candidato cancele sua candidatura a uma vaga",
               security = @SecurityRequirement(name = "bearerAuth"),
               responses = {
                   @ApiResponse(responseCode = "204", description = "Candidatura cancelada com sucesso"),
                   @ApiResponse(responseCode = "401", description = "Não autorizado"),
                   @ApiResponse(responseCode = "403", description = "Acesso negado"),
                   @ApiResponse(responseCode = "404", description = "Candidatura não encontrada")
               })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdrawApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        applicationService.withdrawApplication(id, user);
    }

    @Operation(summary = "Atualizar status da candidatura",
               description = "Permite que um empregador atualize o status de uma candidatura",
               security = @SecurityRequirement(name = "bearerAuth"),
               parameters = {
                   @Parameter(name = "id", description = "ID da candidatura", required = true, example = "1"),
                   @Parameter(name = "status", description = "Novo status da candidatura (ex: REVIEWING, INTERVIEW_SCHEDULED, HIRED, REJECTED)", 
                             required = true, example = "INTERVIEW_SCHEDULED")
               },
               responses = {
                   @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
                   @ApiResponse(responseCode = "400", description = "Status inválido"),
                   @ApiResponse(responseCode = "401", description = "Não autorizado"),
                   @ApiResponse(responseCode = "403", description = "Acesso negado"),
                   @ApiResponse(responseCode = "404", description = "Candidatura não encontrada")
               })
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ApplicationResponse updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal User user) {
        return applicationService.updateApplicationStatus(id, status, user);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApplicationResponse getApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return applicationService.getApplicationById(id, user);
    }

    @Operation(summary = "Minhas candidaturas",
               description = "Lista todas as candidaturas do usuário autenticado (candidato)",
               security = @SecurityRequirement(name = "bearerAuth"),
               parameters = {
                   @Parameter(name = "page", description = "Número da página (0-based)", example = "0"),
                   @Parameter(name = "size", description = "Tamanho da página", example = "10"),
                   @Parameter(name = "sort", description = "Campo para ordenação (ex: 'dateApplied,desc')", example = "dateApplied,desc")
               },
               responses = {
                   @ApiResponse(responseCode = "200", description = "Lista de candidaturas retornada com sucesso"),
                   @ApiResponse(responseCode = "401", description = "Não autorizado"),
                   @ApiResponse(responseCode = "403", description = "Acesso negado")
               })
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public List<ApplicationResponse> getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort,
            @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return applicationService.getApplicationsByJobSeeker(user, pageable).getContent();
    }

    @Operation(summary = "Listar candidaturas de um empregador",
               description = "Lista todas as candidaturas recebidas para as vagas de um empregador",
               security = @SecurityRequirement(name = "bearerAuth"),
               parameters = {
                   @Parameter(name = "page", description = "Número da página (0-based)", example = "0"),
                   @Parameter(name = "size", description = "Tamaño de la página", example = "10"),
                   @Parameter(name = "sort", description = "Campo para ordenação (ex: 'dateApplied,desc')", example = "dateApplied,desc")
               },
               responses = {
                   @ApiResponse(responseCode = "200", description = "Lista de candidaturas retornada com sucesso"),
                   @ApiResponse(responseCode = "401", description = "Não autorizado"),
                   @ApiResponse(responseCode = "403", description = "Acesso negado")
               })
    @GetMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    public List<ApplicationResponse> getApplicationsForEmployer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort,
            @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return applicationService.getApplicationsForEmployer(user, pageable).getContent();
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("isAuthenticated()")
    public List<ApplicationResponse> getApplicationsByJobId(
            @PathVariable Long jobId,
            @AuthenticationPrincipal User user) {
        return applicationService.getApplicationsByJobId(jobId, user);
    }

    @GetMapping("/count/my-applications")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public long countMyApplications(@AuthenticationPrincipal User user) {
        return applicationService.countApplicationsByJobSeeker(user);
    }

    @GetMapping("/count/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    public long countApplicationsForEmployer(@AuthenticationPrincipal User user) {
        return applicationService.countApplicationsForEmployer(user);
    }

    @GetMapping("/check/{jobId}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public boolean hasApplied(
            @PathVariable Long jobId,
            @AuthenticationPrincipal User user) {
        return applicationService.hasApplied(jobId, user);
    }
}
