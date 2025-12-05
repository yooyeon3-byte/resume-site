package com.example.resumesite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResumeForm {

    private Long id; // 수정 시 사용

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    private String content;
}
