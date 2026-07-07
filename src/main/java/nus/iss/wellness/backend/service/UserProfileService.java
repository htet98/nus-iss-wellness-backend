package nus.iss.wellness.backend.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import nus.iss.wellness.backend.dto.request.UserProfileRequest;
import nus.iss.wellness.backend.dto.response.UserProfileResponse;
import nus.iss.wellness.backend.exception.ResourceNotFoundException;
import nus.iss.wellness.backend.model.User;
import nus.iss.wellness.backend.model.UserProfile;
import nus.iss.wellness.backend.repository.UserProfileRepository;
import nus.iss.wellness.backend.repository.UserRepository;

//author: Junior

@Service
@Transactional
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;

    public UserProfileService(
            UserRepository userRepository,
            UserProfileRepository profileRepository) {

        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    // =====================================================
    // Get Profile
    // =====================================================

    public UserProfileResponse getProfile(String username) {

        User user = getUser(username);

        UserProfile profile = getProfileEntity(user);

        return convertToResponse(profile);
    }

    // =====================================================
    // Update Profile
    // =====================================================

    public UserProfileResponse updateProfile(
            String username,
            UserProfileRequest request) {

        validateProfileRequest(request);

        User user = getUser(username);

        // Create profile if it doesn't exist
        UserProfile profile = profileRepository
                .findByUser(user)
                .orElseGet(() -> {

                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);

                    return newProfile;
                });

        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setGender(request.getGender());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setAddress(request.getAddress());
        profile.setHeightCm(request.getHeightCm());
        profile.setWeightKg(request.getWeightKg());
        profile.setFitnessGoal(request.getFitnessGoal());

        UserProfile updatedProfile = profileRepository.save(profile);

        return convertToResponse(updatedProfile);
    }

    // =====================================================
    // Delete Profile
    // =====================================================

    public void deleteProfile(String username) {

        User user = getUser(username);

        UserProfile profile = getProfileEntity(user);

        profileRepository.delete(profile);
    }

    // =====================================================
    // Helper Methods
    // =====================================================

    private User getUser(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."));
    }

    private UserProfile getProfileEntity(User user) {

        return profileRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Profile not found."));
    }

    private void validateProfileRequest(
            UserProfileRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Profile request cannot be null.");
        }

        if (request.getFirstName() == null ||
                request.getFirstName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "First name is required.");
        }

        if (request.getLastName() == null ||
                request.getLastName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Last name is required.");
        }
    }

    private UserProfileResponse convertToResponse(
            UserProfile profile) {

        UserProfileResponse response = new UserProfileResponse();

        response.setId(profile.getId());
        response.setUsername(profile.getUser().getUsername());
        response.setFirstName(profile.getFirstName());
        response.setLastName(profile.getLastName());
        response.setGender(profile.getGender());
        response.setDateOfBirth(profile.getDateOfBirth());
        response.setAddress(profile.getAddress());
        response.setHeightCm(profile.getHeightCm());
        response.setWeightKg(profile.getWeightKg());
        response.setFitnessGoal(profile.getFitnessGoal());

        return response;
    }
}