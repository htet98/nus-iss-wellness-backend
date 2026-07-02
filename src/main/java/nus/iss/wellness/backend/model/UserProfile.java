package nus.iss.wellness.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

//@Entity
@Table(name = "user_profile")
public class UserProfile {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    private String firstName;

    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate dateOfBirth;

    private String address;

    private Double heightCm;

    private Double weightKg;

    @Enumerated(EnumType.STRING)
    private FitnessGoal fitnessGoal;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void createDate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void updateDate() {
        updatedAt = LocalDateTime.now();
    }

    public UserProfile() {
        super();
    }

    public UserProfile(Long id, User user, String firstName, String lastName, Gender gender, LocalDate dateOfBirth,
                       String address, Double heightCm, Double weightKg, FitnessGoal fitnessGoal, LocalDateTime createdAt,
                       LocalDateTime updatedAt) {
        super();
        this.id = id;
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.fitnessGoal = fitnessGoal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserProfile(User user, String firstName, String lastName, Gender gender, LocalDate dateOfBirth,
                       String address, Double heightCm, Double weightKg, FitnessGoal fitnessGoal, LocalDateTime createdAt,
                       LocalDateTime updatedAt) {
        super();
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.fitnessGoal = fitnessGoal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public FitnessGoal getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(FitnessGoal fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "UserProfile [id=" + id + ", user=" + user + ", firstName=" + firstName + ", lastName=" + lastName
                + ", gender=" + gender + ", dateOfBirth=" + dateOfBirth + ", address=" + address + ", heightCm="
                + heightCm + ", weightKg=" + weightKg + ", fitnessGoal=" + fitnessGoal + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt + "]";
    }
}
