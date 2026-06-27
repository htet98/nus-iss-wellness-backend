package nus.iss.wellness.backend.service;

import nus.iss.wellness.backend.dto.request.AddWellnessRecordRequest;
import nus.iss.wellness.backend.model.WellnessRecord;

public interface WellnessRecordService {
    WellnessRecord addWellnessRecord(AddWellnessRecordRequest request);
}