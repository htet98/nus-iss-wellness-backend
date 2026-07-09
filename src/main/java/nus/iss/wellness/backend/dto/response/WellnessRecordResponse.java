package nus.iss.wellness.backend.dto.response;

//import nus.iss.wellness.backend.model.User;
import nus.iss.wellness.backend.model.WellnessRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;

	// Author: Si Hua
public class WellnessRecordResponse {

    private Long id;
    private Long userId;
    private WellnessRecord.Category category;
    private Double value;
    private Double caloriesBurned;
    private String unit;
    private Integer durationMinutes;
    private LocalDate recordDate;
    private String notes;
    private LocalDateTime createdAt;

    public static WellnessRecordResponse from(WellnessRecord record) {
        WellnessRecordResponse response = new WellnessRecordResponse();
        response.id = record.getId();   
        response.userId = record.getUserId();
        response.category = record.getCategory();
        response.value = record.getValue();
        response.caloriesBurned = record.getCaloriesBurned();
        response.unit = record.getUnit();
        response.durationMinutes = record.getDurationMinutes();
        response.recordDate = record.getRecordDate();
        response.notes = record.getNotes();
        response.createdAt = record.getCreatedAt();
        return response;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public WellnessRecord.Category getCategory() { return category; }
    public Double getValue() { return value; }
    public Double getCaloriesBurned() { return caloriesBurned; }
    public String getUnit() { return unit; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public LocalDate getRecordDate() { return recordDate; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

// Request DTO defines what JSON the client sends in (userId, category, value, etc.). 
// Response DTO defines what JSON gets returned. 
// This keeps the API clean and separate from the internal database model.