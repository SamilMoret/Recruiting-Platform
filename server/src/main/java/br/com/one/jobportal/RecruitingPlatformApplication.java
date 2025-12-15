package br.com.one.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RecruitingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruitingPlatformApplication.class, args);
        System.out.println("\n🚀 Plataforma de Recrutamento iniciada com sucesso!");
        System.out.println("📍 Servidor rodando em: http://localhost:5001");
        System.out.println("📚 Documentação da API (Swagger UI): http://localhost:5001/swagger-ui.html");
        System.out.println("📖 Documentação da API (OpenAPI): http://localhost:5001/v3/api-docs\n");
    }
}