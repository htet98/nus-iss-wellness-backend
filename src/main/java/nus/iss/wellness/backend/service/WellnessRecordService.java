package nus.iss.wellness.backend.service;

import nus.iss.wellness.backend.dto.request.WellnessRecordRequest;
import nus.iss.wellness.backend.dto.response.WellnessRecordResponse;
//Loh Si Hua - 27 Jun 2026
public interface WellnessRecordService {
    WellnessRecordResponse createRecord(WellnessRecordRequest request);
    WellnessRecordResponse updateRecord(Long recordId, WellnessRecordRequest request);
}

// We defined the service interface with 5 operations 
// (create (Si Hua), get all, get by category, get by id (Cecil), update (pang wee) , delete (Thiha)), 
// then implemented them in the Impl class. The service layer sits 
// between the controller and repository — it contains the business logic.