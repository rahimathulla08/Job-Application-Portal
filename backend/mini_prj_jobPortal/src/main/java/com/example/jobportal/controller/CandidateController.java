package com.example.jobportal.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.jobportal.dto.CandidateRequest;
import com.example.jobportal.entity.Candidate;
import com.example.jobportal.service.CandidateService;

@RestController
@RequestMapping("/api/candidates")
@CrossOrigin(origins = {
    "http://localhost:5500",
    "http://127.0.0.1:5500"
})
public class CandidateController {

    private final CandidateService service;

    public CandidateController(CandidateService service) {
        this.service = service;
    }

    // Save candidate details and resume
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> saveCandidate(
            @ModelAttribute CandidateRequest request,
            @RequestParam("resume") MultipartFile resume) {

        try {
            Candidate savedCandidate =
                    service.saveCandidate(request, resume);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedCandidate);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());

        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to save resume");

        }
    }

    // Get all candidates
    @GetMapping
    public ResponseEntity<List<Candidate>> getAllCandidates() {

        return ResponseEntity.ok(service.getAllCandidates());
    }

    // Get candidate using ID
    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getCandidateById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getCandidateById(id));
    }
}

