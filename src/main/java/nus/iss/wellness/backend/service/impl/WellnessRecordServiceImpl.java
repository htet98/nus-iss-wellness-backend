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

import java.util.List;

@Service
public class WellnessRecordServiceImpl implements WellnessRecordService {

    @Autowired
    private WellnessRecordRepository wellnessRecordRepository;
    
    // For Create : Loh Si Hua
    @Override
    public WellnessRecordResponse createRecord(WellnessRecordRequest request) {
    	WellnessRecord record = wellnessRecordRepository
    	        .findFirstByUserIdAndCategoryAndRecordDateOrderByCreatedAtDesc(
    	                request.getUserId(), request.getCategory(), request.getRecordDate())
    	        .orElseGet(WellnessRecord::new);
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
    // For update : Tan Pang Wee
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
    @Override
    public WellnessRecordResponse getRecord(Long recordId) {

        WellnessRecord record = wellnessRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        return WellnessRecordResponse.from(record);
    }

    @Override
    public List<WellnessRecordResponse> getRecordsByUserId(Long userId) {
        return wellnessRecordRepository.findByUserIdOrderByRecordDateDesc(userId)
                .stream()
                .map(WellnessRecordResponse::from)
                .toList();
    }

    @Override
    public void deleteRecord(Long recordId) {
        // Find the record or throw 404 if it doesn't exit
        WellnessRecord record = wellnessRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Record not found"));

        // Delete the record
        wellnessRecordRepository.delete(record);
    }
}

//We defined the service interface with 5 operations 
//(create (Si Hua), get all, get by category, get by id (Cecil), update (pang wee) , delete (Thiha)), 
//then implemented them in the Impl class. The service layer sits 
//between the controller and repository — it contains the business logic.