package nus.iss.wellness.backend.service;

import io.jsonwebtoken.Claims;
import nus.iss.wellness.backend.dto.request.LoginRequest;
import nus.iss.wellness.backend.dto.request.RegisterRequest;
import nus.iss.wellness.backend.dto.response.ApiResponse;
import nus.iss.wellness.backend.dto.response.LoginResponse;
import nus.iss.wellness.backend.exception.BadRequestException;
import nus.iss.wellness.backend.exception.EmailAlreadyExistsException;
import nus.iss.wellness.backend.exception.UsernameAlreadyExistsException;
import nus.iss.wellness.backend.model.Role;
import nus.iss.wellness.backend.model.User;
import nus.iss.wellness.backend.repository.UserProfileRepository;
import nus.iss.wellness.backend.repository.UserRepository;
import nus.iss.wellness.backend.security.HashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//author: Junior

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {

        authService = new AuthService(
                jwtService,
                userProfileRepository,
                userRepository,
                "generated-jwts.txt",
                5
        );
    }

    // REGISTER
    @Test
    void registerShouldSuccess() {

        RegisterRequest request = new RegisterRequest();

        request.setUsername("Junior");
        request.setEmail("junior@test.com");
        request.setPassword("Junior");
        request.setConfirmPassword("Junior");

        when(userRepository.existsByUsername("Junior"))
                .thenReturn(false);

        when(userRepository.existsByEmail("junior@test.com"))
                .thenReturn(false);

        ApiResponse response = authService.register(request);

        assertTrue(response.isSuccess());
        assertEquals("Registration Successful", response.getMessage());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerShouldRejectPasswordMismatch() {

        RegisterRequest request = new RegisterRequest();

        request.setUsername("Junior");
        request.setEmail("junior@test.com");
        request.setPassword("123456");
        request.setConfirmPassword("654321");

        assertThrows(
                BadRequestException.class,
                () -> authService.register(request)
        );
    }

    @Test
    void registerShouldRejectDuplicateUsername() {

        RegisterRequest request = new RegisterRequest();

        request.setUsername("Junior");
        request.setEmail("junior@test.com");
        request.setPassword("Junior");
        request.setConfirmPassword("Junior");

        when(userRepository.existsByUsername("Junior"))
                .thenReturn(true);

        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> authService.register(request)
        );
    }

    @Test
    void registerShouldRejectDuplicateEmail() {

        RegisterRequest request = new RegisterRequest();

        request.setUsername("Junior");
        request.setEmail("junior@test.com");
        request.setPassword("Junior");
        request.setConfirmPassword("Junior");

        when(userRepository.existsByUsername("Junior"))
                .thenReturn(false);

        when(userRepository.existsByEmail("junior@test.com"))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(request)
        );
    }

    // LOGIN
    @Test
    void loginShouldSuccess() {

        LoginRequest request = new LoginRequest();

        request.setUsername("Junior");
        request.setPassword("Junior");

        User user = new User();

        user.setUserId(1L);
        user.setUsername("Junior");
        user.setEmail("junior@test.com");
        user.setRole(Role.USER);
        user.setPasswordHash(HashUtil.sha512("Junior"));

        when(userRepository.findByUsername("Junior"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken("Junior", "USER"))
                .thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("Junior", response.getUsername());
        assertEquals("junior@test.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
        assertEquals("jwt-token", response.getToken());

        verify(userRepository).save(user);
    }

    @Test
    void loginShouldRejectWrongPassword() {

        LoginRequest request = new LoginRequest();

        request.setUsername("Junior");
        request.setPassword("WrongPassword");

        User user = new User();

        user.setUsername("Junior");
        user.setPasswordHash(HashUtil.sha512("Junior"));

        when(userRepository.findByUsername("Junior"))
                .thenReturn(Optional.of(user));

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );
    }

    @Test
    void loginShouldRejectUnknownUser() {

        LoginRequest request = new LoginRequest();

        request.setUsername("Junior");
        request.setPassword("Junior");

        when(userRepository.findByUsername("Junior"))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );
    }

    // LOGOUT
    @Test
    void logoutShouldSuccess() {

        String token = "jwt-token";

        Claims claims = mock(Claims.class);

        User user = new User();

        user.setUsername("Junior");
        user.setJwtToken(token);

        when(jwtService.authenticateToken(token))
                .thenReturn(claims);

        when(claims.getSubject())
                .thenReturn("Junior");

        when(userRepository.findByUsername("Junior"))
                .thenReturn(Optional.of(user));

        authService.logout(token);

        assertNull(user.getJwtToken());

        verify(userRepository).save(user);
    }

    @Test
    void logoutShouldRejectUnknownUser() {

        String token = "jwt-token";

        Claims claims = mock(Claims.class);

        when(jwtService.authenticateToken(token))
                .thenReturn(claims);

        when(claims.getSubject())
                .thenReturn("Junior");

        when(userRepository.findByUsername("Junior"))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.logout(token)
        );
    }
}