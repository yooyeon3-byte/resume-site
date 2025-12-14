package com.example.resumesite.service;

import com.example.resumesite.domain.Resume;
import com.example.resumesite.domain.User;
import com.example.resumesite.dto.ResumeForm;
import com.example.resumesite.repository.ResumeRepository;
import com.example.resumesite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper; // 필수: JSON 직렬화를 위한 ObjectMapper 추가

@Service
@RequiredArgsConstructor
@Transactional
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String toJson(Object obj) {
        try {
            if (obj == null || (obj instanceof List && ((List<?>) obj).isEmpty())) {
                return "[]";
            }
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON 직렬화에 실패했습니다: " + e.getMessage());
        }
    }

    public Resume create(User owner, ResumeForm form) {

        User managedOwner = userRepository.findById(owner.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 ID를 찾을 수 없습니다."));

        // ⭐ 수정된 부분: 모든 필드를 새로운 DTO 구조에 맞게 매핑
        Resume resume = Resume.builder()
                // 인적 사항 및 사진 경로
                .title(form.getTitle())
                .photoPath(form.getExistingPhotoPath())
                .name(form.getName())
                .birthDate(form.getBirthDate())
                .address(form.getAddress())
                .phone(form.getPhone())
                .email(form.getEmail())
                .personalContact(form.getPersonalContact())
                .gender(form.getGender())

                // 병역 사항
                .militaryStatus(form.getMilitaryStatus())
                .militaryBranch(form.getMilitaryBranch())
                .militaryRank(form.getMilitaryRank())
                .militarySpecialty(form.getMilitarySpecialty())
                .militaryStartDate(form.getMilitaryStartDate()) // ⭐ 수정
                .militaryEndDate(form.getMilitaryEndDate())     // ⭐ 추가
                .veteranBenefit(form.getVeteranBenefit())

                // ⭐ 추가: 공개 여부
                .isPublic(form.getIsPublic() != null ? form.getIsPublic() : false)

                // 구조화된 리스트 (JSON 직렬화)
                .educationHistory(toJson(form.getEducationList()))
                .experienceHistory(toJson(form.getExperienceList()))
                .certificationsAndSkills(toJson(form.getCertificationList()))
                .extracurricularActivities(toJson(form.getActivityList()))

                // 자기소개서
                .selfIntroduction(form.getSelfIntroduction())

                .owner(managedOwner)
                .build();
        return resumeRepository.save(resume);
    }

    public Resume update(User owner, ResumeForm form) {
        Resume resume = resumeRepository.findById(form.getId())
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));
        if (!resume.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("본인 이력서만 수정할 수 있습니다.");
        }

        User managedOwner = userRepository.findById(owner.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // ⭐ 수정된 부분: 모든 필드를 새로운 DTO 구조에 맞게 매핑
        // 인적 사항 및 사진 경로
        resume.setTitle(form.getTitle());
        resume.setPhotoPath(form.getExistingPhotoPath());
        resume.setName(form.getName());
        resume.setBirthDate(form.getBirthDate());
        resume.setAddress(form.getAddress());
        resume.setPhone(form.getPhone());
        resume.setEmail(form.getEmail());
        resume.setPersonalContact(form.getPersonalContact());
        resume.setGender(form.getGender());

        // 병역 사항
        resume.setMilitaryStatus(form.getMilitaryStatus());
        resume.setMilitaryBranch(form.getMilitaryBranch());
        resume.setMilitaryRank(form.getMilitaryRank());
        resume.setMilitarySpecialty(form.getMilitarySpecialty());
        resume.setMilitaryStartDate(form.getMilitaryStartDate()); // ⭐ 수정
        resume.setMilitaryEndDate(form.getMilitaryEndDate());     // ⭐ 추가
        resume.setVeteranBenefit(form.getVeteranBenefit());

        // ⭐ 추가: 공개 여부
        resume.setIsPublic(form.getIsPublic() != null ? form.getIsPublic() : false);


        // 구조화된 리스트 (JSON 직렬화)
        resume.setEducationHistory(toJson(form.getEducationList()));
        resume.setExperienceHistory(toJson(form.getExperienceList()));
        resume.setCertificationsAndSkills(toJson(form.getCertificationList()));
        resume.setExtracurricularActivities(toJson(form.getActivityList()));

        // 자기소개서
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

    // 관리자 전용: 모든 이력서를 가져옵니다.
    @Transactional(readOnly = true)
    public List<Resume> findAll() {
        return resumeRepository.findAll();
    }

    // ⭐ 추가: 기업 유저 전용 - 공개된 이력서(isPublic=true)만 가져옵니다.
    @Transactional(readOnly = true)
    public List<Resume> findAllPublicResumes() {
        return resumeRepository.findByIsPublic(true);
    }

    @Transactional(readOnly = true)
    public Resume findById(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));
    }

    // ⭐ 추가: 이력서 공개 여부를 토글하고 변경사항을 저장하는 메소드
    public void togglePublicStatus(Long resumeId, User owner) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));

        // 소유자 검증
        if (!resume.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("본인 이력서만 공개/비공개 전환할 수 있습니다.");
        }

        resume.setIsPublic(!resume.getIsPublic()); // 상태 반전
        // @Transactional에 의해 트랜잭션 종료 시점에 변경 사항이 자동 저장됩니다.
    }
}