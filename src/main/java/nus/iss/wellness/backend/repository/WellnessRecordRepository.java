package nus.iss.wellness.backend.repository;

import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nus.iss.wellness.backend.model.User;
import nus.iss.wellness.backend.model.WellnessRecord;
import nus.iss.wellness.backend.model.WellnessCategoryEnum;


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
    List<WellnessRecord> findByUserAndCategory(User user, WellnessCategoryEnum category);

    /**
     * Get records within a date range.
     */
    List<WellnessRecord> findByUserAndRecordDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate);

    /**
     * Get records for one day.
     */
    List<WellnessRecord> findByUserAndRecordDate(
            User user,
            LocalDate recordDate);
    
    /*SELECT * FROM wellness_record 
    WHERE user_id = :userId 
      AND category = :category 
      AND record_date = :recordDate 
    LIMIT 1; */
    
    Optional<WellnessRecord> findFirstByUserAndCategoryAndRecordDate(
            User user, WellnessCategoryEnum category, LocalDate recordDate);
    
    /*SELECT * FROM wellness_record 
    WHERE user_id = :userId AND category = :category 
    ORDER BY record_date DESC 
    LIMIT 1; */

    Optional<WellnessRecord> findTopByUserAndCategoryOrderByRecordDateDesc(
            User user,
            WellnessCategoryEnum category);

}
