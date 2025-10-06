package br.com.one.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableAsync
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class RecruitingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruitingPlatformApplication.class, args);
        System.out.println(" Plataforma de Recrutamento iniciada com sucesso!");
        System.out.println("  Servidor rodando em: http://localhost:5001");
    }
}