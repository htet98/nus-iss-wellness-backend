package nus.iss.wellness.backend.repository;

import nus.iss.wellness.backend.model.WellnessRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WellnessRecordRepository extends JpaRepository<WellnessRecord, Long> {

    List<WellnessRecord> findByUserIdOrderByRecordDateDesc(Long userId);

    List<WellnessRecord> findByUserIdAndCategoryOrderByRecordDateDesc(Long userId, WellnessRecord.Category category);
}
