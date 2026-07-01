package nus.iss.wellness.backend.service.impl;

import nus.iss.wellness.backend.dto.request.WellnessRecordRequest;
import nus.iss.wellness.backend.dto.response.WellnessRecordResponse;
import nus.iss.wellness.backend.model.WellnessRecord;
import nus.iss.wellness.backend.repository.WellnessRecordRepository;
import nus.iss.wellness.backend.service.WellnessRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WellnessRecordServiceImpl implements WellnessRecordService {

    @Autowired
    private WellnessRecordRepository wellnessRecordRepository;

    @Override
    public WellnessRecordResponse createRecord(WellnessRecordRequest request) {
        WellnessRecord record = new WellnessRecord();
        record.setUserId(request.getUserId());
        record.setCategory(request.getCategory());
        record.setValue(request.getValue());
        record.setCaloriesBurned(request.getCaloriesBurned());
        record.setUnit(request.getUnit());
        record.setDurationMinutes(request.getDurationMinutes());
        record.setRecordDate(request.getRecordDate());
        record.setNotes(request.getNotes());
        return WellnessRecordResponse.from(wellnessRecordRepository.save(record));
    }

    @Override
    public WellnessRecordResponse updateRecord(Long recordId, WellnessRecordRequest request) {

        WellnessRecord record = wellnessRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Record not found"));

        // record.setUserId(record.getUserId());
        record.setCategory(request.getCategory());
        record.setValue(request.getValue());
        record.setCaloriesBurned(request.getCaloriesBurned());
        record.setUnit(request.getUnit());
        record.setDurationMinutes(request.getDurationMinutes());
        record.setRecordDate(request.getRecordDate());
        record.setNotes(request.getNotes());

        WellnessRecord updatedRecord = wellnessRecordRepository.save(record);

        return WellnessRecordResponse.from(updatedRecord);
    }
}
//