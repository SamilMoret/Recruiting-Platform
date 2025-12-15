package br.com.one.jobportal.service.impl;

import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.exception.ResourceNotFoundException;
import br.com.one.jobportal.repository.UserRepository;
import br.com.one.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return (User) loadUserByUsername(authentication.getName());
    }

    @Override
    @Transactional
    public User updateUserProfile(User userDetails) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new ResourceNotFoundException("Usuário não autenticado");
        }
        
        // Atualiza apenas os campos permitidos
        if (userDetails.getName() != null) {
            currentUser.setName(userDetails.getName());
        }
        if (userDetails.getPhone() != null) {
            currentUser.setPhone(userDetails.getPhone());
        }
        if (userDetails.getAvatar() != null) {
            currentUser.setAvatar(userDetails.getAvatar());
        }
        if (userDetails.getResume() != null) {
            currentUser.setResume(userDetails.getResume());
        }
        if (userDetails.getCompanyName() != null) {
            currentUser.setCompanyName(userDetails.getCompanyName());
        }
        if (userDetails.getCompanyDescription() != null) {
            currentUser.setCompanyDescription(userDetails.getCompanyDescription());
        }
        
        return userRepository.save(currentUser);
    }

    @Override
    public User getUserById(Long id) throws ResourceNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
    }

    @Override
    @Transactional
    public User updateUser(Long id, User userDetails) throws ResourceNotFoundException {
        User user = getUserById(id);
        
        // Atualiza os campos permitidos
        if (userDetails.getName() != null) {
            user.setName(userDetails.getName());
        }
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPhone() != null) {
            user.setPhone(userDetails.getPhone());
        }
        if (userDetails.getAvatar() != null) {
            user.setAvatar(userDetails.getAvatar());
        }
        if (userDetails.getResume() != null) {
            user.setResume(userDetails.getResume());
        }
        if (userDetails.getCompanyName() != null) {
            user.setCompanyName(userDetails.getCompanyName());
        }
        if (userDetails.getCompanyDescription() != null) {
            user.setCompanyDescription(userDetails.getCompanyDescription());
        }
        
        return userRepository.save(user);
    }
}
