package nus.iss.wellness.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import nus.iss.wellness.backend.model.WellnessRecord;

import java.time.LocalDate;

	//Author: Si Hua
public class WellnessRecordRequest {

    @NotNull
    private Long userId;

    @NotNull
    private WellnessRecord.Category category;

    @NotNull
    private Double value;

    private Double caloriesBurned;
    private String unit;
    private Integer durationMinutes;

    @NotNull
    private LocalDate recordDate;

    private String notes;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public WellnessRecord.Category getCategory() { return category; }
    public void setCategory(WellnessRecord.Category category) { this.category = category; }

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
}

// Request DTO defines what JSON the client sends in (userId, category, value, etc.). 
// Response DTO defines what JSON gets returned. 
// This keeps the API clean and separate from the internal database model.