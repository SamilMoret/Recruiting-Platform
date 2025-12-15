package br.com.one.jobportal.service;

import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.exception.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User getCurrentUser();
    User updateUserProfile(User userDetails);
    User getUserById(Long id) throws ResourceNotFoundException;
    User updateUser(Long id, User userDetails) throws ResourceNotFoundException;
}
