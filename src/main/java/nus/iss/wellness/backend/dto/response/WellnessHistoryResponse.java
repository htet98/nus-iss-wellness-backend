package nus.iss.wellness.backend.dto.response;

import java.util.List;
import java.util.Map;

//Author: Cecil

public class WellnessHistoryResponse {
    
    private Map<String, List<HistoryRecordResponse>> chartData;

    public WellnessHistoryResponse() {}

    public WellnessHistoryResponse(Map<String, List<HistoryRecordResponse>> chartData) {
        this.chartData = chartData;
    }

    public Map<String, List<HistoryRecordResponse>> getChartData() { 
        return chartData; 
    }
    
    public void setChartData(Map<String, List<HistoryRecordResponse>> chartData) { 
        this.chartData = chartData; 
    }
}