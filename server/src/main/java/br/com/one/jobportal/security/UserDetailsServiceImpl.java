package br.com.one.jobportal.security;

import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Carrega o usuário com as coleções necessárias para autenticação
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
        
        // Inicializa as coleções que serão necessárias durante a autenticação
        // Isso evita LazyInitializationException quando as coleções forem acessadas posteriormente
        if (user.getApplications() != null) {
            user.getApplications().size(); // Força o carregamento da coleção
        }
        
        if (user.getPostedJobs() != null) {
            user.getPostedJobs().size(); // Força o carregamento da coleção
        }
        
        if (user.getSavedJobs() != null) {
            user.getSavedJobs().size(); // Força o carregamento da coleção
        }
        
        return user; // User implementa UserDetails, então podemos retorná-lo diretamente
    }
}
