package nus.iss.wellness.backend.repository;

import nus.iss.wellness.backend.model.WellnessRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WellnessRecordRepository extends JpaRepository<WellnessRecord, Long> {
}