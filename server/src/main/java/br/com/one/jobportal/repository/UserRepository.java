package br.com.one.jobportal.repository;

import br.com.one.jobportal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
    
    // Busca um usuário por email (case-insensitive)
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmail(@Param("email") String email);
    
    // Busca usuários por papel (role) com paginação
    Page<User> findByRole(User.Role role, Pageable pageable);
    
    // Busca apenas usuários ativos com paginação
    Page<User> findByActiveTrue(Pageable pageable);
    
    // Busca usuários por nome (case-insensitive e parcial) com paginação
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<User> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    // Busca usuários por papel e status de ativação com paginação
    Page<User> findByRoleAndActive(User.Role role, boolean active, Pageable pageable);
    
    // Métodos antigos mantidos para compatibilidade
    @Deprecated
    List<User> findByRole(User.Role role);
    
    @Deprecated
    List<User> findByActiveTrue();
    
    @Deprecated
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);
    
    @Deprecated
    List<User> findByRoleAndActive(User.Role role, boolean active);
    
    // Conta usuários por papel
    Long countByRole(User.Role role);
    
    // Busca usuário com suas vagas publicadas (para carregamento antecipado)
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.postedJobs WHERE u.id = :id")
    Optional<User> findByIdWithPostedJobs(@Param("id") Long id);
    
    // Busca usuário com suas candidaturas (para carregamento antecipado)
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.applications WHERE u.id = :id")
    Optional<User> findByIdWithApplications(@Param("id") Long id);
    
    // Busca usuário com suas vagas salvas (para carregamento antecipado)
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.savedJobs WHERE u.id = :id")
    Optional<User> findByIdWithSavedJobs(@Param("id") Long id);
    
    // Métodos para Admin com paginação
    Page<User> findByActive(Boolean active, Pageable pageable);
    Long countByActive(Boolean active);
    
    // Método antigo mantido para compatibilidade
    @Deprecated
    List<User> findByActive(Boolean active);
}
