package br.com.one.jobportal.controller;

import br.com.one.jobportal.dto.response.ApplicationStatsResponse;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.service.ApplicationService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final ApplicationService applicationService;
    
    @GetMapping("/candidate")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApplicationStatsResponse> getCandidateStats(
            @AuthenticationPrincipal User candidate) {
        ApplicationStatsResponse stats = applicationService.getJobSeekerStats(candidate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/employer/overview")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApplicationStatsResponse> getEmployerOverview(
            @AuthenticationPrincipal User employer) {
        ApplicationStatsResponse stats = applicationService.getEmployerStats(employer);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/employer/jobs/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApplicationStatsResponse> getJobAnalytics(
            @PathVariable @NotNull Long jobId,
            @AuthenticationPrincipal User employer) {
        ApplicationStatsResponse stats = applicationService.getJobStats(jobId, employer);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/employer/applications")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApplicationStatsResponse> getApplicationsAnalytics(
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String period,
            @AuthenticationPrincipal User employer) {
        
        ApplicationStatsResponse stats;
        if (jobId != null) {
            stats = applicationService.getJobStats(jobId, employer);
        } else {
            stats = applicationService.getEmployerStats(employer);
        }
        return ResponseEntity.ok(stats);
    }
}
