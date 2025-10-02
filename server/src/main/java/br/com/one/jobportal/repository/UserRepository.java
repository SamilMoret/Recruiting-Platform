package br.com.one.jobportal.repository;

import br.com.one.jobportal.entity.User;
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

    // Busca usuários por papel (role)
    List<User> findByRole(User.Role role);

    // Busca apenas usuários ativos
    List<User> findByActiveTrue();

    // Busca usuários por nome (case-insensitive e parcial)
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);

    // Busca usuários por papel e status de ativação
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
}
