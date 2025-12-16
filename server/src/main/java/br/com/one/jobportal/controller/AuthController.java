package br.com.one.jobportal.controller;

import br.com.one.jobportal.dto.LoginRequest;
import br.com.one.jobportal.dto.RegisterRequest;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para gerenciar autenticação e registro de usuários")
public class AuthController {

    // ✅ Injete todas as dependências necessárias
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Operation(summary = "Autenticar usuário", 
               description = "Realiza o login de um usuário e retorna um token JWT para autenticação",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                              content = @Content(mediaType = "application/json", 
                              examples = @ExampleObject(value = """
                              {
                                  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                  "type": "Bearer",
                                  "email": "usuario@exemplo.com"
                              }""")))
               })
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

    @Operation(summary = "Registrar novo candidato", 
               description = "Cria uma nova conta de candidato (Job Seeker)",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Candidato registrado com sucesso"),
                   @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
               })
    @PostMapping("/register/jobseeker")
    public ResponseEntity<?> registerJobSeeker(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest, "JOB_SEEKER");
    }

    @Operation(summary = "Registrar novo empregador", 
               description = "Cria uma nova conta de empregador (Employer) com uma empresa associada",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Empregador registrado com sucesso"),
                   @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
               })
    @PostMapping("/register/employer")
    public ResponseEntity<?> registerEmployer(@Valid @RequestBody RegisterRequest registerRequest,
                                            @RequestParam(required = false) String company) {
        return authService.registerEmployer(registerRequest, company);
    }
    
    @Operation(summary = "Registro genérico de usuário", 
               description = "Cria uma nova conta de usuário. O tipo de conta é definido pelo parâmetro 'role' (JOB_SEEKER ou EMPLOYER)",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso"),
                   @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou role não especificada")
               })
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
