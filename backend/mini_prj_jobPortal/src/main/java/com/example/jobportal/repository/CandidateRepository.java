package com.example.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.jobportal.entity.Candidate;

public interface CandidateRepository
        extends JpaRepository<Candidate, Long> {

}
