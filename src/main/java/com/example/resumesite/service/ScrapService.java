package com.example.resumesite.service;

import com.example.resumesite.domain.Resume;
import com.example.resumesite.domain.Scrap;
import com.example.resumesite.domain.User;
import com.example.resumesite.repository.ResumeRepository;
import com.example.resumesite.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final ResumeRepository resumeRepository;

    /**
     * 이력서 스크랩/스크랩 취소 처리
     * @return true: 스크랩됨, false: 스크랩 취소됨
     */
    public boolean toggleScrap(Long resumeId, User company) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));

        // 이미 스크랩했는지 확인
        return scrapRepository.findByCompanyAndResume(company, resume)
                .map(scrap -> {
                    // 이미 스크랩했다면 취소
                    scrapRepository.delete(scrap);
                    return false; // 스크랩 취소됨
                })
                .orElseGet(() -> {
                    // 스크랩하지 않았다면 새로 생성
                    Scrap newScrap = Scrap.builder()
                            .company(company)
                            .resume(resume)
                            .build();
                    scrapRepository.save(newScrap);
                    return true; // 스크랩됨
                });
    }

    @Transactional(readOnly = true)
    public boolean isScrapped(Long resumeId, User company) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));
        return scrapRepository.findByCompanyAndResume(company, resume).isPresent();
    }

    @Transactional(readOnly = true)
    public List<Scrap> findScrapList(User company) {
        return scrapRepository.findByCompanyOrderByIdDesc(company);
    }
}