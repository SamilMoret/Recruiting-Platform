package br.com.one.jobportal.service;

import br.com.one.jobportal.dto.UpdateProfileRequest;
import br.com.one.jobportal.dto.UpdateResumeRequest;
import br.com.one.jobportal.dto.response.UserProfileResponse;
import br.com.one.jobportal.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileService {
    User updateUserResume(UserDetails userDetails, UpdateResumeRequest request);
    
    User updateProfile(UserDetails userDetails, UpdateProfileRequest request);
    
    UserProfileResponse getPublicProfile(Long userId);
    
    String uploadAvatar(UserDetails userDetails, MultipartFile file) throws IOException;
    
    /**
     * Obtém o perfil do usuário autenticado
     * @param userDetails Detalhes do usuário autenticado
     * @return Usuário autenticado
     */
    User getMyProfile(UserDetails userDetails);
}
