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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<List<Job>> getRecruiterJobs(@AuthenticationPrincipal User recruiter) {
        return ResponseEntity.ok(jobService.getJobsByRecruiter(recruiter));
    }

    @PostMapping
    public ResponseEntity<Job> createJob(@RequestBody Job job, @AuthenticationPrincipal User recruiter) {
        return ResponseEntity.ok(jobService.createJob(job, recruiter));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Long id, @RequestBody Job job,
                                         @AuthenticationPrincipal User recruiter) {
        return ResponseEntity.ok(jobService.updateJob(id, job, recruiter));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id, @AuthenticationPrincipal User recruiter) {
        jobService.deleteJob(id, recruiter);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Job> toggleJobStatus(@PathVariable Long id, @RequestParam boolean active,
                                               @AuthenticationPrincipal User recruiter) {
        return ResponseEntity.ok(jobService.toggleJobStatus(id, recruiter, active));
    }
}
