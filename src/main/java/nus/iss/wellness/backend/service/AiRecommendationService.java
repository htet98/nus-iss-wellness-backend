package nus.iss.wellness.backend.service;

import nus.iss.wellness.backend.dto.response.AiRecommendationResponse;
import java.util.Optional;

// Author: Htet Nandar
public interface AiRecommendationService {

    /** Returns the most recent recommendation for the user, or empty if none exists. */
    Optional<AiRecommendationResponse> getLatest(Long userId);

    /** Calls the Python AI service to generate a new recommendation and saves it to the DB. */
    AiRecommendationResponse generate(Long userId);
}
