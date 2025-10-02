package br.com.one.jobportal.controller;

import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.service.JobStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs/status")
public class JobStatusController {

    private final JobStatusService jobStatusService;

    public JobStatusController(JobStatusService jobStatusService) {
        this.jobStatusService = jobStatusService;
    }

    @PostMapping("/{jobId}/toggle-save")
    public ResponseEntity<?> toggleSaveStatus(@PathVariable Long jobId, @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(jobStatusService.toggleSaveStatus(jobId, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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

    @GetMapping("/{jobId}/is-saved")
    public ResponseEntity<?> isJobSaved(@PathVariable Long jobId, @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(jobStatusService.isJobSaved(jobId, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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
