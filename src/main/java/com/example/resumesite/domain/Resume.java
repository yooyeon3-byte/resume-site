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

    // ⭐ 기존 content 필드를 대체하고 새로운 필드 추가
    @Lob @Column(nullable = false)
    private String personalContact; // 인적 사항 및 연락처

    @Lob @Column(nullable = false)
    private String educationHistory; // 학력 사항

    @Lob @Column(nullable = false)
    private String experienceHistory; // 경력 사항

    @Lob @Column(nullable = false)
    private String certificationsAndSkills; // 자격 및 기술

    @Lob @Column(nullable = false)
    private String selfIntroduction; // 자기소개서 (기존 content 대체)

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