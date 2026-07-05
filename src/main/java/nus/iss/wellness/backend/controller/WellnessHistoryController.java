package nus.iss.wellness.backend.controller;


import nus.iss.wellness.backend.dto.response.WellnessHistoryResponse;
import nus.iss.wellness.backend.service.WellnessHistoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Author: Cecil

@RestController
@RequestMapping("/api/wellness")
public class WellnessHistoryController {

    private final WellnessHistoryService historyService;

    public WellnessHistoryController(WellnessHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<WellnessHistoryResponse> getWellnessHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1m") String timeframe) {
        
    	//System.out.println("Show Time Frame = " + timeframe); // For Testing Purpose
    	
        WellnessHistoryResponse response = historyService.getHistory(userId, timeframe);
        return ResponseEntity.ok(response);
    }
}