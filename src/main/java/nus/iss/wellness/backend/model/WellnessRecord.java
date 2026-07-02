package nus.iss.wellness.backend.model;

// Cecil - 2 Jul 2026
import nus.iss.wellness.backend.model.WellnessCategoryEnum;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Loh Si Hua - 27 Jun 2026

@Entity
@Table(name = "wellness_records")
public class WellnessRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Column(name = "value", nullable = false)
    private Double value;

    @Column(name = "calories_burned")
    private Double caloriesBurned;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    // ===========================
    // Constructors
    // ===========================

    public WellnessRecord() {
        this.createdAt = LocalDateTime.now();
    }

   

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

//    many to one to user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Category {
        sleep, exercise, mood, water, steps
    }

    // ===========================
    // Getters & Setters
    // ===========================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }


    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }


    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }


    public Double getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Double caloriesBurned) { this.caloriesBurned = caloriesBurned; }


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


// I added the full JPA entity with all fields mapped to the 
// wellness_records table in your database — id, 
// userId, category (enum: sleep/exercise/mood/water/steps), 
// value, caloriesBurned, unit, durationMinutes, 
// recordDate, notes, and createdAt. 
// Without this, Spring/Hibernate wouldn't know how to 
// read/write wellness records to the database.

