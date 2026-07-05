package nus.iss.wellness.backend.service.impl;

import nus.iss.wellness.backend.dto.response.HistoryRecordResponse;
import nus.iss.wellness.backend.dto.response.WellnessHistoryResponse;
import nus.iss.wellness.backend.model.WellnessRecord;
import nus.iss.wellness.backend.repository.WellnessRecordRepository;
import nus.iss.wellness.backend.service.WellnessHistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

//Author: Cecil

@Service
public class WellnessHistoryServiceImpl implements WellnessHistoryService {

    private final WellnessRecordRepository recordRepository;

    public WellnessHistoryServiceImpl(WellnessRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    public WellnessHistoryResponse getHistory(Long userId, String timeframe) {
        LocalDate startDate = calculateStartDate(timeframe);
        
        List<WellnessRecord> records = recordRepository.findRecordsByTimeframe(userId, startDate);

        // Internal Maps to aggregate/sum data points by unique date
        Map<LocalDate, Double> stepsMap = new TreeMap<>();
        Map<LocalDate, Double> exerciseMap = new TreeMap<>();
        Map<LocalDate, Double> sleepMap = new TreeMap<>();
        Map<LocalDate, Double> waterMap = new TreeMap<>();
        Map<LocalDate, Double> distanceMap = new TreeMap<>();

        for (WellnessRecord record : records) {
            LocalDate date = record.getRecordDate();
            String category = record.getCategory().name().toLowerCase();
            
            switch (category) {
                case "steps":
                    stepsMap.put(date, stepsMap.getOrDefault(date, 0.0) + record.getValue());
                    
                    // Add steps-to-distance conversion to the total distance for this day
                    double calculatedKm = record.getValue() * 0.000762;
                    distanceMap.put(date, distanceMap.getOrDefault(date, 0.0) + calculatedKm);
                    break;
                    
                case "exercise":
                    // If exercise was recorded natively in km, add it directly to distance map
                    if ("km".equalsIgnoreCase(record.getUnit())) {
                        distanceMap.put(date, distanceMap.getOrDefault(date, 0.0) + record.getValue());
                    }
                    if (record.getDurationMinutes() != null) {
                        exerciseMap.put(date, exerciseMap.getOrDefault(date, 0.0) + record.getDurationMinutes());
                    }
                    break;
                    
                case "sleep":
                    sleepMap.put(date, sleepMap.getOrDefault(date, 0.0) + record.getValue());
                    break;
                    
                case "water":
                    waterMap.put(date, waterMap.getOrDefault(date, 0.0) + record.getValue());
                    break;
            }
        }

        // Map the accumulated totals back into lists of HistoryRecordResponses
        Map<String, List<HistoryRecordResponse>> structuredData = new HashMap<>();
        structuredData.put("steps", convertMapToList(stepsMap));
        structuredData.put("exercise", convertMapToList(exerciseMap));
        structuredData.put("sleep", convertMapToList(sleepMap));
        structuredData.put("water", convertMapToList(waterMap));
        structuredData.put("distance", convertMapToList(distanceMap));

        return new WellnessHistoryResponse(structuredData);
    }

    // Helper method to transform the maps into your requested DTO format
    private List<HistoryRecordResponse> convertMapToList(Map<LocalDate, Double> map) {
        List<HistoryRecordResponse> list = new ArrayList<>();
        for (Map.Entry<LocalDate, Double> entry : map.entrySet()) {
            // Optional: round decimal values (like distance) to 2 decimal places for cleaner JSON
            double roundedValue = Math.round(entry.getValue() * 100.0) / 100.0;
            list.add(new HistoryRecordResponse(entry.getKey(), roundedValue));
        }
        return list;
    }

    private LocalDate calculateStartDate(String timeframe) {
        LocalDate today = LocalDate.now();
        switch (timeframe.toLowerCase()) {
        	case "1d": return today.minusDays(1);
            case "1w": return today.minusWeeks(1);
            case "1m": return today.minusMonths(1);
            case "3m": return today.minusMonths(3);
            case "12m": return today.minusMonths(12);
            default: 	
            			return today.minusMonths(13); 
        }
    }
}
