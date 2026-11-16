package com.example.jobportal.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.jobportal.dto.CandidateRequest;
import com.example.jobportal.entity.Candidate;
import com.example.jobportal.repository.CandidateRepository;

@Service
public class CandidateService {

    private final CandidateRepository repository;

    public CandidateService(CandidateRepository repository) {
        this.repository = repository;
    }

    public Candidate saveCandidate(
            CandidateRequest request,
            MultipartFile resume) throws IOException {

        // Basic validation
        if (request.getFirstName() == null ||
            request.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (request.getEmail() == null ||
            request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (resume == null || resume.isEmpty()) {
            throw new IllegalArgumentException("Resume is required");
        }

        if (resume.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Resume must be below 5 MB");
        }

        String originalName = resume.getOriginalFilename();

        if (originalName == null || !originalName.contains(".")) {
            throw new IllegalArgumentException("Invalid resume filename");
        }

        String extension = originalName.substring(
                originalName.lastIndexOf(".")).toLowerCase();

        if (!extension.matches("\\.(pdf|doc|docx)")) {
            throw new IllegalArgumentException(
                    "Only PDF, DOC and DOCX files are allowed");
        }

        // Create upload folder
        Path uploadDirectory = Path.of("uploads", "resumes")
                .toAbsolutePath().normalize();

        Files.createDirectories(uploadDirectory);

        // Generate a unique filename
        String storedFileName = UUID.randomUUID() + extension;

        Path filePath = uploadDirectory.resolve(storedFileName);

        // Save resume
        Files.copy(resume.getInputStream(), filePath);

        // Create Candidate object
        Candidate candidate = new Candidate();

        candidate.setFirstName(request.getFirstName());
        candidate.setLastName(request.getLastName());
        candidate.setEmail(request.getEmail());
        candidate.setPhone(request.getPhone());
        candidate.setAddress(request.getAddress());
        candidate.setSkills(request.getSkills());
        candidate.setRoles(request.getRoles());
        candidate.setExperienceLevel(request.getExperienceLevel());
        candidate.setExperience(request.getExperience());

        candidate.setResumeFileName(originalName);
        candidate.setResumePath(filePath.toString());

        // Save candidate in MySQL
        return repository.save(candidate);
    }

    public java.util.List<Candidate> getAllCandidates() {
        return repository.findAll();
    }

    public Candidate getCandidateById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Candidate not found with ID: " + id));
    }
}

