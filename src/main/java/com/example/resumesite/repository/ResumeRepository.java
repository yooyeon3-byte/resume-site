package com.example.resumesite.repository;

import com.example.resumesite.domain.Resume;
import com.example.resumesite.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByOwner(User owner);

    // ⭐ 추가: 공개 여부(isPublic)가 true인 이력서만 조회
    List<Resume> findByIsPublic(Boolean isPublic);
}