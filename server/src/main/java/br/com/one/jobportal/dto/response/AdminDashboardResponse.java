package br.com.one.jobportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    // Estatísticas de Usuários
    private Long totalUsers;
    private Long totalJobSeekers;
    private Long totalEmployers;
    private Long totalAdmins;
    private Long activeUsers;
    private Long inactiveUsers;
    
    // Estatísticas de Vagas
    private Long totalJobs;
    private Long activeJobs;
    private Long closedJobs;
    private Map<String, Long> jobsByCategory;
    private Map<String, Long> jobsByType;
    
    // Estatísticas de Candidaturas
    private Long totalApplications;
    private Long pendingApplications;
    private Long approvedApplications;
    private Long rejectedApplications;
    private Map<String, Long> applicationsByStatus;
    
    // Estatísticas Gerais
    private Double averageApplicationsPerJob;
    private Double applicationSuccessRate;
}
