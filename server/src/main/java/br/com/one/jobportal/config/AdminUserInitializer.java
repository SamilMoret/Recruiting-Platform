package br.com.one.jobportal.config;

import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app.admin")
public class AdminUserInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    private String email;
    private String password;
    private String name;

    @Bean
    @Profile("!test") // Não executa em ambiente de teste
    public CommandLineRunner initAdminUser() {
        return args -> {
            // Verifica se já existe um admin
            if (userRepository.findByEmail(email).isEmpty()) {
                User admin = User.builder()
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(User.Role.ADMIN)
                    .active(true)
                    .phone("(00) 00000-0000")
                    .companyName("Job Portal")
                    .companyDescription("Administrador do sistema")
                    .build();
                
                userRepository.save(admin);
                log.info("Usuário administrador criado com sucesso! Email: {}", email);
            }
        };
    }

    // Getters e Setters necessários para o @ConfigurationProperties
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }
}
