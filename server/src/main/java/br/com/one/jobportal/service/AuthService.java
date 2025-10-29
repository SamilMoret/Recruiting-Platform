package br.com.one.jobportal.service;

import br.com.one.jobportal.dto.LoginRequest;
import br.com.one.jobportal.dto.RegisterRequest;
import br.com.one.jobportal.dto.response.UserProfileResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> login(LoginRequest loginRequest);
    ResponseEntity<?> register(RegisterRequest registerRequest, String role);
    ResponseEntity<?> registerEmployer(RegisterRequest registerRequest, String company);
    
    /**
     * Obtém o perfil do usuário autenticado
     * @return ResponseEntity com o perfil do usuário ou status de erro
     */
    ResponseEntity<UserProfileResponse> getUserProfile();
}
