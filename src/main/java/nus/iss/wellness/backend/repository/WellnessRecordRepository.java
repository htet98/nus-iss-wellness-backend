package nus.iss.wellness.backend.repository;

import nus.iss.wellness.backend.model.WellnessRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

//Loh Si Hua - 27 Jun 2026
@Repository
public interface WellnessRecordRepository extends JpaRepository<WellnessRecord, Long> {

    List<WellnessRecord> findByUserIdOrderByRecordDateDesc(Long userId);

    List<WellnessRecord> findByUserIdAndCategoryOrderByRecordDateDesc(Long userId, WellnessRecord.Category category);
}

// I made it extend JpaRepository so Spring Data JPA 
// automatically provides all standard database operations 
// (save, find, delete, etc.) without writing SQL manually. 
// I also added two query methods — find records by user, and find records by user + category.