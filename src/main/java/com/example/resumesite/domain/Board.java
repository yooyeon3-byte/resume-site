package com.example.resumesite.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title; // 게시글 제목

    @Lob @Column(nullable = false)
    private String content; // 게시글 내용

    // ...
// @Builder.Default 를 추가하여 @Builder 사용 시 초기값(0)이 적용되도록 합니다.
    @Builder.Default
    private int viewCount = 0; // 조회수
// ...

    // ⭐ 추가: 게시글 분류 (자유, 질문, 정보공유)
    @Column(length = 20)
    private String category;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 작성자 (User 엔티티와 ManyToOne 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    // ⭐ 추가: 댓글 리스트
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt asc") // 댓글은 작성 순서대로 정렬
    private List<Comment> comments;

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