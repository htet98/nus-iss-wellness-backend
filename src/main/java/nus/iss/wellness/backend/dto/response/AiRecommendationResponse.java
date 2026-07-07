package nus.iss.wellness.backend.dto.response;

import nus.iss.wellness.backend.model.AiRecommendation;

// Author: Htet Nandar
public class AiRecommendationResponse {

    private Long id;
    private String title;
    private String recommendation;
    private String generatedAt;   // ISO string e.g. "2026-07-07T15:18:56"

    public static AiRecommendationResponse from(AiRecommendation rec) {
        AiRecommendationResponse dto = new AiRecommendationResponse();
        dto.id             = rec.getRecommendationId();
        dto.title          = rec.getTitle();
        dto.recommendation = rec.getRecommendation();
        dto.generatedAt    = rec.getGeneratedAt() != null
                             ? rec.getGeneratedAt().toString()
                             : null;
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
}
