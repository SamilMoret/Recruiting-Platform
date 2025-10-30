package br.com.one.jobportal.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfig {
    
    @Bean
    @Primary
    public StorageProperties storageProperties() {
        return new StorageProperties();
    }
}
