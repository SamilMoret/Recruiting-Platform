package br.com.one.jobportal.service.impl;

import br.com.one.jobportal.dto.UpdateResumeRequest;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.exception.ResourceNotFoundException;
import br.com.one.jobportal.exception.UnauthorizedException;
import br.com.one.jobportal.repository.UserRepository;
import br.com.one.jobportal.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User updateUserResume(UserDetails userDetails, UpdateResumeRequest request) {
        log.info("Iniciando atualização de currículo para o usuário: {}", userDetails.getUsername());
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado para o email: {}", userDetails.getUsername());
                    return new ResourceNotFoundException("Usuário não encontrado");
                });

        log.info("Usuário encontrado - ID: {}, Role: {}", user.getId(), user.getRole());

        // Verifica se o usuário é um JOB_SEEKER
        if (!User.Role.JOB_SEEKER.equals(user.getRole())) {
            log.warn("Tentativa de acesso negado - Usuário não é um JOB_SEEKER. Role atual: {}", user.getRole());
            throw new UnauthorizedException("Apenas candidatos podem atualizar o currículo");
        }

        log.info("Atualizando currículo para: {}", request.getResumeUrl());
        user.setResume(request.getResumeUrl());
        
        User savedUser = userRepository.save(user);
        log.info("Currículo atualizado com sucesso para o usuário ID: {}", savedUser.getId());
        return savedUser;
    }
}
