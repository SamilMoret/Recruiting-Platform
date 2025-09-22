package br.com.one.jobportal.repository;

public interface CustomUserRepository {
    boolean existsByEmailIgnoreCase(String email);
    
    // Método de compatibilidade para o código existente
    default boolean existsByEmail(String email) {
        return existsByEmailIgnoreCase(email);
    }
}
