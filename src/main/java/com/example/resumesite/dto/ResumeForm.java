package com.example.resumesite.dto;

import com.example.resumesite.domain.Resume;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;

@Data
public class ResumeForm {

    private Long id;

    // -- 파일 업로드 관련 필드 --
    private MultipartFile photoFile;
    private String existingPhotoPath;

    // -- 인적 사항 (Personal Details) --
    @NotBlank
    @Size(max = 100)
    private String title; // 지원 분야 (제목)

    @NotBlank private String name;
    @NotBlank private String birthDate;
    @NotBlank private String address;
    @NotBlank private String phone;
    @NotBlank @Email private String email;
    @NotBlank private String personalContact; // 긴급 연락처

    // ⭐ 추가: 성별 필드
    private String gender;

    // -- 병역 사항 --
    private String militaryStatus;
    private String militaryBranch;
    private String militaryRank;
    private String militarySpecialty;
    private String militaryPeriod;
    private Boolean veteranBenefit = false;

    // ⭐ 구조화된 반복 필드
    private List<EducationDto> educationList;
    private List<ExperienceDto> experienceList;
    private List<CertificateDto> certificationList;
    private List<ActivityDto> activityList;

    @NotBlank(message = "자기소개서는 필수입니다.") // ⭐ 누락 방지: 자기소개서 필드
    private String selfIntroduction;


    // JSON 역직렬화를 위한 static 필드 및 유틸리티 함수
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static <T> List<T> deserializeJsonList(String json, TypeReference<List<T>> typeRef) {
        if (json == null || json.trim().equals("[]") || json.trim().isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (IOException e) {
            throw new RuntimeException("JSON 역직렬화 실패: " + e.getMessage(), e);
        }
    }


    // from() 메소드 업데이트 (JSON 역직렬화 로직 포함)
    public static ResumeForm from(Resume resume) {
        ResumeForm form = new ResumeForm();
        form.setId(resume.getId());
        form.setTitle(resume.getTitle());

        // 인적 사항 매핑
        form.setExistingPhotoPath(resume.getPhotoPath());
        form.setName(resume.getName());
        form.setBirthDate(resume.getBirthDate());
        form.setAddress(resume.getAddress());
        form.setPhone(resume.getPhone());
        form.setEmail(resume.getEmail());
        form.setPersonalContact(resume.getPersonalContact());

        // ⭐ 추가: 성별 매핑
        form.setGender(resume.getGender());

        // 병역 사항 매핑
        form.setMilitaryStatus(resume.getMilitaryStatus());
        form.setMilitaryBranch(resume.getMilitaryBranch());
        form.setMilitaryRank(resume.getMilitaryRank());
        form.setMilitarySpecialty(resume.getMilitarySpecialty());
        form.setMilitaryPeriod(resume.getMilitaryPeriod());
        form.setVeteranBenefit(resume.getVeteranBenefit());

        // 자기소개서 매핑 (단순 필드)
        form.setSelfIntroduction(resume.getSelfIntroduction());


        // List 필드 역직렬화 및 매핑
        form.setEducationList(deserializeJsonList(
                resume.getEducationHistory(), new TypeReference<List<EducationDto>>() {}));
        form.setExperienceList(deserializeJsonList(
                resume.getExperienceHistory(), new TypeReference<List<ExperienceDto>>() {}));
        form.setCertificationList(deserializeJsonList(
                resume.getCertificationsAndSkills(), new TypeReference<List<CertificateDto>>() {}));
        form.setActivityList(deserializeJsonList(
                resume.getExtracurricularActivities(), new TypeReference<List<ActivityDto>>() {}));

        return form;
    }

    // Simplified Nested DTOs
    @Data
    public static class EducationDto {
        private String schoolName;
        private String period;
        private String major;
        private String notes;
    }

    @Data
    public static class ExperienceDto {
        private String companyName;
        private String period;
        private String responsibility;
    }

    @Data
    public static class CertificateDto {
        private String certificateName;
        private String acquisitionDate;
        private String publisher;
    }

    @Data
    public static class ActivityDto { // 대외활동 경험 DTO
        private String activityName;
        private String period;
        private String organization;
        private String details;
    }
}