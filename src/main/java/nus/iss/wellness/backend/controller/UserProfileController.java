package nus.iss.wellness.backend.controller;

import nus.iss.wellness.backend.model.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import nus.iss.wellness.backend.dto.request.UserProfileRequest;
import nus.iss.wellness.backend.dto.response.UserProfileResponse;
import nus.iss.wellness.backend.service.UserProfileService;

import nus.iss.wellness.backend.repository.UserRepository;
import nus.iss.wellness.backend.exception.ResourceNotFoundException;

//author: Junior

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserRepository userRepository;

    public UserProfileController(
            UserProfileService userProfileService,
            UserRepository userRepository) {

        this.userProfileService = userProfileService;
        this.userRepository = userRepository;
    }

    // =====================================================
    // Get Profile
    // =====================================================

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        UserProfileResponse response =
                userProfileService.getProfile(user.getUsername());

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // Update Profile
    // =====================================================

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UserProfileRequest request) {

        Long userId = (Long) authentication.getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        UserProfileResponse response =
                userProfileService.updateProfile(
                        user.getUsername(),
                        request);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // Delete Profile
    // =====================================================

    @DeleteMapping
    public ResponseEntity<String> deleteProfile(Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        userProfileService.deleteProfile(user.getUsername());

        return ResponseEntity.ok("Profile deleted successfully.");
    }
}
