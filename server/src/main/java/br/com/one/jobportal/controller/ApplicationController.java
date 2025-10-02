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
    public List<ApplicationResponse> getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort,
            @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return applicationService.getApplicationsByJobSeeker(user, pageable).getContent();
    }

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
