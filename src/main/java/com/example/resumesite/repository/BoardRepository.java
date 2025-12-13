package com.example.resumesite.repository;

import com.example.resumesite.domain.Board;
import org.springframework.data.domain.Page; // ⭐ 추가
import org.springframework.data.domain.Pageable; // ⭐ 추가
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // ⭐ 검색을 위해 추가

public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> { // ⭐ 상속 추가
    // 제목, 내용, 분류, 페이징을 처리할 수 있는 메서드 (분류 + 검색)
    Page<Board> findByCategoryAndTitleContainingOrCategoryAndContentContaining(
            String category1, String title,
            String category2, String content,
            Pageable pageable
    );

    // 검색어 없이 분류만 처리
    Page<Board> findByCategory(String category, Pageable pageable);

    // findAll(Pageable pageable)는 JpaRepository에 이미 정의되어 있습니다.
}