package nus.iss.wellness.backend.controller;

import nus.iss.wellness.backend.dto.response.WellnessHistoryResponse;
import nus.iss.wellness.backend.service.WellnessHistoryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        WellnessHistoryResponse response = historyService.getHistory(userId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}