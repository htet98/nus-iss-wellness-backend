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
    public WellnessHistoryResponse getHistory(Long userId, LocalDate startDate, LocalDate endDate) {
    	
        // Query database directly with the given date boundaries
        List<WellnessRecord> records = recordRepository.findRecordsByDateRange(userId, startDate, endDate);
        
        Map<LocalDate, Double> stepsMap = new TreeMap<>();
        Map<LocalDate, Double> exerciseMap = new TreeMap<>();
        Map<LocalDate, Double> sleepMap = new TreeMap<>();
        Map<LocalDate, Double> waterMap = new TreeMap<>();
        Map<LocalDate, Double> distanceMap = new TreeMap<>();
        

        for (WellnessRecord record : records) {
            LocalDate date = record.getRecordDate(); //.minusDays(3);
            String category = record.getCategory().name().toLowerCase();
            
            switch (category) {
                case "steps":
                    stepsMap.put(date, stepsMap.getOrDefault(date, 0.0) + record.getValue());
                    //System.out.println("steps: " + record.getValue() + ",  Time Stamp: " + date); // For Test
                    
                    double calculatedKm = record.getValue() * 0.000762;
                    distanceMap.put(date, distanceMap.getOrDefault(date, 0.0) + calculatedKm);
                    break;
                    
                case "exercise":
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

        Map<String, List<HistoryRecordResponse>> structuredData = new HashMap<>();
        structuredData.put("steps", convertMapToList(stepsMap));
        structuredData.put("exercise", convertMapToList(exerciseMap));
        structuredData.put("sleep", convertMapToList(sleepMap));
        structuredData.put("water", convertMapToList(waterMap));
        structuredData.put("distance", convertMapToList(distanceMap));

        return new WellnessHistoryResponse(structuredData);
    }

    private List<HistoryRecordResponse> convertMapToList(Map<LocalDate, Double> map) {
        List<HistoryRecordResponse> list = new ArrayList<>();
        for (Map.Entry<LocalDate, Double> entry : map.entrySet()) {
            double roundedValue = Math.round(entry.getValue() * 100.0) / 100.0;
            list.add(new HistoryRecordResponse(entry.getKey(), roundedValue));
        }
        return list;
    }
}

