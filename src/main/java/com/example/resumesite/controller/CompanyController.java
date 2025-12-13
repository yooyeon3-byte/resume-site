package com.example.resumesite.controller;

import com.example.resumesite.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final ResumeService resumeService;

    // 전체 이력서 목록 조회 (기업 열람용)
    @GetMapping("/resumes")
    public String allResumes(Model model) {
        // 모든 이력서를 조회하여 기업용 목록 템플릿으로 전달
        model.addAttribute("resumes", resumeService.findAll());
        return "company/resume-list";
    }

    // 특정 이력서 상세 조회
    @GetMapping("/resumes/{id}")
    public String resumeDetail(@PathVariable Long id, Model model) {
        // 관리자용 이력서 상세 템플릿을 재활용
        model.addAttribute("resume", resumeService.findById(id));
        return "admin/resume-detail";
    }
}