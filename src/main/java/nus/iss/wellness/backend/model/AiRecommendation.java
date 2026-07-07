package nus.iss.wellness.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_recommendations")
public class AiRecommendation {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    private String status;

    private LocalDateTime generatedAt;

    public AiRecommendation() {
    }

    @PrePersist
    public void prePersist() {
        generatedAt = LocalDateTime.now();
    }

    public Long getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(Long recommendationId) {
        this.recommendationId = recommendationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public AiRecommendation(Long recommendationId, User user, String title, String recommendation,
                            LocalDateTime generatedAt) {
        super();
        this.recommendationId = recommendationId;
        this.user = user;
        this.title = title;
        this.recommendation = recommendation;
        this.generatedAt = generatedAt;
    }

    public AiRecommendation(User user, String title, String recommendation, LocalDateTime generatedAt) {
        super();
        this.user = user;
        this.title = title;
        this.recommendation = recommendation;
        this.generatedAt = generatedAt;
    }
}
