package br.com.one.jobportal.controller;

import br.com.one.jobportal.dto.UpdateProfileRequest;
import br.com.one.jobportal.dto.UpdateResumeRequest;
import br.com.one.jobportal.dto.response.LoginResponse;
import br.com.one.jobportal.dto.response.UserProfileResponse;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PutMapping("/resume")
    public ResponseEntity<LoginResponse> updateResume(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateResumeRequest request) {
        
        log.info("Atualizando currículo para o usuário: {}", userDetails.getUsername());
        User updatedUser = profileService.updateUserResume(userDetails, request);
        
        // Criar uma resposta de login atualizada
        LoginResponse response = LoginResponse.fromUserAndToken(updatedUser, null);
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/me")
    public ResponseEntity<User> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        
        log.info("Atualizando perfil para o usuário: {}", userDetails.getUsername());
        User updatedUser = profileService.updateProfile(userDetails, request);
        return ResponseEntity.ok(updatedUser);
    }
    
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Obtendo perfil do usuário: {}", userDetails.getUsername());
        User user = profileService.getMyProfile(userDetails);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getPublicProfile(@PathVariable Long userId) {
        log.info("Obtendo perfil público do usuário ID: {}", userId);
        UserProfileResponse profile = profileService.getPublicProfile(userId);
        return ResponseEntity.ok(profile);
    }
    
    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        
        log.info("Upload de avatar para o usuário: {}", userDetails.getUsername());
        try {
            String filePath = profileService.uploadAvatar(userDetails, file);
            return ResponseEntity.ok(filePath);
        } catch (IOException e) {
            log.error("Erro ao fazer upload do avatar: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erro ao fazer upload do arquivo: " + e.getMessage());
        }
    }
}
