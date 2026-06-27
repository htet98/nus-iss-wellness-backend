package nus.iss.wellness.backend.service;

import nus.iss.wellness.backend.dto.request.WellnessRecordRequest;
import nus.iss.wellness.backend.dto.response.WellnessRecordResponse;
import nus.iss.wellness.backend.model.WellnessRecord;

import java.util.List;

public interface WellnessRecordService {

    WellnessRecordResponse createRecord(WellnessRecordRequest request);

    List<WellnessRecordResponse> getRecordsByUser(Long userId);

    List<WellnessRecordResponse> getRecordsByUserAndCategory(Long userId, WellnessRecord.Category category);

    WellnessRecordResponse getRecordById(Long id);

    WellnessRecordResponse updateRecord(Long id, WellnessRecordRequest request);

    void deleteRecord(Long id);
}
