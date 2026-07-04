package nus.iss.wellness.backend.service.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import nus.iss.wellness.backend.dto.response.DashboardResponse;
import nus.iss.wellness.backend.model.User;
import nus.iss.wellness.backend.model.UserProfile;
import nus.iss.wellness.backend.model.WellnessRecord;
import nus.iss.wellness.backend.repository.AiRecommendationRepository;
import nus.iss.wellness.backend.repository.UserProfileRepository;
import nus.iss.wellness.backend.repository.UserRepository;
import nus.iss.wellness.backend.repository.WellnessRecordRepository;
import nus.iss.wellness.backend.service.DashboardService;


//Author: Cecil

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final WellnessRecordRepository wellnessRepository;
    private final UserProfileRepository userProfileRepository;
    private final AiRecommendationRepository recommendationRepository;

    public DashboardServiceImpl(
            UserRepository userRepository,
            WellnessRecordRepository wellnessRepository,
            UserProfileRepository userProfileRepository,
            AiRecommendationRepository recommendationRepository
            ) 
    {

        this.userRepository = userRepository;
        this.wellnessRepository = wellnessRepository;
        this.userProfileRepository = userProfileRepository;
        this.recommendationRepository = recommendationRepository;
    }

    @Override
    public DashboardResponse getDashboard(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        DashboardResponse dto = new DashboardResponse();

        
        dto.setUsername(user.getUsername());
        
        
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        dto.setfullName(profile.getFirstName() + " " + profile.getLastName());
        
  
        

        // Query today's sleep  OrderByRecordDateDesc
        wellnessRepository
        .findTopByUserAndCategoryOrderByRecordDateDesc(
                user,
                WellnessRecord.Category.sleep
                )
        .ifPresent(record ->
                dto.setSleepHours(record.getValue()));

        // Query today's exercise
        wellnessRepository
        .findTopByUserAndCategoryOrderByRecordDateDesc(
                user,
                WellnessRecord.Category.exercise
                )
        .ifPresent(record ->
                dto.setExerciseMinutes(
                        record.getDurationMinutes()));

        // Query today's water
        wellnessRepository
        .findTopByUserAndCategoryOrderByRecordDateDesc(
                user,
                WellnessRecord.Category.water
                )
        .ifPresent(record ->
                dto.setWaterIntake(record.getValue()));
        
     // Query today's Steps
        wellnessRepository
        .findTopByUserAndCategoryOrderByRecordDateDesc(
                user,
                WellnessRecord.Category.steps
                )
        .ifPresent(record ->
                dto.setSteps(record.getValue()));

        // Query today's Mood
        wellnessRepository
        .findTopByUserAndCategoryOrderByRecordDateDesc(
                user,
                WellnessRecord.Category.mood
                )
        //.ifPresent(record ->
        //        dto.setMood(record.getNotes()));
        .ifPresent(record -> { dto.setMood(getMoodLevel(record.getValue())); });

        // Query latest recommendation
        recommendationRepository
        .findTopByUserOrderByGeneratedAtDesc(user)
        .ifPresent(rec ->
            dto.setLatestRecommendation(rec.getTitle()));
        
        
        // Calculating Average Value
        LocalDate startDate = LocalDate.now().minusDays(6);
        
        Double avgSleep = wellnessRepository.findAverageValue( user,
                        									   WellnessRecord.Category.sleep,
                        									   startDate);

        dto.setAvgSleepHours(avgSleep);
        

        Double avgWater = wellnessRepository.findAverageValue( user,
                        									   WellnessRecord.Category.water,
                        									   startDate);

        dto.setAvgWaterIntake(avgWater);
        
     
        Double avgSteps = wellnessRepository.findAverageValue( user,
                        									   WellnessRecord.Category.steps,
                        									   startDate);

        dto.setAvgSteps(avgSteps);
        
        
        Double avgExercise = wellnessRepository.findAverageDuration( user,
                        											 WellnessRecord.Category.exercise,
                        											 startDate);

        dto.setAvgExerciseMinutes(avgExercise == null ? null : avgExercise.intValue());
         
        
        return dto;
    }
    
    private String getMoodLevel(Double moodValue) {

        if (moodValue == null) { return "Unknown"; }
        if (moodValue <= 2) { return "Low"; }
        if (moodValue <= 5) { return "Content"; }
        if (moodValue <= 8) { return "Good"; }

        return "Excellent";
    }

}
