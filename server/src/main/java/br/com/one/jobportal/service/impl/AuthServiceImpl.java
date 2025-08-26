package br.com.one.jobportal.service.impl;

import br.com.one.jobportal.dto.LoginRequest;
import br.com.one.jobportal.dto.RegisterRequest;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.repository.UserRepository;
import br.com.one.jobportal.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> login(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), userOptional.get().getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Credenciais inválidas"));
        }

        User user = userOptional.get();
        return ResponseEntity.ok(Map.of(
                "message", "Login realizado com sucesso",
                "user", user
        ));
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

        try {
            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            user.setRole(userRole);
        } catch (IllegalArgumentException e) {
            user.setRole(User.Role.CANDIDATE);
        }

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
        user.setRole(User.Role.RECRUITER);
        user.setCompany(company);

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Recrutador criado com sucesso"));
    }
}
