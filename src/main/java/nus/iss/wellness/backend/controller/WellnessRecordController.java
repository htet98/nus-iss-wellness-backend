package nus.iss.wellness.backend.controller;

import jakarta.validation.Valid;
import nus.iss.wellness.backend.dto.request.WellnessRecordRequest;
import nus.iss.wellness.backend.dto.response.WellnessRecordResponse;
import nus.iss.wellness.backend.model.WellnessRecord;
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

    @PostMapping("/records")
    public ResponseEntity<WellnessRecordResponse> createRecord(@Valid @RequestBody WellnessRecordRequest request) {
        WellnessRecordResponse response = wellnessRecordService.createRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/records")
    public ResponseEntity<List<WellnessRecordResponse>> getRecords(
            @RequestParam Long userId,
            @RequestParam(required = false) WellnessRecord.Category category) {
        List<WellnessRecordResponse> records = (category != null)
                ? wellnessRecordService.getRecordsByUserAndCategory(userId, category)
                : wellnessRecordService.getRecordsByUser(userId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/records/{id}")
    public ResponseEntity<WellnessRecordResponse> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(wellnessRecordService.getRecordById(id));
    }

    @PutMapping("/records/{id}")
    public ResponseEntity<WellnessRecordResponse> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody WellnessRecordRequest request) {
        return ResponseEntity.ok(wellnessRecordService.updateRecord(id, request));
    }

    @DeleteMapping("/records/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        wellnessRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
