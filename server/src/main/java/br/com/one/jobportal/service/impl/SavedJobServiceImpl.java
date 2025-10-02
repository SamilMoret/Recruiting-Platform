package br.com.one.jobportal.service.impl;

import br.com.one.jobportal.dto.SaveJobRequest;
import br.com.one.jobportal.dto.response.SavedJobResponse;
import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.SavedJob;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.exception.ResourceNotFoundException;
import br.com.one.jobportal.repository.JobRepository;
import br.com.one.jobportal.repository.SavedJobRepository;
import br.com.one.jobportal.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedJobServiceImpl implements SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional
    public SavedJobResponse saveJob(SaveJobRequest request, User jobSeeker) {
        // Verifica se a vaga existe
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada com o ID: " + request.getJobId()));
        
        // Verifica se a vaga já foi salva pelo candidato
        if (savedJobRepository.existsByJobSeekerIdAndJobId(jobSeeker.getId(), job.getId())) {
            throw new IllegalStateException("Esta vaga já foi salva anteriormente");
        }
        
        // Cria e salva a vaga salva
        SavedJob savedJob = new SavedJob();
        savedJob.setJobSeeker(jobSeeker);
        savedJob.setJob(job);
        
        SavedJob saved = savedJobRepository.save(savedJob);
        return convertToResponse(saved);
    }

    @Override
    @Transactional
    public void unsaveJob(Long jobId, User jobSeeker) {
        // Verifica se a vaga foi salva pelo candidato
        SavedJob savedJob = savedJobRepository.findByJobSeekerIdAndJobId(jobSeeker.getId(), jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga salva não encontrada"));
        
        // Remove a vaga salva
        savedJobRepository.delete(savedJob);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavedJobResponse> getSavedJobsByJobSeeker(Long jobSeekerId) {
        return savedJobRepository.findByJobSeekerId(jobSeekerId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SavedJobResponse> getSavedJobsByJobSeeker(Long jobSeekerId, Pageable pageable) {
        // Busca as vagas salvas com paginação e ordenação
        return savedJobRepository.findByJobSeekerId(jobSeekerId, pageable)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isJobSavedByUser(Long jobSeekerId, Long jobId) {
        return savedJobRepository.existsByJobSeekerIdAndJobId(jobSeekerId, jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countSavedJobsByJobSeeker(Long jobSeekerId) {
        return savedJobRepository.countByJobSeekerId(jobSeekerId);
    }
    
    // Método auxiliar para converter SavedJob para SavedJobResponse
    private SavedJobResponse convertToResponse(SavedJob savedJob) {
        SavedJobResponse response = new SavedJobResponse();
        
        // Mapeia os campos básicos
        response.setId(savedJob.getId());
        response.setJobId(savedJob.getJob().getId());
        response.setJobSeekerId(savedJob.getJobSeeker().getId());
        response.setSavedAt(savedJob.getCreatedAt());
        
        // Preenche os campos adicionais da vaga
        Job job = savedJob.getJob();
        response.setJobTitle(job.getTitle());
        // Obtém o nome da empresa do recrutador
        String companyName = job.getRecruiter() != null && job.getRecruiter().getCompanyName() != null 
            ? job.getRecruiter().getCompanyName() 
            : "Empresa não especificada";
        response.setCompanyName(companyName);
        response.setLocation(job.getLocation());
        response.setEmploymentType(job.getType() != null ? job.getType().name() : null);
        response.setJobCategory(job.getCategory());
        // Como não há applicationDeadline na entidade Job, usaremos null
        response.setApplicationDeadline(null);
        
        return response;
    }
}
