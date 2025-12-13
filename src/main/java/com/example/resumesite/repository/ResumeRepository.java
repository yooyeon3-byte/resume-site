package com.example.resumesite.repository;

import com.example.resumesite.domain.Resume;
import com.example.resumesite.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByOwner(User owner);
}