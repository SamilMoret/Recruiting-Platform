package br.com.one.jobportal.service.impl;

import br.com.one.jobportal.dto.LoginRequest;
import br.com.one.jobportal.dto.RegisterRequest;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.repository.UserRepository;
import br.com.one.jobportal.security.JwtService;
import br.com.one.jobportal.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        // Use the fromString method for case-insensitive role conversion
        user.setRole(User.Role.fromString(role));

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Usuário criado com sucesso"));
    }

    @Override
    public ResponseEntity<?> registerRecruiter(RegisterRequest registerRequest, String company) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email já cadastrado"));
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhone(registerRequest.getPhone());
        user.setRole(User.Role.EMPLOYER);
        user.setCompanyName(company);

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Recrutador criado com sucesso"));
    }
}