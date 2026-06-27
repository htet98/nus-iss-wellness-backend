package nus.iss.wellness.backend.controller;

import nus.iss.wellness.backend.dto.request.AddWellnessRecordRequest;
import nus.iss.wellness.backend.model.WellnessRecord;
import nus.iss.wellness.backend.service.WellnessRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wellness")
public class WellnessRecordController {

    @Autowired
    private WellnessRecordService wellnessRecordService;

    @PostMapping("/records")
    public ResponseEntity<WellnessRecord> addWellnessRecord(
            @RequestBody AddWellnessRecordRequest request) {
        WellnessRecord saved = wellnessRecordService.addWellnessRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}