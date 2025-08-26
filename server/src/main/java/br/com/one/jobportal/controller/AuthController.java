package br.com.one.jobportal.controller;

import br.com.one.jobportal.dto.LoginRequest;
import br.com.one.jobportal.dto.RegisterRequest;
import br.com.one.jobportal.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register/candidate")
    public ResponseEntity<?> registerCandidate(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest, "CANDIDATE");
    }

    @PostMapping("/register/recruiter")
    public ResponseEntity<?> registerRecruiter(@Valid @RequestBody RegisterRequest registerRequest,
                                               @RequestParam String company) {
        return authService.registerRecruiter(registerRequest, company);
    }
}