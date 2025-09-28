package br.com.one.jobportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permite credenciais
        config.setAllowCredentials(true);
        
        // Define as origens permitidas (substitua com as origens do seu frontend)
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:5173");
        
        // Define os métodos HTTP permitidos
        config.addAllowedMethod("*");
        
        // Define os cabeçalhos permitidos
        config.addAllowedHeader("*");
        
        // Expõe cabeçalhos personalizados
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Disposition");
        
        // Configura o tempo máximo de cache para pré-voo (preflight)
        config.setMaxAge(3600L);
        
        // Aplica a configuração a todos os caminhos
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
