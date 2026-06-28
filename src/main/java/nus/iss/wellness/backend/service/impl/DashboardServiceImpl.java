package nus.iss.wellness.backend.service.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import nus.iss.wellness.backend.dto.response.DashboardResponse;
import nus.iss.wellness.backend.model.User;
import nus.iss.wellness.backend.model.WellnessCategoryEnum;
import nus.iss.wellness.backend.repository.UserRepository;
import nus.iss.wellness.backend.repository.WellnessRecordRepository;
import nus.iss.wellness.backend.service.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final WellnessRecordRepository wellnessRepository;
    //private final RecommendationRepository recommendationRepository;

    public DashboardServiceImpl(
            UserRepository userRepository,
            WellnessRecordRepository wellnessRepository
            //RecommendationRepository recommendationRepository
            ) 
    {

        this.userRepository = userRepository;
        this.wellnessRepository = wellnessRepository;
        //this.recommendationRepository = recommendationRepository;
    }

    @Override
    public DashboardResponse getDashboard(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        DashboardResponse dto = new DashboardResponse();

        dto.setUsername(user.getUsername());
        
        LocalDate searchDate = LocalDate.of(2026, 6, 22); // LocalDate.now()

        // Query today's sleep
        wellnessRepository
        .findFirstByUserAndCategoryAndRecordDate(
                user,
                WellnessCategoryEnum.sleep,
                searchDate)
        .ifPresent(record ->
                dto.setSleepHours(record.getValue()));

        // Query today's exercise
        wellnessRepository
        .findFirstByUserAndCategoryAndRecordDate(
                user,
                WellnessCategoryEnum.exercise,
                searchDate)
        .ifPresent(record ->
                dto.setExerciseMinutes(
                        record.getDurationMinutes()));

        // Query today's water
        wellnessRepository
        .findFirstByUserAndCategoryAndRecordDate(
                user,
                WellnessCategoryEnum.water,
                searchDate)
        .ifPresent(record ->
                dto.setWaterIntake(record.getValue()));
        
     // Query today's Steps
        wellnessRepository
        .findFirstByUserAndCategoryAndRecordDate(
                user,
                WellnessCategoryEnum.steps,
                searchDate)
        .ifPresent(record ->
                dto.setSteps(record.getValue()));

        // TODO:
        wellnessRepository
        .findFirstByUserAndCategoryAndRecordDate(
                user,
                WellnessCategoryEnum.mood,
                searchDate)
        .ifPresent(record ->
                dto.setMood(record.getNotes()));

        // TODO:
        // Query latest recommendation

        return dto;
    }

}
