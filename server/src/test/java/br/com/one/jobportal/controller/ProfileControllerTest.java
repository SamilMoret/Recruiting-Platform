package br.com.one.jobportal.controller;

import br.com.one.jobportal.config.StorageProperties;
import br.com.one.jobportal.dto.UpdateProfileRequest;
import br.com.one.jobportal.dto.UpdateResumeRequest;
import br.com.one.jobportal.dto.response.UserProfileResponse;
import br.com.one.jobportal.entity.User;
import br.com.one.jobportal.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProfileService profileService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ProfileController profileController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(profileController).build();
    }

    @Test
    void updateResume_ShouldReturnUpdatedUser() throws Exception {
        // Arrange
        UpdateResumeRequest request = new UpdateResumeRequest();
        request.setResumeUrl("http://example.com/resume.pdf");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setResume(request.getResumeUrl());

        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(profileService.updateUserResume(any(UserDetails.class), any(UpdateResumeRequest.class)))
                .thenReturn(user);

        // Act & Assert
        mockMvc.perform(put("/api/profile/resume")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(() -> "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.resume").value("http://example.com/resume.pdf"));
    }

    @Test
    void updateProfile_ShouldReturnUpdatedUser() throws Exception {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("Updated Name");
        request.setPhone("(11) 99999-9999");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Updated Name");
        user.setPhone("(11) 99999-9999");

        when(profileService.updateProfile(any(UserDetails.class), any(UpdateProfileRequest.class)))
                .thenReturn(user);

        // Act & Assert
        mockMvc.perform(put("/api/profile/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(() -> "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.phone").value("(11) 99999-9999"));
    }

    @Test
    void getMyProfile_ShouldReturnUserProfile() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");

        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(profileService.getMyProfile(any(UserDetails.class))).thenReturn(user);

        // Act & Assert
        mockMvc.perform(get("/api/profile/me")
                .principal(() -> "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getPublicProfile_ShouldReturnPublicProfile() throws Exception {
        // Arrange
        UserProfileResponse profileResponse = UserProfileResponse.builder()
                .id(1L)
                .name("Public User")
                .email("public@example.com")
                .build();

        when(profileService.getPublicProfile(1L)).thenReturn(profileResponse);

        // Act & Assert
        mockMvc.perform(get("/api/profile/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Public User"))
                .andExpect(jsonPath("$.email").value("public@example.com"));
    }

    @Test
    void uploadAvatar_ShouldReturnFilePath() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        when(profileService.uploadAvatar(any(UserDetails.class), any()))
                .thenReturn("/uploads/avatar_1_12345.jpg");

        // Act & Assert
        mockMvc.perform(multipart("/api/profile/upload-avatar")
                .file(file)
                .principal(() -> "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("/uploads/avatar_1_12345.jpg"));
    }
}
