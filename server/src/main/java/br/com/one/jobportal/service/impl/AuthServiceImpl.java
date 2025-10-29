package br.com.one.jobportal.service.impl;

import br.com.one.jobportal.dto.LoginRequest;
import br.com.one.jobportal.dto.RegisterRequest;
import br.com.one.jobportal.dto.response.UserProfileResponse;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.exception.ResourceNotFoundException;
import br.com.one.jobportal.repository.UserRepository;
import br.com.one.jobportal.security.JwtService;
import br.com.one.jobportal.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public ResponseEntity<?> login(LoginRequest loginRequest) {
        try {
            var userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

            if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Credenciais inválidas"));
            }

            String token = jwtService.generateToken(userDetails);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "type", "Bearer",
                    "email", userDetails.getUsername()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Credenciais inválidas"));
        }
    }

    @Override
    public ResponseEntity<?> register(RegisterRequest registerRequest, String role) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email já cadastrado"));
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhone(registerRequest.getPhone());

        // Define o avatar se estiver presente na requisição
        if (registerRequest.getAvatar() != null && !registerRequest.getAvatar().trim().isEmpty()) {
            user.setAvatar(registerRequest.getAvatar().trim());
        }

        // Define o resume se estiver presente na requisição e for JOB_SEEKER
        if (role.equalsIgnoreCase("JOB_SEEKER") && registerRequest.getResume() != null && !registerRequest.getResume().trim().isEmpty()) {
            user.setResume(registerRequest.getResume().trim());
        }

        // Use the fromString method for case-insensitive role conversion
        user.setRole(User.Role.fromString(role));
        
        // Salva o usuário no banco de dados
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Usuário criado com sucesso"));
    }

    @Override
    public ResponseEntity<?> registerEmployer(RegisterRequest registerRequest, String company) {
        // Implementação existente
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhone(registerRequest.getPhone());
        user.setRole(User.Role.EMPLOYER);
        
        // Define o avatar se estiver presente na requisição
        if (registerRequest.getAvatar() != null && !registerRequest.getAvatar().trim().isEmpty()) {
            user.setAvatar(registerRequest.getAvatar().trim());
        }
        
        // Define os dados da empresa
        if (company != null && !company.trim().isEmpty()) {
            user.setCompanyName(company);
        }
        
        if (registerRequest.getCompanyDescription() != null) {
            user.setCompanyDescription(registerRequest.getCompanyDescription());
        }
        
        if (registerRequest.getCompanyLogo() != null) {
            user.setCompanyLogo(registerRequest.getCompanyLogo());
        }

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Empregador criado com sucesso"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        try {
            // Obtém o usuário autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            // Busca o usuário no banco de dados
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + email));
                    
            // Converte para DTO de resposta
            return ResponseEntity.ok(UserProfileResponse.fromEntity(user));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}