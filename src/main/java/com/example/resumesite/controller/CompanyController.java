package com.example.resumesite.controller;

import com.example.resumesite.domain.User;
import com.example.resumesite.security.CustomUserDetails;
import com.example.resumesite.service.ResumeService; // ⭐ 이 import가 필요합니다.
import com.example.resumesite.service.ScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final ResumeService resumeService;
    private final ScrapService scrapService;

    // 전체 이력서 목록 조회 (기업 열람용)
    @GetMapping("/resumes")
    public String allResumes(Model model) {
        // ⭐ 수정: 공개된 이력서만 조회하도록 변경 (isPublic=true)
        model.addAttribute("resumes", resumeService.findAllPublicResumes());
        return "company/resume-list";
    }

    // ⭐ 추가: 스크랩한 이력서 목록 조회
    @GetMapping("/scraps")
    public String scrapList(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("scraps", scrapService.findScrapList(userDetails.getUser()));
        return "company/scrap-list"; // 새 템플릿 사용
    }

    // 특정 이력서 상세 조회 (스크랩 여부 확인)
    @GetMapping("/resumes/{id}")
    public String resumeDetail(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {
        var resume = resumeService.findById(id);
        model.addAttribute("resume", resume);

        // 기업 사용자인 경우 스크랩 여부 확인
        if (userDetails != null && userDetails.getUser().getRole() == User.Role.COMPANY) {
            boolean isScrapped = scrapService.isScrapped(id, userDetails.getUser());
            model.addAttribute("isScrapped", isScrapped);
        }

        // Admin/Company는 동일 템플릿 사용
        return "admin/resume-detail";
    }

    // ⭐ 추가: 스크랩/스크랩 취소 토글 처리
    @PostMapping("/resumes/{id}/toggle-scrap")
    public String toggleScrap(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails.getUser().getRole() != User.Role.COMPANY) {
            return "redirect:/company/resumes/" + id + "?error=unauthorized";
        }

        scrapService.toggleScrap(id, userDetails.getUser());
        return "redirect:/company/resumes/" + id;
    }
}