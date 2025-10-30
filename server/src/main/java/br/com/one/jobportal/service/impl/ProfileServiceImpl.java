package br.com.one.jobportal.service.impl;

import br.com.one.jobportal.config.StorageProperties;
import br.com.one.jobportal.dto.UpdateProfileRequest;
import br.com.one.jobportal.dto.UpdateResumeRequest;
import br.com.one.jobportal.dto.response.UserProfileResponse;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.exception.ResourceNotFoundException;
import br.com.one.jobportal.exception.StorageException;
import br.com.one.jobportal.exception.UnauthorizedException;
import br.com.one.jobportal.repository.UserRepository;
import br.com.one.jobportal.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final StorageProperties storageProperties;

    @Override
    @Transactional(readOnly = true)
    public User getMyProfile(UserDetails userDetails) {
        log.info("Obtendo perfil do usuário: {}", userDetails.getUsername());
        
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado para o email: {}", userDetails.getUsername());
                    return new ResourceNotFoundException("Usuário não encontrado");
                });
    }
    
    @Override
    @Transactional
    public User updateUserResume(UserDetails userDetails, UpdateResumeRequest request) {
        log.info("Atualizando currículo do usuário: {}", userDetails.getUsername());
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado para o email: {}", userDetails.getUsername());
                    return new ResourceNotFoundException("Usuário não encontrado");
                });

        // Verifica se o usuário é um JOB_SEEKER
        if (!User.Role.JOB_SEEKER.equals(user.getRole())) {
            log.warn("Tentativa de acesso negado - Usuário não é um JOB_SEEKER. Role atual: {}", user.getRole());
            throw new UnauthorizedException("Apenas candidatos podem atualizar o currículo");
        }

        // Atualiza a URL do currículo se fornecida
        if (request.getResumeUrl() != null && !request.getResumeUrl().isBlank()) {
            log.info("Atualizando currículo para: {}", request.getResumeUrl());
            // Verifica se o método setResumeUrl existe na entidade User
            try {
                user.getClass().getMethod("setResumeUrl", String.class).invoke(user, request.getResumeUrl());
            } catch (Exception e) {
                log.warn("Não foi possível atualizar o currículo. O campo 'resumeUrl' não está disponível na entidade User");
                throw new UnsupportedOperationException("Funcionalidade de currículo não implementada");
            }
        }
        
        User savedUser = userRepository.save(user);
        log.info("Currículo atualizado com sucesso para o usuário ID: {}", savedUser.getId());
        return savedUser;
    }
    
    @Override
    @Transactional
    public User updateProfile(UserDetails userDetails, UpdateProfileRequest request) {
        log.info("Atualizando perfil do usuário: {}", userDetails.getUsername());
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
                
        // Atualiza os campos básicos
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        
        // Se for um empregador, atualiza os dados da empresa
        if (User.Role.EMPLOYER.equals(user.getRole())) {
            if (request.getCompanyName() != null) {
                user.setCompanyName(request.getCompanyName());
            }
            if (request.getCompanyDescription() != null) {
                user.setCompanyDescription(request.getCompanyDescription());
            }
            // TODO: Adicionar campo website na entidade User se necessário
            // if (request.getWebsite() != null) {
            //     user.setWebsite(request.getWebsite());
            // }
            log.warn("Campo 'website' não está disponível na entidade User. Adicione o campo se necessário.");
        }
        
        // Se for um candidato, atualiza os dados específicos
        if (User.Role.JOB_SEEKER.equals(user.getRole())) {
            // Se o request tiver campos específicos de candidato, atualize-os aqui
            // Exemplo:
            // if (request.getTitle() != null) {
            //     user.setTitle(request.getTitle());
            // }
        }
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getPublicProfile(Long userId) {
        log.info("Buscando perfil público do usuário ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
                
        return mapToUserProfileResponse(user);
    }
    
    
    @Override
    @Transactional
    public String uploadAvatar(UserDetails userDetails, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new StorageException("Falha ao armazenar arquivo vazio");
        }
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
                
        // Gera um nome único para o arquivo
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }
        String filename = "avatar_" + user.getId() + "_" + UUID.randomUUID() + extension;
        
        // Cria o diretório se não existir
        Path uploadDir = Paths.get(storageProperties.getLocation());
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // Salva o arquivo
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadDir.resolve(filename);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Atualiza o caminho do avatar no perfil do usuário
        String avatarPath = "/uploads/" + filename;
        user.setAvatar(avatarPath);
        userRepository.save(user);
        
        return avatarPath;
    }
    
    private UserProfileResponse mapToUserProfileResponse(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }
        
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                // TODO: Adicionar campos adicionais quando estiverem disponíveis na entidade User
                // .resume(user.getResumeUrl())
                // .title(user.getTitle())
                // .skills(user.getSkills())
                // .companyName(user.getCompanyName())
                // .companyDescription(user.getCompanyDescription())
                // .website(user.getWebsite())
                .build();
    }
}
