package br.com.one.jobportal.controller;

import br.com.one.jobportal.dto.SaveJobRequest;
import br.com.one.jobportal.dto.response.SavedJobResponse;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.service.SavedJobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;

    @PostMapping
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<SavedJobResponse> saveJob(
            @Valid @RequestBody SaveJobRequest request,
            @AuthenticationPrincipal User jobSeeker) {
        SavedJobResponse response = savedJobService.saveJob(request, jobSeeker);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Void> unsaveJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal User jobSeeker) {
        savedJobService.unsaveJob(jobId, jobSeeker);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Page<SavedJobResponse>> getSavedJobs(
            @AuthenticationPrincipal User jobSeeker,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<SavedJobResponse> savedJobs = savedJobService.getSavedJobsByJobSeeker(jobSeeker.getId(), pageable);
        return ResponseEntity.ok(savedJobs);
    }

    @GetMapping("/check/{jobId}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Boolean> isJobSavedByUser(
            @PathVariable Long jobId,
            @AuthenticationPrincipal User jobSeeker) {
        boolean isSaved = savedJobService.isJobSavedByUser(jobSeeker.getId(), jobId);
        return ResponseEntity.ok(isSaved);
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Long> countSavedJobs(@AuthenticationPrincipal User jobSeeker) {
        long count = savedJobService.countSavedJobsByJobSeeker(jobSeeker.getId());
        return ResponseEntity.ok(count);
    }
}
