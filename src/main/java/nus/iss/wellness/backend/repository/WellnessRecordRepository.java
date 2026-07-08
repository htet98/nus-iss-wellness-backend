package nus.iss.wellness.backend.repository;

import nus.iss.wellness.backend.model.WellnessRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


//Cecil - 2 Jul 2026
import java.time.LocalDate;
import java.util.Optional;
import nus.iss.wellness.backend.model.User;
import nus.iss.wellness.backend.model.WellnessCategoryEnum;

// Author: Cecil

@Repository
public interface WellnessRecordRepository extends JpaRepository<WellnessRecord, Long> {

    /**
     * Get all wellness records of a user.
     */
    List<WellnessRecord> findByUser(User user);

    /**
     * Get all wellness records ordered by newest first.
     */
    List<WellnessRecord> findByUserOrderByRecordDateDesc(User user);

    /**
     * Get all records of a specific category.
     */
    List<WellnessRecord> findByUserAndCategory(User user, WellnessRecord.Category category);

    /**
     * Get records within a date range.
     */
    List<WellnessRecord> findByUserAndRecordDateBetween( User user, LocalDate startDate, LocalDate endDate);

    /**
     * Get records for one day.
     */
    List<WellnessRecord> findByUserAndRecordDate( User user, LocalDate recordDate);
    
    
    Optional<WellnessRecord> findFirstByUserAndCategoryAndRecordDate(
            User user, WellnessRecord.Category category, LocalDate recordDate);
    
    
    Optional<WellnessRecord> findTopByUserAndCategoryOrderByRecordDateDescCreatedAtDesc(
            User user,
            WellnessRecord.Category category);
    
    
    @Query("""
    		SELECT AVG(w.value)
    		FROM WellnessRecord w
    		WHERE w.user = :user
    		AND w.category = :category
    		AND w.recordDate >= :startDate
    		""")
    		Double findAverageValue(
    		        @Param("user") User user,
    		        @Param("category") WellnessRecord.Category category,
    		        @Param("startDate") LocalDate startDate);
    
    
    @Query("""
    		SELECT AVG(w.durationMinutes)
    		FROM WellnessRecord w
    		WHERE w.user = :user
    		AND w.category = :category
    		AND w.recordDate >= :startDate
    		""")
    		Double findAverageDuration(
    		        @Param("user") User user,
    		        @Param("category") WellnessRecord.Category category,
    		        @Param("startDate") LocalDate startDate);
    
    
    @Query("SELECT w FROM WellnessRecord w WHERE w.user.id = :userId AND w.recordDate >= :startDate")
    List<WellnessRecord> findRecordsByTimeframe(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);

    
    @Query("SELECT w FROM WellnessRecord w WHERE w.user.id = :userId AND w.recordDate BETWEEN :startDate AND :endDate")
    List<WellnessRecord> findRecordsByDateRange(
            @Param("userId") Long userId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    

  // Author : Si Hua
    List<WellnessRecord> findByUserIdOrderByRecordDateDesc(Long userId);

    List<WellnessRecord> findByUserIdAndCategoryOrderByRecordDateDesc(Long userId, WellnessRecord.Category category);

    Optional<WellnessRecord> findFirstByUserIdAndCategoryAndRecordDateOrderByCreatedAtDesc(
            Long userId, WellnessRecord.Category category, LocalDate recordDate);
}

// I made it extend JpaRepository so Spring Data JPA 
// automatically provides all standard database operations 
// (save, find, delete, etc.) without writing SQL manually. 
// I also added two query methods — find records by user, and find records by user + category.