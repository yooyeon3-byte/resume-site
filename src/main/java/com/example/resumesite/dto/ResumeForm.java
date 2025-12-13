package com.example.resumesite.dto;

import com.example.resumesite.domain.Resume;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

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
    private List<ActivityDto> activityList; // ⭐ 변경: 대외활동 경험 리스트


    // from() 메소드 업데이트 (JSON 역직렬화 로직은 생략/가정)
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

        // 병역 사항 매핑
        form.setMilitaryStatus(resume.getMilitaryStatus());
        form.setMilitaryBranch(resume.getMilitaryBranch());
        form.setMilitaryRank(resume.getMilitaryRank());
        form.setMilitarySpecialty(resume.getMilitarySpecialty());
        form.setMilitaryPeriod(resume.getMilitaryPeriod());
        form.setVeteranBenefit(resume.getVeteranBenefit());

        // List 필드 역직렬화 및 매핑 (복잡하여 로직 생략)
        // form.setEducationList(JsonConverter.deserialize(resume.getEducationHistory()));
        // form.setExperienceList(JsonConverter.deserialize(resume.getExperienceHistory()));
        // form.setCertificationList(JsonConverter.deserialize(resume.getCertificationsAndSkills()));
        // form.setActivityList(JsonConverter.deserialize(resume.getExtracurricularActivities()));

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
    public static class ActivityDto { // ⭐ 대외활동 경험 DTO
        private String activityName;
        private String period;
        private String organization;
        private String details;
    }
}