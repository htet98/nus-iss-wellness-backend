package nus.iss.wellness.backend.service;

import nus.iss.wellness.backend.dto.request.WellnessRecordRequest;
import nus.iss.wellness.backend.dto.response.WellnessRecordResponse;

public interface WellnessRecordService {
    WellnessRecordResponse createRecord(WellnessRecordRequest request);
    WellnessRecordResponse updateRecord(Long recordId, WellnessRecordRequest request);
}
//