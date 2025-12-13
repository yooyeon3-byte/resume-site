package com.example.resumesite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentForm {

    private Long boardId; // 연결된 게시글 ID

    private Long commentId; // 수정/삭제 시 필요

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;
}