package br.com.one.jobportal.controller;

import br.com.one.jobportal.dto.LoginRequest;
import br.com.one.jobportal.dto.RegisterRequest;
import br.com.one.jobportal.security.JwtService;
import br.com.one.jobportal.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    // ✅ Injete todas as dependências necessárias
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthService authService; // ← ADICIONE ESTA LINHA

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("Tentativa de login para o email: " + request.getEmail());
            
            // Autentica o usuário
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            System.out.println("Autenticação bem-sucedida para: " + request.getEmail());

            // Carrega os detalhes do usuário
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            System.out.println("Detalhes do usuário carregados: " + userDetails.getUsername());

            // Gera o token JWT
            String jwt = jwtService.generateToken(userDetails);
            System.out.println("Token JWT gerado com sucesso");

            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "type", "Bearer",
                    "email", request.getEmail()
            ));

        } catch (Exception e) {
            System.err.println("Erro durante o login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).body("Credenciais inválidas: " + e.getMessage());
        }
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