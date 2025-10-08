package br.com.one.jobportal.controller;

import br.com.one.jobportal.dto.response.AdminDashboardResponse;
import br.com.one.jobportal.dto.response.UserManagementResponse;
import br.com.one.jobportal.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserManagementResponse>> getAllUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return ResponseEntity.ok(adminService.getAllUsers(role, active, pageable));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserManagementResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserById(userId));
    }

    @PutMapping("/users/{userId}/toggle-status")
    public ResponseEntity<Void> toggleUserStatus(@PathVariable Long userId) {
        adminService.toggleUserStatus(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
    }

    @PutMapping("/jobs/{jobId}/close")
    public ResponseEntity<Void> closeJob(@PathVariable Long jobId) {
        adminService.closeJob(jobId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/jobs/{jobId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJob(@PathVariable Long jobId) {
        adminService.deleteJob(jobId);
    }

    @GetMapping("/stats/users/{role}")
    public ResponseEntity<Long> countUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(adminService.countUsersByRole(role));
    }

    @GetMapping("/stats/active-users")
    public ResponseEntity<Long> countActiveUsers() {
        return ResponseEntity.ok(adminService.countActiveUsers());
    }
}
