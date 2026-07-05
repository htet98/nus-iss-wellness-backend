package nus.iss.wellness.backend.service;

import nus.iss.wellness.backend.dto.response.WellnessHistoryResponse;

public interface WellnessHistoryService {
	
    WellnessHistoryResponse getHistory(Long userId, String timeframe);
    
}