package com.example.resumesite.dto;

import com.example.resumesite.domain.Board;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BoardForm {

    private Long id;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자를 넘을 수 없습니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    // ⭐ 추가: 분류 필드
    private String category;

    // Board 엔티티에서 DTO로 변환하는 정적 메서드 (수정 폼 로딩용)
    public static BoardForm from(Board board) {
        BoardForm form = new BoardForm();
        form.setId(board.getId());
        form.setTitle(board.getTitle());
        form.setContent(board.getContent());
        form.setCategory(board.getCategory()); // ⭐ 추가
        return form;
    }
}