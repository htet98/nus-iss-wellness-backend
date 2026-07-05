package nus.iss.wellness.backend.service;

import nus.iss.wellness.backend.model.User;
import nus.iss.wellness.backend.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

//author: Junior

@Service
public class JwtCleanupService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public JwtCleanupService(UserRepository userRepository,
                             JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void clearExpiredTokens() {

        List<User> users = userRepository.findAll();

        for (User user : users) {

            String token = user.getJwtToken();

            if (token == null || token.isBlank()) {
                continue;
            }

            try {

                // Validates signature and expiration
                jwtService.authenticateToken(token);

            } catch (Exception e) {

                // Token expired or invalid
                user.setJwtToken(null);
                userRepository.save(user);
            }
        }
    }
}