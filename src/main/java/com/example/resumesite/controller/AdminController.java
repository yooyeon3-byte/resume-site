package com.example.resumesite.controller;

import com.example.resumesite.domain.User;
import com.example.resumesite.service.ResumeService;
import com.example.resumesite.service.UserService; // UserService import 유지
import com.example.resumesite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ResumeService resumeService;
    private final UserRepository userRepository;
    private final UserService userService; // ⭐ 수정: UserService를 final 필드로 선언 및 주입

    // 기존 allResumes 메소드...
    @GetMapping("/resumes")
    public String allResumes(Model model) {
        model.addAttribute("resumes", resumeService.findAll());
        return "admin/resume-list";
    }

    @GetMapping("/resumes/{id}")
    public String resumeDetail(@PathVariable Long id, Model model) {
        model.addAttribute("resume", resumeService.findById(id));
        return "admin/resume-detail";
    }

    // ⭐ 기업 승인 관리 페이지 (PENDING 목록 조회) - 경로: /admin/companies
    @GetMapping("/companies")
    public String pendingCompanies(Model model) {
        // PENDING 상태인 사용자만 조회
        List<User> pendingUsers = userRepository.findByRole(User.Role.PENDING);
        model.addAttribute("pendingUsers", pendingUsers);
        return "admin/pending-companies"; // 템플릿 파일명
    }

    // ⭐ 기업 승인 처리 (POST 요청) - 경로: /admin/companies/{id}/approve
    @PostMapping("/companies/{id}/approve")
    public String approveCompany(@PathVariable Long id) { // ⭐ 수정: UserService 매개변수 제거
        try {
            userService.approveCompany(id); // ⭐ 수정: 주입된 필드(this.userService) 사용
        } catch (Exception e) {
            // 실패 처리
            return "redirect:/admin/companies?error=" + e.getMessage();
        }
        return "redirect:/admin/companies?success";
    }
}