package br.com.one.jobportal.service;

import br.com.one.jobportal.dto.response.AdminDashboardResponse;
import br.com.one.jobportal.dto.response.UserManagementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    // Dashboard
    AdminDashboardResponse getDashboardStats();
    
    // Gerenciamento de Usuários
    Page<UserManagementResponse> getAllUsers(String role, Boolean active, Pageable pageable);
    UserManagementResponse getUserById(Long userId);
    void toggleUserStatus(Long userId);
    void deleteUser(Long userId);
    
    // Gerenciamento de Vagas
    void closeJob(Long jobId);
    void deleteJob(Long jobId);
    
    // Estatísticas
    Long countUsersByRole(String role);
    Long countActiveUsers();
}
