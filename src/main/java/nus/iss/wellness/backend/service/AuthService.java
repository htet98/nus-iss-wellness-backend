package nus.iss.wellness.backend.service;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nus.iss.wellness.backend.dto.request.LoginRequest;
import nus.iss.wellness.backend.dto.request.RegisterRequest;
import nus.iss.wellness.backend.dto.response.ApiResponse;
import nus.iss.wellness.backend.dto.response.LoginResponse;
import nus.iss.wellness.backend.model.User;
import nus.iss.wellness.backend.repository.UserRepository;

//Author: Junior

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    /**
     * Register new user
     */
    public ApiResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return new ApiResponse(false, "Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Email already exists");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        user.setPasswordHash(request.getPassword());

        userRepository.save(user);

        return new ApiResponse(true, "Registration Successful");
    }

    /**
     * Login
     */
    public LoginResponse login(LoginRequest request) {

        Optional<User> optionalUser =
                userRepository.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        if (!user.getPasswordHash().equals(request.getPassword())) {
            return null;
        }

        LoginResponse response = new LoginResponse();

        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        return response;
    }
}
