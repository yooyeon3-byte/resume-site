package com.example.resumesite.repository;

import com.example.resumesite.domain.Scrap;
import com.example.resumesite.domain.Resume;
import com.example.resumesite.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    // 기업이 특정 이력서를 스크랩했는지 확인
    Optional<Scrap> findByCompanyAndResume(User company, Resume resume);

    // 기업이 스크랩한 목록 조회 (최신 순)
    List<Scrap> findByCompanyOrderByIdDesc(User company);
}