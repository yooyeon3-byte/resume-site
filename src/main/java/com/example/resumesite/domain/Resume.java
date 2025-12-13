package com.example.resumesite.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    // ⭐ 추가/수정된 필드: 사진 파일 경로 및 인적 사항 분리
    @Column(length = 255)
    private String photoPath;

    @Column(length = 50) private String name;
    @Column(length = 50) private String birthDate; // 생년월일
    @Column(length = 100) private String address; // 주소
    @Column(length = 30) private String phone; // 전화
    @Column(length = 100) private String email; // 이메일
    @Column(length = 30) private String personalContact; // 긴급 연락처 (기존 필드 재활용)

    @Column(length = 10) private String gender;

    // ⭐ 병역 사항 (양식 반영)
    @Column(length = 20) private String militaryStatus; // 병역 구분
    @Column(length = 20) private String militaryBranch; // 군별
    @Column(length = 20) private String militaryRank;   // 계급
    @Column(length = 20) private String militarySpecialty; // 병과
    @Column(length = 50) private String militaryStartDate; // ⭐ 수정: 복무 시작 기간 필드
    @Column(length = 50) private String militaryEndDate; // ⭐ 추가: 복무 종료 기간 필드
    @Column(nullable = false) private Boolean veteranBenefit; // 보훈대상

    // ⭐ 구조화된 리스트 데이터 (JSON 문자열로 저장)
    @Lob @Column(nullable = false)
    private String educationHistory; // 학력 사항 리스트 (JSON)

    @Lob @Column(nullable = false)
    private String experienceHistory; // 경력 사항 리스트 (JSON)

    @Lob @Column(nullable = false)
    private String certificationsAndSkills; // 자격 및 기술 리스트 (JSON)

    @Lob @Column(nullable = false)
    private String extracurricularActivities; // 대외활동 경험 리스트 (JSON)

    @Lob @Column(nullable = false) // ⭐ 복구: 자기소개서 필드 복구
    private String selfIntroduction;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}