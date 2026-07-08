package nus.iss.wellness.backend.controller;

import jakarta.validation.Valid;
import nus.iss.wellness.backend.dto.request.WellnessRecordRequest;
import nus.iss.wellness.backend.dto.response.WellnessRecordResponse;
import nus.iss.wellness.backend.service.WellnessRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wellness")
public class WellnessRecordController {

    @Autowired
    private WellnessRecordService wellnessRecordService;

 // Loh Si Hua - 27 Jun 2026
    @PostMapping("/records")
    public ResponseEntity<WellnessRecordResponse> createRecord(@Valid @RequestBody WellnessRecordRequest request) {
        WellnessRecordResponse response = wellnessRecordService.createRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // For edit - Tan Pang Wee
    @PutMapping("/{recordId}")
    public WellnessRecordResponse updateRecord(
            @PathVariable Long recordId,
            @RequestBody WellnessRecordRequest request) {

        return wellnessRecordService.updateRecord(recordId, request);
    }
    @GetMapping("/{recordId}")
    public WellnessRecordResponse getRecord(@PathVariable Long recordId) {
        return wellnessRecordService.getRecord(recordId);
    }
    @GetMapping("/userid/{userId}")
    public List<WellnessRecordResponse> getRecordsByUserId(@PathVariable Long userId) {
        return wellnessRecordService.getRecordsByUserId(userId);
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long recordId) {
        wellnessRecordService.deleteRecord(recordId);

        // Return 204 No Content upon successful deletion
        return ResponseEntity.noContent().build();
    }
}

//We added 5 REST endpoints under /api/wellness/records — POST to create (Si Hua), 
//GET to list (with optional category filter)(Cecil), 
//GET by id,  PUT to update (Pang wee), and DELETE (Thiha). 
//This is what Postman actually calls.