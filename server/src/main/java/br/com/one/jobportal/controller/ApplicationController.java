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

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApplicationResponse> applyForJob(
            @Valid @RequestBody ApplyJobRequest request,
            @AuthenticationPrincipal User user) {
        ApplicationResponse response = applicationService.applyForJob(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdrawApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        applicationService.withdrawApplication(id, user);
    }

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

    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Page<ApplicationResponse>> getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @AuthenticationPrincipal User user) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ApplicationResponse> applications = applicationService.getApplicationsByJobSeeker(user, status, pageable);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Page<ApplicationResponse>> getApplicationsForEmployer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long jobId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @AuthenticationPrincipal User user) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ApplicationResponse> applications = applicationService.getApplicationsForEmployer(user, status, jobId, pageable);
        return ResponseEntity.ok(applications);
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

    @GetMapping("/stats/job-seeker")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> getJobSeekerStats(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getJobSeekerStats(user));
    }

    @GetMapping("/stats/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> getEmployerStats(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getEmployerStats(user));
    }

    @GetMapping("/stats/job/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> getJobStats(
            @PathVariable Long jobId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getJobStats(jobId, user));
    }
}
