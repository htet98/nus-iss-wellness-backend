package nus.iss.wellness.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

//Author: Junior

@Entity
@Table(name = "users")
public class User {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDateTime createdAt;

//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
//    private UserProfile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WellnessRecord> wellnessRecords;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ChatSession> chatSessions;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<AiRecommendation> recommendations;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public User(Long userId, String username, String passwordHash, String email, LocalDateTime createdAt,
                UserProfile profile, List<WellnessRecord> wellnessRecords, List<ChatSession> chatSessions,
                List<AiRecommendation> recommendations) {
        super();
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.createdAt = createdAt;
//        this.profile = profile;
        this.wellnessRecords = wellnessRecords;
        this.chatSessions = chatSessions;
//        this.recommendations = recommendations;
    }

    public User() {
        super();
    }

    public User(String username, String passwordHash, String email, LocalDateTime createdAt, UserProfile profile,
                List<WellnessRecord> wellnessRecords, List<ChatSession> chatSessions,
                List<AiRecommendation> recommendations) {
        super();
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.createdAt = createdAt;
//        this.profile = profile;
        this.wellnessRecords = wellnessRecords;
        this.chatSessions = chatSessions;
//        this.recommendations = recommendations;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

//    public UserProfile getProfile() {
//        return profile;
//    }

//    public void setProfile(UserProfile profile) {
//        this.profile = profile;
//    }

    public List<WellnessRecord> getWellnessRecords() {
        return wellnessRecords;
    }

    public void setWellnessRecords(List<WellnessRecord> wellnessRecords) {
        this.wellnessRecords = wellnessRecords;
    }

    public List<ChatSession> getChatSessions() {
        return chatSessions;
    }

    public void setChatSessions(List<ChatSession> chatSessions) {
        this.chatSessions = chatSessions;
    }

//    public List<AiRecommendation> getRecommendations() {
//        return recommendations;
//    }

//    public void setRecommendations(List<AiRecommendation> recommendations) {
//        this.recommendations = recommendations;
//    }

    @Override
    public String toString() {
//        return "User [userId=" + userId + ", username=" + username + ", passwordHash=" + passwordHash + ", email="
//                + email + ", createdAt=" + createdAt + ", profile=" + profile + ", wellnessRecords=" + wellnessRecords
//                + ", chatSessions=" + chatSessions + ", recommendations=" + recommendations + "]";
        return "User [userId=" + userId + ", username=" + username + ", passwordHash=" + passwordHash + ", email="
                + email + ", createdAt=" + createdAt + ", wellnessRecords=" + wellnessRecords
                + ", chatSessions=" + chatSessions + "]";
    }
}
