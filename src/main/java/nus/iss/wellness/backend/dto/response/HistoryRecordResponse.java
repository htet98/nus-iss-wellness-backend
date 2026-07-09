package nus.iss.wellness.backend.dto.response;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

//Author: Cecil

public class HistoryRecordResponse {
    
    // This annotation fixes the [2026, 6, 29] array issue
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    private double value;

    public HistoryRecordResponse(LocalDate date, double value) {
        this.date = date;
        this.value = value;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}