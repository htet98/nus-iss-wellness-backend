package nus.iss.wellness.backend.service;

import java.time.LocalDate;

import nus.iss.wellness.backend.dto.response.WellnessHistoryResponse;

public interface WellnessHistoryService {
	
    WellnessHistoryResponse getHistory(Long userId, LocalDate startDate, LocalDate endDate);
    
}