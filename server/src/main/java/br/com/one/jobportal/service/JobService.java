package br.com.one.jobportal.service;

import br.com.one.jobportal.entity.Job;
import br.com.one.jobportal.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

/**
 * Interface de serviço para operações relacionadas a vagas de emprego.
 * Contém apenas os métodos que estão sendo utilizados no código.
 */
public interface JobService {
    
    /**
     * Salva uma nova vaga no sistema.
     * 
     * @param job A vaga a ser salva
     * @return A vaga salva com o ID gerado
     */
    Job saveJob(Job job);
    
    /**
     * Busca uma vaga pelo ID.
     * 
     * @param id O ID da vaga
     * @return A vaga encontrada
     * @throws EntityNotFoundException Se a vaga não for encontrada
     */
    Job getJobById(Long id) throws EntityNotFoundException;
    
    /**
     * Exclui uma vaga do sistema.
     * 
     * @param id O ID da vaga a ser excluída
     * @param recruiter O usuário que está tentando excluir a vaga
     * @throws EntityNotFoundException Se a vaga não for encontrada
     * @throws AccessDeniedException Se o usuário não tiver permissão para excluir a vaga
     */
    void deleteJob(Long id, User recruiter) throws EntityNotFoundException, AccessDeniedException;
    
    /**
     * Atualiza os dados de uma vaga existente.
     * 
     * @param id O ID da vaga a ser atualizada
     * @param job Os novos dados da vaga
     * @param recruiter O usuário que está tentando atualizar a vaga
     * @return A vaga atualizada
     * @throws EntityNotFoundException Se a vaga não for encontrada
     * @throws AccessDeniedException Se o usuário não tiver permissão para atualizar a vaga
     */
    Job updateJob(Long id, Job job, User recruiter) throws EntityNotFoundException, AccessDeniedException;
    
    /**
     * Busca vagas com base em critérios de pesquisa.
     * 
     * @param spec Especificação de critérios de pesquisa
     * @param pageable Configuração de paginação
     * @return Uma página de vagas que atendem aos critérios
     */
    Page<Job> searchJobs(Specification<Job> spec, Pageable pageable);
    
    /**
     * Verifica se um usuário é o dono de uma vaga.
     * 
     * @param jobId O ID da vaga
     * @param user O usuário a ser verificado
     * @return true se o usuário for o dono da vaga, false caso contrário
     */
    boolean isJobOwner(Long jobId, User user);
}
