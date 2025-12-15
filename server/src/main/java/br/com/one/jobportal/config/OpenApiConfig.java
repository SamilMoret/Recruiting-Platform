package br.com.one.jobportal.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.Arrays;
import java.util.Map;

// src/main/java/br/com/one/jobportal/config/OpenApiConfig.java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                                .addSchemas("JobRequest", new Schema<Map<String, Object>>()
                                        .addProperty("title", new Schema<String>().example("Desenvolvedor Java"))
                                        .addProperty("description", new Schema<String>().example("Desenvolvimento de aplicações Java com Spring Boot"))
                                        .addProperty("requirements", new Schema<String>().example("Conhecimento em Java, Spring Boot, JPA, Hibernate"))
                                        .addProperty("salaryMin", new Schema<Double>().example(5000.0))
                                        .addProperty("salaryMax", new Schema<Double>().example(8000.0))
                                        .addProperty("location", new Schema<String>().example("Remoto"))
                                        .addProperty("employmentType", new Schema<String>().example("CLT")._enum(Arrays.asList("CLT", "PJ", "Freelance")))
                                        .addProperty("isRemote", new Schema<Boolean>().example(true))
                                        .addProperty("active", new Schema<Boolean>().example(true)))
                )
                .info(new Info()
                        .title("Recruiting Platform API")
                        .description("""
                                ## Documentação da API de Recrutamento
                                
                                Esta API permite gerenciar vagas de emprego, candidaturas e perfis de usuários.
                                
                                ### Autenticação
                                A API usa autenticação JWT. Para obter um token, faça login no endpoint `/api/auth/login`.
                                
                                ### Exemplos de Uso
                                - **Criar uma vaga**: `POST /api/jobs`
                                - **Listar vagas**: `GET /api/jobs`
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Suporte")
                                .email("suporte@recruitingplatform.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                // Add common responses
                ApiResponse unauthorizedResponse = new ApiResponse()
                        .description("Não autorizado - Token JWT inválido ou ausente");

                ApiResponse forbiddenResponse = new ApiResponse()
                        .description("Acesso negado - Permissões insuficientes");

                operation.getResponses()
                        .addApiResponse("401", unauthorizedResponse)
                        .addApiResponse("403", forbiddenResponse);

                // Add security to all endpoints except auth
                if (operation.getOperationId() != null &&
                        !operation.getOperationId().contains("login") &&
                        !operation.getOperationId().contains("register")) {
                    operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
                }
            }));
        };
    }
}