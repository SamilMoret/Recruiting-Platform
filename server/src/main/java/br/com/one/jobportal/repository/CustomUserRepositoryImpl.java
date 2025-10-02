package br.com.one.jobportal.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        try {
            String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(:email)";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("email", email);
            long count = ((Number)query.getSingleResult()).longValue();
            return count > 0;
        } catch (Exception e) {
            // Log the error for debugging
            e.printStackTrace();
            return false;
        }
    }
}
