package com.example.resumesite.dto;

import com.example.resumesite.domain.Resume;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResumeForm {

    private Long id;

    @NotBlank
    @Size(max = 100)
    private String title; // 이력서 제목

    @NotBlank(message = "인적 사항 및 연락처는 필수입니다.")
    private String personalContact; // 연락처, 주소, 이메일 등

    @NotBlank(message = "학력 사항은 필수입니다.")
    private String educationHistory; // 학력 (학교, 전공, 기간 등)

    @NotBlank(message = "경력 사항은 필수입니다.")
    private String experienceHistory; // 경력 (회사, 직위, 기간 등)

    @NotBlank(message = "자격 및 기술 사항은 필수입니다.")
    private String certificationsAndSkills; // 자격증 및 기술 스택

    @NotBlank(message = "자기소개서는 필수입니다.")
    private String selfIntroduction; // 자기소개서 (기존 content 필드 대체)

    // from() 메소드 업데이트
    public static ResumeForm from(Resume resume) {
        ResumeForm form = new ResumeForm();
        form.setId(resume.getId());
        form.setTitle(resume.getTitle());
        // ⭐ 신규 필드 매핑
        form.setPersonalContact(resume.getPersonalContact());
        form.setEducationHistory(resume.getEducationHistory());
        form.setExperienceHistory(resume.getExperienceHistory());
        form.setCertificationsAndSkills(resume.getCertificationsAndSkills());
        form.setSelfIntroduction(resume.getSelfIntroduction());

        return form;
    }
}