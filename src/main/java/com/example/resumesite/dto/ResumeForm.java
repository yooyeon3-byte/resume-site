package com.example.resumesite.dto;

import com.example.resumesite.domain.Resume;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResumeForm {

    private Long id; // 수정 시 사용

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    private String content;
    // ⭐ 추가: Resume 엔티티를 ResumeForm DTO로 변환하는 정적 메소드
    public static ResumeForm from(Resume resume) {
        ResumeForm form = new ResumeForm();
        form.setId(resume.getId());
        form.setTitle(resume.getTitle());
        form.setContent(resume.getContent());
        return form;
    }
}
