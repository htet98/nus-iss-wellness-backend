package nus.iss.wellness.backend.service;

import nus.iss.wellness.backend.dto.request.LoginRequest;
import nus.iss.wellness.backend.dto.request.RegisterRequest;
import nus.iss.wellness.backend.dto.response.ApiResponse;
import nus.iss.wellness.backend.dto.response.LoginResponse;
import nus.iss.wellness.backend.model.Role;
import nus.iss.wellness.backend.model.User;
import nus.iss.wellness.backend.repository.UserProfileRepository;
import nus.iss.wellness.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

//Author: Junior

@Service
public class AuthService {
    private final JwtService jwtService;
    private final UserProfileRepository userProfileRepo;
    private final UserRepository userRepo;

    private final Path jwtOutputFile;
    private final int jwtOutputMaxEntries;

    public AuthService(JwtService jwtService, UserProfileRepository userProfileRepo, UserRepository userRepo,
                       @Value("${jwt.output-file:generated-jwts.txt}") String jwtOutputFile,
                       @Value("${jwt.output-max-entries:5}") int jwtOutputMaxEntries) {

        this.jwtService = jwtService;
        this.userProfileRepo = userProfileRepo;
        this.userRepo = userRepo;

        this.jwtOutputFile = Path.of(jwtOutputFile);
        this.jwtOutputMaxEntries = Math.max(1, jwtOutputMaxEntries);
    }

    // ==========================================================
    // REGISTER
    // ==========================================================
    public ApiResponse register(RegisterRequest request) {

        if (userRepo.existsByUsername(request.getUsername())) {
            return new ApiResponse(false, "Username already exists");
        }

        if (userRepo.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Email already exists");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(Role.USER);

        // SHA-512
        user.setPasswordHash(sha512(request.getPassword()));

        userRepo.save(user);

        return new ApiResponse(true, "Registration Successful");
    }

    // ==========================================================
    // LOGIN
    // ==========================================================
    public LoginResponse login(LoginRequest request) {

        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        String hashedPassword = sha512(request.getPassword());

        if (!user.getPasswordHash().equals(hashedPassword)) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        saveTokenToLocalFile(user.getUsername(), token);

        LoginResponse response = new LoginResponse();

        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        // add token field in LoginResponse.java
        response.setToken(token);

        return response;
    }

    // ==========================================================
    // SHA-512
    // ==========================================================
    private String sha512(String value) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-512");

            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // ==========================================================
    // SAVE TOKEN
    // ==========================================================
    private void saveTokenToLocalFile(String username, String token) {

        try {

            Path parent = jwtOutputFile.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = new ArrayList<>();

            if (Files.exists(jwtOutputFile)) {
                lines.addAll(Files.readAllLines(jwtOutputFile));
            }

            lines.add(LocalDateTime.now() + " | " + username + " | " + token);

            while (lines.size() > jwtOutputMaxEntries) {
                lines.remove(0);
            }

            Files.writeString(jwtOutputFile, String.join(System.lineSeparator(), lines) + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {

            throw new RuntimeException("JWT generated but could not be written to file", e);
        }
    }
}
