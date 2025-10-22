package com.chatapp.authservice.controller;

import com.chatapp.authservice.controller.payload.LoginRequest;
import com.chatapp.authservice.controller.payload.OtpVerificationRequest;
import com.chatapp.authservice.controller.payload.SignupRequest;
import com.chatapp.authservice.controller.payload.TokenRefreshRequest;
import com.chatapp.authservice.model.ERole;
import com.chatapp.authservice.model.User;
import com.chatapp.authservice.repository.RoleRepository;
import com.chatapp.authservice.repository.UserRepository;
import org.springframework.security.test.context.support.WithMockUser;
import com.chatapp.authservice.service.OtpService;
import com.chatapp.authservice.service.RefreshTokenService;
import com.chatapp.authservice.service.UserDetailsServiceImpl;
import com.chatapp.authservice.service.dto.UserDetailsImpl;
import com.chatapp.authservice.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private com.chatapp.authservice.repository.UserRepository userRepository;

    @MockBean
    private com.chatapp.authservice.repository.RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private OtpService otpService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void whenValidSignupRequest_thenReturnsSuccessMessage() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("testuser@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setRole(Set.of(ERole.ROLE_USER));

        when(roleRepository.findByName(any())).thenReturn(java.util.Optional.of(new com.chatapp.authservice.model.Role()));
        doNothing().when(otpService).createAndSendOtp(any());

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully! Please check your email for the OTP."));
    }

    @Test
    void whenUsernameIsTaken_thenReturnsBadRequest() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("testuser@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setRole(Set.of(ERole.ROLE_USER));

        when(userRepository.existsByUsername(any())).thenReturn(true);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Username is already taken!"));
    }

    @Test
    void whenEmailIsTaken_thenReturnsBadRequest() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("testuser@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setRole(Set.of(ERole.ROLE_USER));

        when(userRepository.existsByEmail(any())).thenReturn(true);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already in use!"));
    }

    @Test
    void whenValidOtp_thenReturnsSuccessMessage() throws Exception {
        OtpVerificationRequest otpVerificationRequest = new OtpVerificationRequest();
        otpVerificationRequest.setEmail("testuser@example.com");
        otpVerificationRequest.setOtp("123456");

        User user = new User();
        user.setActive(false);

        when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.of(user));
        when(otpService.verifyOtp(any(), any())).thenReturn(true);

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otpVerificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email verified successfully!"));
    }

    @Test
    void whenInvalidOtp_thenReturnsBadRequest() throws Exception {
        OtpVerificationRequest otpVerificationRequest = new OtpVerificationRequest();
        otpVerificationRequest.setEmail("testuser@example.com");
        otpVerificationRequest.setOtp("wrong-otp");

        User user = new User();
        user.setActive(false);

        when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.of(user));
        when(otpService.verifyOtp(any(), any())).thenReturn(false);

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otpVerificationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Invalid OTP!"));
    }

    @Test
    void whenValidSigninRequest_thenReturnsJwtResponse() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        User user = new User();
        user.setActive(true);

        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "testuser", "testuser@example.com", "password123", java.util.Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);

        when(userRepository.findByUsername(any())).thenReturn(java.util.Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(any())).thenReturn("test-jwt");
        when(refreshTokenService.createRefreshToken(any())).thenReturn(new com.chatapp.authservice.model.RefreshToken());

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt"));
    }

    @Test
    void whenUserIsInactive_thenReturnsBadRequest() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        User user = new User();
        user.setActive(false);

        when(userRepository.findByUsername(any())).thenReturn(java.util.Optional.of(user));

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Please verify your email before logging in."));
    }

    @Test
    void whenValidRefreshToken_thenReturnsNewAccessToken() throws Exception {
        TokenRefreshRequest tokenRefreshRequest = new TokenRefreshRequest();
        tokenRefreshRequest.setRefreshToken("valid-refresh-token");

        com.chatapp.authservice.model.RefreshToken refreshToken = new com.chatapp.authservice.model.RefreshToken();
        refreshToken.setUser(new User());

        when(refreshTokenService.findByToken(any())).thenReturn(java.util.Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(any())).thenReturn(refreshToken);
        when(jwtUtils.generateTokenFromUsername(any())).thenReturn("new-access-token");

        mockMvc.perform(post("/api/auth/refreshtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRefreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"));
    }

    @Test
    void whenInvalidRefreshToken_thenReturnsForbidden() throws Exception {
        TokenRefreshRequest tokenRefreshRequest = new TokenRefreshRequest();
        tokenRefreshRequest.setRefreshToken("invalid-refresh-token");

        when(refreshTokenService.findByToken(any())).thenThrow(new com.chatapp.authservice.exception.TokenRefreshException("invalid-refresh-token", "Invalid refresh token"));

        mockMvc.perform(post("/api/auth/refreshtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRefreshRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenLogout_thenReturnsSuccessMessage() throws Exception {
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "testuser", "testuser@example.com", "password123", java.util.Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);

        when(refreshTokenService.deleteByUserId(any())).thenReturn(1);

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Log out successful!"));
    }
}
