package nus.iss.wellness.backend.controller;


import nus.iss.wellness.backend.dto.response.DashboardResponse;
import nus.iss.wellness.backend.service.DashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Author: Cecil

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<DashboardResponse> getDashboard(
            @PathVariable Long userId) {

        DashboardResponse response =
                dashboardService.getDashboard(userId);

        return ResponseEntity.ok(response);
    }

}
