package nus.iss.wellness.backend.dto.response;


// Author: Cecil

public class DashboardResponse {

    private String username;
    private String fullName;
    private Double sleepHours;
    private Double AvgSleepHours;
    private Integer exerciseMinutes;
    private Integer AvgExerciseMinutes;
    private Double waterIntake;
    private Double AvgWaterIntake;
    private Double steps;
    private Double AvgSteps;
    private String mood;
    
    // AI Recommendation fields
    private String latestRecommendation;        // Title
    private String latestRecommendationText;    // Detailed TEXT content
    
    // Wellness Scores
    private Integer stepsScore;
    private Integer sleepScore;
    private Integer exerciseScore;
    private Integer waterScore;
    private Integer overallWellnessScore;
    
   // Record Dates for Latest Activities ---
    private String sleepRecordDate;
    private String exerciseRecordDate;
    private String waterRecordDate;
    private String stepsRecordDate;
    
    

    public DashboardResponse() {
    }

    public DashboardResponse(
            String username,
            String fullName,
            Double sleepHours,
            Double AvgSleepHours,
            Integer exerciseMinutes,
            Integer AvgExerciseMinutes,
            Double waterIntake,
            Double AvgWaterIntake,
            Double steps,
            Double AvgSteps,
            String mood,
            String latestRecommendation) 
    {
        this.username = username;
        this.fullName = fullName;
        this.sleepHours = sleepHours;
        this.AvgSleepHours = AvgSleepHours;
        this.exerciseMinutes = exerciseMinutes;
        this.AvgExerciseMinutes = AvgExerciseMinutes;
        this.waterIntake = waterIntake;
        this.AvgWaterIntake = AvgWaterIntake;
        this.steps = steps;
        this.AvgSteps = AvgSteps;
        this.mood = mood;
        this.latestRecommendation = latestRecommendation;
    }

    // getters & setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getfullName() { return fullName; }
    public void setfullName(String fullName) { this.fullName = fullName; }

    public Double getSleepHours() { return sleepHours; }
    public void setSleepHours(Double sleepHours) { this.sleepHours = sleepHours; }
    
    public Double getAvgSleepHours() { return AvgSleepHours; }
    public void setAvgSleepHours(Double AvgSleepHours) { this.AvgSleepHours = AvgSleepHours; }

    public Integer getExerciseMinutes() { return exerciseMinutes; }
    public void setExerciseMinutes(Integer exerciseMinutes) { this.exerciseMinutes = exerciseMinutes; }
    
    public Integer getAvgExerciseMinutes() { return AvgExerciseMinutes; }
    public void setAvgExerciseMinutes(Integer AvgExerciseMinutes) { this.AvgExerciseMinutes = AvgExerciseMinutes; }

    public Double getWaterIntake() { return waterIntake; }
    public void setWaterIntake(Double waterIntake) { this.waterIntake = waterIntake; }
    
    public Double getAvgWaterIntake() { return AvgWaterIntake; }
    public void setAvgWaterIntake(Double AvgWaterIntake) { this.AvgWaterIntake = AvgWaterIntake; }

    public Double getSteps() { return steps; }
    public void setSteps(Double steps) { this.steps = steps; }
    
    public Double getAvgSteps() { return AvgSteps; }
    public void setAvgSteps(Double AvgSteps) { this.AvgSteps = AvgSteps; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }


    
    // AI Recommendation
    public String getLatestRecommendationText() { return latestRecommendationText;  }
    
    public void setLatestRecommendationText(String latestRecommendationText) { 
        this.latestRecommendationText = latestRecommendationText; 
    }

    public String getLatestRecommendation() { return latestRecommendation; }
    public void setLatestRecommendation(String latestRecommendation) { this.latestRecommendation = latestRecommendation;  }

    
    
    public Integer getStepsScore() { return stepsScore; }
    public void setStepsScore(Integer stepsScore) { this.stepsScore = stepsScore; }

    public Integer getSleepScore() { return sleepScore; }
    public void setSleepScore(Integer sleepScore) { this.sleepScore = sleepScore; }

    public Integer getExerciseScore() { return exerciseScore; }
    public void setExerciseScore(Integer exerciseScore) { this.exerciseScore = exerciseScore; }

    public Integer getWaterScore() { return waterScore; }
    public void setWaterScore(Integer waterScore) { this.waterScore = waterScore; }

    public Integer getOverallWellnessScore() { return overallWellnessScore; }
    public void setOverallWellnessScore(Integer overallWellnessScore) { this.overallWellnessScore = overallWellnessScore; }
    
    
    
    public String getSleepRecordDate() { return sleepRecordDate; }
    public void setSleepRecordDate(String sleepRecordDate) { this.sleepRecordDate = sleepRecordDate; }

    public String getExerciseRecordDate() { return exerciseRecordDate; }
    public void setExerciseRecordDate(String exerciseRecordDate) { this.exerciseRecordDate = exerciseRecordDate; }

    public String getWaterRecordDate() { return waterRecordDate; }
    public void setWaterRecordDate(String waterRecordDate) { this.waterRecordDate = waterRecordDate; }

    public String getStepsRecordDate() { return stepsRecordDate; }
    public void setStepsRecordDate(String stepsRecordDate) { this.stepsRecordDate = stepsRecordDate; }
    
}
