package nus.iss.wellness.backend.model;


import java.time.LocalDate;
import java.time.LocalDateTime;

import nus.iss.wellness.backend.model.WellnessCategoryEnum;

import jakarta.persistence.*;


@Entity
@Table(name = "wellness_records")
public class WellnessRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many wellness records belong to one user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WellnessCategoryEnum category;

    /**
     * Generic value.
     *
     * Examples:
     * Sleep      = 8
     * Exercise   = 30
     * Water      = 2.5
     * Steps      = 8000
     * Mood       = 4
     */
    @Column(nullable = false)
    private Double value;

    @Column(name = "calories_burned")
    private Integer caloriesBurned;

    @Column(length = 20)
    private String unit;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ===========================
    // Constructors
    // ===========================

    public WellnessRecord() {
        this.createdAt = LocalDateTime.now();
    }

    // ===========================
    // Getters & Setters
    // ===========================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public WellnessCategoryEnum getCategory() { return category; }
    public void setCategory(WellnessCategoryEnum category) { this.category = category; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public Integer getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Integer caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}