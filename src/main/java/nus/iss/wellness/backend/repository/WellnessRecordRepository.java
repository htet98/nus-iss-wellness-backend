package nus.iss.wellness.backend.repository;

import nus.iss.wellness.backend.model.WellnessRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


//Cecil - 2 Jul 2026
import java.time.LocalDate;
import java.util.Optional;
import nus.iss.wellness.backend.model.User;
import nus.iss.wellness.backend.model.WellnessCategoryEnum;

//Cecil - 2 Jul 2026
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
            User user, WellnessRecord.Category category, LocalDate recordDate);
    
    /*SELECT * FROM wellness_record 
    WHERE user_id = :userId AND category = :category 
    ORDER BY record_date DESC 
    LIMIT 1; */

    Optional<WellnessRecord> findTopByUserAndCategoryOrderByRecordDateDesc(
            User user,
            WellnessRecord.Category category);

  //Loh Si Hua - 27 Jun 2026
    List<WellnessRecord> findByUserIdOrderByRecordDateDesc(Long userId);

    List<WellnessRecord> findByUserIdAndCategoryOrderByRecordDateDesc(Long userId, WellnessRecord.Category category);
}

// I made it extend JpaRepository so Spring Data JPA 
// automatically provides all standard database operations 
// (save, find, delete, etc.) without writing SQL manually. 
// I also added two query methods — find records by user, and find records by user + category.