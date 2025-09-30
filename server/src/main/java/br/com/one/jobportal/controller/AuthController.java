package br.com.one.jobportal.controller;

import br.com.one.jobportal.dto.LoginRequest;
import br.com.one.jobportal.dto.RegisterRequest;
import br.com.one.jobportal.dto.response.LoginResponse;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.repository.UserRepository;
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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // ✅ Injete todas as dependências necessárias
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthService authService;

    private final UserRepository userRepository;

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

            // Busca o usuário completo para obter os dados adicionais
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Gera o token JWT
            String jwt = jwtService.generateToken(userDetails);
            System.out.println("Token JWT gerado com sucesso");

            // Cria a resposta personalizada
            LoginResponse response = LoginResponse.fromUserAndToken(user, jwt);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Erro durante o login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).body("Credenciais inválidas: " + e.getMessage());
        }
    }

    @PostMapping("/register/jobseeker")
    public ResponseEntity<?> registerJobSeeker(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest, "JOB_SEEKER");
    }

    @PostMapping("/register/employer")
    public ResponseEntity<?> registerEmployer(@Valid @RequestBody RegisterRequest registerRequest,
                                            @RequestParam(required = false) String company) {
        return authService.registerEmployer(registerRequest, company);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (registerRequest.getRole() == null) {
            return ResponseEntity.badRequest().body("O campo 'role' é obrigatório. Valores aceitos: 'JOB_SEEKER' ou 'EMPLOYER'");
        }
        
        // Normaliza o role (remove underscore se existir e converte para maiúsculas)
        String normalizedRole = registerRequest.getRole().toUpperCase().replace("_", "");
        
        if ("JOBSEEKER".equals(normalizedRole)) {
            return authService.register(registerRequest, "JOB_SEEKER");
        } else if ("EMPLOYER".equals(normalizedRole)) {
            // Para EMPLOYER, o nome da empresa pode ser o nome do usuário por padrão
            // ou pode ser solicitado em um campo separado no frontend
            return authService.registerEmployer(registerRequest, registerRequest.getName());
        } else {
            return ResponseEntity.badRequest().body("Valor inválido para o campo 'role'. Use 'JOB_SEEKER' (ou 'JOBSEEKER') ou 'EMPLOYER'");
        }
    }
}
