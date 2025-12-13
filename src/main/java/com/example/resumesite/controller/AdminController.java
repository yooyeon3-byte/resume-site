package com.example.resumesite.controller;

import com.example.resumesite.domain.User; // ⭐ User import 추가
import com.example.resumesite.service.ResumeService; // ⭐ 누락된 ResumeService import 추가
import com.example.resumesite.service.UserService; // ⭐ UserService import 추가
import com.example.resumesite.repository.UserRepository; // ⭐ UserRepository import 추가
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
    private final UserRepository userRepository; // ⭐ UserRepository 추가

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

    // ⭐ 추가: 기업 승인 관리 페이지
    @GetMapping("/companies")
    public String pendingCompanies(Model model) {
        // PENDING 상태인 사용자만 조회
        List<User> pendingUsers = userRepository.findByRole(User.Role.PENDING);
        model.addAttribute("pendingUsers", pendingUsers);
        return "admin/pending-companies"; // 새 템플릿 사용
    }

    // ⭐ 추가: 기업 승인 처리
    @PostMapping("/companies/{id}/approve")
    public String approveCompany(@PathVariable Long id, UserService userService) {
        try {
            userService.approveCompany(id);
        } catch (Exception e) {
            // 실패 처리 (예: 이미 승인되었거나 존재하지 않는 사용자)
            return "redirect:/admin/companies?error=" + e.getMessage();
        }
        return "redirect:/admin/companies?success";
    }
}