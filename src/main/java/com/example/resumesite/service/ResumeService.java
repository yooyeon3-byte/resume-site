package com.example.resumesite.service;

import com.example.resumesite.domain.Resume;
import com.example.resumesite.domain.User;
import com.example.resumesite.dto.ResumeForm;
import com.example.resumesite.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ResumeService {

    private final ResumeRepository resumeRepository;

    public Resume create(User owner, ResumeForm form) {
        Resume resume = Resume.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .owner(owner)
                .build();
        return resumeRepository.save(resume);
    }

    public Resume update(User owner, ResumeForm form) {
        Resume resume = resumeRepository.findById(form.getId())
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));
        if (!resume.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("본인 이력서만 수정할 수 있습니다.");
        }
        resume.setTitle(form.getTitle());
        resume.setContent(form.getContent());
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
