package br.com.one.jobportal.service;

import br.com.one.jobportal.dto.LoginRequest;
import br.com.one.jobportal.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> login(LoginRequest loginRequest);
    ResponseEntity<?> register(RegisterRequest registerRequest, String role);
    ResponseEntity<?> registerRecruiter(RegisterRequest registerRequest, String company);
}
