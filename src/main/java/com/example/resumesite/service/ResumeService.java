package com.example.resumesite.service;

import com.example.resumesite.domain.Resume;
import com.example.resumesite.domain.User;
import com.example.resumesite.dto.ResumeForm;
import com.example.resumesite.repository.ResumeRepository;
import com.example.resumesite.repository.UserRepository; // ⭐ UserRepository 임포트 추가
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository; // ⭐ UserRepository 필드 추가


    // ⭐ create 메소드 수정
    public Resume create(User owner, ResumeForm form) {
        Resume resume = Resume.builder()
                .title(form.getTitle())
                // ⭐ 신규 필드 매핑
                .personalContact(form.getPersonalContact())
                .educationHistory(form.getEducationHistory())
                .experienceHistory(form.getExperienceHistory())
                .certificationsAndSkills(form.getCertificationsAndSkills())
                .selfIntroduction(form.getSelfIntroduction())

                .owner(owner) // (혹은 ManagedOwner)
                .build();
        return resumeRepository.save(resume);
    }

    // ⭐ update 메소드 수정
    public Resume update(User owner, ResumeForm form) {
        Resume resume = resumeRepository.findById(form.getId())
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));
        if (!resume.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("본인 이력서만 수정할 수 있습니다.");
        }

        // ⭐ update 시에도 Managed Entity 사용
        User managedOwner = userRepository.findById(owner.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // ⭐ 신규 필드 업데이트 로직 추가
        resume.setTitle(form.getTitle());
        resume.setPersonalContact(form.getPersonalContact());
        resume.setEducationHistory(form.getEducationHistory());
        resume.setExperienceHistory(form.getExperienceHistory());
        resume.setCertificationsAndSkills(form.getCertificationsAndSkills());
        resume.setSelfIntroduction(form.getSelfIntroduction());

        return resume;
    }

    public void delete(User owner, Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));
        if (!resume.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("본인 이력서만 삭제할 수 있습니다.");
        }
        resumeRepository.delete(resume);
    }

    @Transactional(readOnly = true)
    public List<Resume> findMyResumes(User owner) {
        return resumeRepository.findByOwner(owner);
    }

    @Transactional(readOnly = true)
    public List<Resume> findAll() {
        return resumeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Resume findById(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));
    }
}
