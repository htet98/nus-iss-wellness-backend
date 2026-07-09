package nus.iss.wellness.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import nus.iss.wellness.backend.dto.request.LoginRequest;
import nus.iss.wellness.backend.dto.request.RegisterRequest;
import nus.iss.wellness.backend.dto.response.ApiResponse;
import nus.iss.wellness.backend.dto.response.LoginResponse;
import nus.iss.wellness.backend.security.JwtAuthenticationFilter;
import nus.iss.wellness.backend.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

//author: Junior

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    //REGISTER
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    //LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    //LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            authService.logout(token);
        }

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                buildLogoutCookie().toString()
        );

        Map<String, String> result = new HashMap<>();
        result.put("message", "Logged out");

        return ResponseEntity.ok(result);
    }

    private ResponseCookie buildLoginCookie(String token) {
        return ResponseCookie.from(JwtAuthenticationFilter.AUTH_COOKIE_NAME, token)
                .httpOnly(true)
                .path("/")
                .maxAge(30 * 60)
                .sameSite("Strict")
                .build();
    }

    private ResponseCookie buildLogoutCookie() {
        return ResponseCookie.from(JwtAuthenticationFilter.AUTH_COOKIE_NAME, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}
