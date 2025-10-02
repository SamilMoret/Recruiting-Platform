package br.com.one.jobportal.service;

import br.com.one.jobportal.dto.SaveJobRequest;
import br.com.one.jobportal.dto.response.SavedJobResponse;
import br.com.one.jobportal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SavedJobService {
    
    /**
     * Salva uma vaga para o candidato
     * @param request Dados da vaga a ser salva
     * @param jobSeeker Candidato que está salvando a vaga
     * @return Resposta com os dados da vaga salva
     */
    SavedJobResponse saveJob(SaveJobRequest request, User jobSeeker);
    
    /**
     * Remove uma vaga salva
     * @param jobId ID da vaga a ser removida
     * @param jobSeeker Candidato que está removendo a vaga
     */
    void unsaveJob(Long jobId, User jobSeeker);
    
    /**
     * Lista todas as vagas salvas por um candidato
     * @param jobSeekerId ID do candidato
     * @return Lista de vagas salvas
     */
    List<SavedJobResponse> getSavedJobsByJobSeeker(Long jobSeekerId);
    
    /**
     * Lista todas as vagas salvas por um candidato com paginação
     * @param jobSeekerId ID do candidato
     * @param pageable Configurações de paginação
     * @return Página de vagas salvas
     */
    Page<SavedJobResponse> getSavedJobsByJobSeeker(Long jobSeekerId, Pageable pageable);
    
    /**
     * Verifica se uma vaga foi salva por um candidato
     * @param jobSeekerId ID do candidato
     * @param jobId ID da vaga
     * @return true se a vaga foi salva, false caso contrário
     */
    boolean isJobSavedByUser(Long jobSeekerId, Long jobId);
    
    /**
     * Conta quantas vagas um candidato salvou
     * @param jobSeekerId ID do candidato
     * @return Número de vagas salvas
     */
    long countSavedJobsByJobSeeker(Long jobSeekerId);
}
