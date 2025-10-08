package br.com.one.jobportal.service.impl;

import br.com.one.jobportal.dto.response.AdminDashboardResponse;
import br.com.one.jobportal.dto.response.UserManagementResponse;
import br.com.one.jobportal.entity.Application;
import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.exception.ResourceNotFoundException;
import br.com.one.jobportal.repository.ApplicationRepository;
import br.com.one.jobportal.repository.JobRepository;
import br.com.one.jobportal.repository.UserRepository;
import br.com.one.jobportal.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboardStats() {
        log.info("Gerando estatísticas do dashboard administrativo");
        // Estatísticas de Usuários
        long totalUsers = userRepository.count();
        long totalJobSeekers = userRepository.countByRole(User.Role.JOB_SEEKER);
        long totalEmployers = userRepository.countByRole(User.Role.EMPLOYER);
        long totalAdmins = userRepository.countByRole(User.Role.ADMIN);
        Long activeUsers = userRepository.countByActive(true);
        Long inactiveUsers = userRepository.countByActive(false);
        
        // Estatísticas de Vagas
        long totalJobs = jobRepository.count();
        long activeJobs = jobRepository.countByIsClosedFalse();
        long closedJobs = jobRepository.countByIsClosed(true);
        
        // Estatísticas de Candidaturas
        long totalApplications = applicationRepository.count();
        long pendingApplications = applicationRepository.countByStatus(Application.Status.PENDING);
        long approvedApplications = applicationRepository.countByStatus(Application.Status.APPROVED);
        long rejectedApplications = applicationRepository.countByStatus(Application.Status.REJECTED);
        
        // Mapa de status de candidaturas
        Map<String, Long> applicationsByStatus = new HashMap<>();
        applicationsByStatus.put("PENDING", pendingApplications);
        applicationsByStatus.put("APPROVED", approvedApplications);
        applicationsByStatus.put("REJECTED", rejectedApplications);
        
        // Cálculos
        double avgApplicationsPerJob = totalJobs > 0 ? (double) totalApplications / totalJobs : 0;
        double successRate = totalApplications > 0 ? (double) approvedApplications / totalApplications * 100 : 0;
        
        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalJobSeekers(totalJobSeekers)
                .totalEmployers(totalEmployers)
                .totalAdmins(totalAdmins)
                .activeUsers(activeUsers != null ? activeUsers : 0L)
                .inactiveUsers(inactiveUsers != null ? inactiveUsers : 0L)
                .totalJobs(totalJobs)
                .activeJobs(activeJobs)
                .closedJobs(closedJobs)
                .totalApplications(totalApplications)
                .pendingApplications(pendingApplications)
                .approvedApplications(approvedApplications)
                .rejectedApplications(rejectedApplications)
                .applicationsByStatus(applicationsByStatus)
                .averageApplicationsPerJob(avgApplicationsPerJob)
                .applicationSuccessRate(successRate)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserManagementResponse> getAllUsers(String role, Boolean active, Pageable pageable) {
        log.info("Listando usuários - Role: {}, Active: {}", role, active);
        
        Page<User> usersPage;
        if (role != null && active != null) {
            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            usersPage = userRepository.findByRoleAndActive(userRole, active, pageable);
        } else if (role != null) {
            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            usersPage = userRepository.findByRole(userRole, pageable);
        } else if (active != null) {
            usersPage = userRepository.findByActive(active, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }
        
        return usersPage.map(this::convertToManagementResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserManagementResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return convertToManagementResponse(user);
    }
    

    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        log.info("Alterando status do usuário: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setActive(!user.isActive());
        userRepository.save(user);
        log.info("Status do usuário {} alterado para: {}", userId, user.isActive());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deletando usuário: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        userRepository.delete(user);
        log.info("Usuário {} deletado com sucesso", userId);
    }

    @Override
    @Transactional
    public void closeJob(Long jobId) {
        log.info("Fechando vaga: {}", jobId);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        job.setIsClosed(true);
        jobRepository.save(job);
        log.info("Vaga {} fechada com sucesso", jobId);
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId) {
        log.info("Deletando vaga: {}", jobId);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        jobRepository.delete(job);
        log.info("Vaga {} deletada com sucesso", jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUsersByRole(String role) {
        User.Role userRole = User.Role.valueOf(role.toUpperCase());
        return userRepository.countByRole(userRole);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveUsers() {
        return userRepository.countByActive(true);
    }

    private UserManagementResponse convertToManagementResponse(User user) {
        Long totalJobs = null;
        Long totalApplications = null;
        
        if (user.getRole() == User.Role.EMPLOYER) {
            totalJobs = jobRepository.countByRecruiter(user);
        } else if (user.getRole() == User.Role.JOB_SEEKER) {
            totalApplications = applicationRepository.countByApplicant(user);
        }
        
        return UserManagementResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.isActive())
                .phone(user.getPhone())
                .companyName(user.getCompanyName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .totalJobs(totalJobs)
                .totalApplications(totalApplications)
                .build();
    }
}
