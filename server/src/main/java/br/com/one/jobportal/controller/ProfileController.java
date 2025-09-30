package br.com.one.jobportal.controller;

import br.com.one.jobportal.dto.UpdateResumeRequest;
import br.com.one.jobportal.dto.response.LoginResponse;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PutMapping("/resume")
    public ResponseEntity<LoginResponse> updateResume(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateResumeRequest request) {
        
        User updatedUser = profileService.updateUserResume(userDetails, request);
        
        // Criar uma resposta de login atualizada
        LoginResponse response = LoginResponse.fromUserAndToken(updatedUser, null);
        
        return ResponseEntity.ok(response);
    }
}
