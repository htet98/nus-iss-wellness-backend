package nus.iss.wellness.backend.controller;

import nus.iss.wellness.backend.dto.response.AiRecommendationResponse;
import nus.iss.wellness.backend.service.AiRecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Author: Htet Nandar
 */
@RestController
@RequestMapping("/api/recommendations")
public class AiRecommendationController {

    private final AiRecommendationService recommendationService;

    public AiRecommendationController(AiRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/latest")
    public ResponseEntity<AiRecommendationResponse> getLatest(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return recommendationService.getLatest(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Asks the Python AI service to generate a new recommendation and saves it.
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generate(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(recommendationService.generate(userId));
        } catch (RuntimeException e) {
            // Surface the real error so the Android client can show it
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(java.util.Map.of("error", e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
        }
    }
}
