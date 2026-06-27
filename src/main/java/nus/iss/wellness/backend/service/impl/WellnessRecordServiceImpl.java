package nus.iss.wellness.backend.service.impl;

import nus.iss.wellness.backend.dto.request.AddWellnessRecordRequest;
import nus.iss.wellness.backend.model.WellnessRecord;
import nus.iss.wellness.backend.repository.WellnessRecordRepository;
import nus.iss.wellness.backend.service.WellnessRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WellnessRecordServiceImpl implements WellnessRecordService {

    @Autowired
    private WellnessRecordRepository wellnessRecordRepository;

    @Override
    public WellnessRecord addWellnessRecord(AddWellnessRecordRequest request) {
        WellnessRecord record = new WellnessRecord();
        record.setUserId(request.getUserId());
        record.setActivityType(request.getActivityType());
        record.setRecordDate(request.getRecordDate());
        record.setDurationMinutes(request.getDurationMinutes());
        record.setNotes(request.getNotes());
        return wellnessRecordRepository.save(record);
    }
}