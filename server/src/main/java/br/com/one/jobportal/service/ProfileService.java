package br.com.one.jobportal.service;

import br.com.one.jobportal.dto.UpdateResumeRequest;
import br.com.one.jobportal.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface ProfileService {
    User updateUserResume(UserDetails userDetails, UpdateResumeRequest request);
}
