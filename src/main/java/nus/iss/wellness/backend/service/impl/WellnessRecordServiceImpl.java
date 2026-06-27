package nus.iss.wellness.backend.service.impl;

import nus.iss.wellness.backend.dto.request.WellnessRecordRequest;
import nus.iss.wellness.backend.dto.response.WellnessRecordResponse;
import nus.iss.wellness.backend.model.WellnessRecord;
import nus.iss.wellness.backend.repository.WellnessRecordRepository;
import nus.iss.wellness.backend.service.WellnessRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<WellnessRecordResponse> getRecordsByUser(Long userId) {
        return wellnessRecordRepository.findByUserIdOrderByRecordDateDesc(userId)
                .stream().map(WellnessRecordResponse::from).collect(Collectors.toList());
    }

    @Override
    public List<WellnessRecordResponse> getRecordsByUserAndCategory(Long userId, WellnessRecord.Category category) {
        return wellnessRecordRepository.findByUserIdAndCategoryOrderByRecordDateDesc(userId, category)
                .stream().map(WellnessRecordResponse::from).collect(Collectors.toList());
    }

    @Override
    public WellnessRecordResponse getRecordById(Long id) {
        WellnessRecord record = wellnessRecordRepository.findById(id)
                .orElseThrow(() -> new nus.iss.wellness.backend.exception.ResourceNotFoundException("Record not found with id: " + id));
        return WellnessRecordResponse.from(record);
    }

    @Override
    public WellnessRecordResponse updateRecord(Long id, WellnessRecordRequest request) {
        WellnessRecord record = wellnessRecordRepository.findById(id)
                .orElseThrow(() -> new nus.iss.wellness.backend.exception.ResourceNotFoundException("Record not found with id: " + id));

        if (!record.getUserId().equals(request.getUserId())) {
            throw new nus.iss.wellness.backend.exception.BadRequestException("userId cannot be changed for an existing record");
        }

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
    public void deleteRecord(Long id) {
        if (!wellnessRecordRepository.existsById(id)) {
            throw new nus.iss.wellness.backend.exception.ResourceNotFoundException("Record not found with id: " + id);
        }
        wellnessRecordRepository.deleteById(id);
    }
}
