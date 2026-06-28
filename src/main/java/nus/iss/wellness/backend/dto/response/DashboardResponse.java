package nus.iss.wellness.backend.dto.response;


public class DashboardResponse {

    private String username;
    private Double sleepHours;
    private Integer exerciseMinutes;
    private Double waterIntake;
    private Double steps;
    private String mood;

    //private String latestRecommendation;

    public DashboardResponse() {
    }

    public DashboardResponse(
            String username,
            Double sleepHours,
            Integer exerciseMinutes,
            Double waterIntake,
            Double steps,
            String mood)
            //String latestRecommendation) 
    {
        this.username = username;
        this.sleepHours = sleepHours;
        this.exerciseMinutes = exerciseMinutes;
        this.waterIntake = waterIntake;
        this.steps = steps;
        this.mood = mood;
        //this.latestRecommendation = latestRecommendation;
    }

    // getters & setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Double getSleepHours() { return sleepHours; }
    public void setSleepHours(Double sleepHours) { this.sleepHours = sleepHours; }

    public Integer getExerciseMinutes() { return exerciseMinutes; }
    public void setExerciseMinutes(Integer exerciseMinutes) { this.exerciseMinutes = exerciseMinutes; }

    public Double getWaterIntake() { return waterIntake; }
    public void setWaterIntake(Double waterIntake) { this.waterIntake = waterIntake; }

    public Double getSteps() { return steps; }
    public void setSteps(Double steps) { this.steps = steps; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    // public String getLatestRecommendation() { return latestRecommendation; }
    public void setLatestRecommendation(String latestRecommendation) { /* this.latestRecommendation = latestRecommendation; */ }

    
}
