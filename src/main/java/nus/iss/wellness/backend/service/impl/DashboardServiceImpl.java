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
        
  
        

     // Query today's / latest sleep  OrderByRecordDateDesc
        wellnessRepository
        .findTopByUserAndCategoryOrderByRecordDateDescCreatedAtDesc(
                user,
                WellnessRecord.Category.sleep
                )
        .ifPresent(record -> {
            dto.setSleepHours(record.getValue());
            if (record.getRecordDate() != null) {
                dto.setSleepRecordDate(record.getRecordDate().toString());
            }
        });

     // Query today's / latest exercise
        wellnessRepository
        .findTopByUserAndCategoryOrderByRecordDateDescCreatedAtDesc(
                user,
                WellnessRecord.Category.exercise
                )
        .ifPresent(record -> {
            dto.setExerciseMinutes(record.getDurationMinutes());
            if (record.getRecordDate() != null) {
                dto.setExerciseRecordDate(record.getRecordDate().toString());
            }
        });
        
     // Query today's / latest water
        wellnessRepository
        .findTopByUserAndCategoryOrderByRecordDateDescCreatedAtDesc(
                user,
                WellnessRecord.Category.water
                )
        .ifPresent(record -> {
            dto.setWaterIntake(record.getValue());
            if (record.getRecordDate() != null) {
                dto.setWaterRecordDate(record.getRecordDate().toString());
            }
        });
        
     // Query today's / latest steps
        wellnessRepository
        .findTopByUserAndCategoryOrderByRecordDateDescCreatedAtDesc(
                user,
                WellnessRecord.Category.steps
                )
        .ifPresent(record -> {
            dto.setSteps(record.getValue());
            if (record.getRecordDate() != null) {
                dto.setStepsRecordDate(record.getRecordDate().toString());
            }
        });

        // Query today's Mood
        wellnessRepository
        .findTopByUserAndCategoryOrderByRecordDateDescCreatedAtDesc(
                user,
                WellnessRecord.Category.mood
                )
        //.ifPresent(record ->
        //        dto.setMood(record.getNotes()));
        .ifPresent(record -> { dto.setMood(getMoodLevel(record.getValue())); });

        
     // Query latest recommendation title and text content
        recommendationRepository
            .findTopByUserOrderByGeneratedAtDesc(user)
            .ifPresent(rec -> {
                dto.setLatestRecommendation(rec.getTitle());
                dto.setLatestRecommendationText(rec.getRecommendation());
            });
        
        
        
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
         
        
        // --- Calculate Wellness Scores ---
        int stepsScore = calculateStepsScore(avgSteps);
        int sleepScore = calculateSleepScore(avgSleep);
        int exerciseScore = calculateExerciseScore(avgExercise);
        int waterScore = calculateWaterScore(avgWater);

        dto.setStepsScore(stepsScore);
        dto.setSleepScore(sleepScore);
        dto.setExerciseScore(exerciseScore);
        dto.setWaterScore(waterScore);

        // Overall Weighted Average Score (100 Max)
        int overallScore = (int) Math.round(
                (stepsScore * 0.25) +
                (sleepScore * 0.30) +
                (exerciseScore * 0.25) +
                (waterScore * 0.20)
        );
        
        dto.setOverallWellnessScore(overallScore);
        
        
        return dto;
    }
    
    
 // --- Score Calculation Logic ---
    private int calculateStepsScore(Double avgSteps) {
        if (avgSteps == null || avgSteps <= 0) return 0;
        return (int) Math.min(100, Math.round((avgSteps / 10000.0) * 100));
    }

    private int calculateSleepScore(Double avgSleep) {
        if (avgSleep == null || avgSleep <= 0) return 0;
        if (avgSleep >= 7.0 && avgSleep <= 9.0) {
            return 100;
        } else if (avgSleep >= 5.0 && avgSleep < 7.0) {
            return (int) Math.round(100 - ((7.0 - avgSleep) * 25));
        } else if (avgSleep < 5.0) {
            return (int) Math.max(0, Math.round(avgSleep * 10));
        } else { // avgSleep > 9.0
            return (int) Math.max(50, Math.round(100 - ((avgSleep - 9.0) * 15)));
        }
    }

    private int calculateExerciseScore(Double avgExerciseMins) {
        if (avgExerciseMins == null || avgExerciseMins <= 0) return 0;
        return (int) Math.min(100, Math.round((avgExerciseMins / 30.0) * 100));
    }

    private int calculateWaterScore(Double avgWaterLiters) {
        if (avgWaterLiters == null || avgWaterLiters <= 0) return 0;
        return (int) Math.min(100, Math.round((avgWaterLiters / 2.5) * 100));
    }

    private String getMoodLevel(Double moodValue) {
        if (moodValue == null) { return "Unknown"; }
        if (moodValue <= 2) { return "Low"; }
        if (moodValue <= 5) { return "Content"; }
        if (moodValue <= 8) { return "Good"; }
        return "Excellent";
    }

}
