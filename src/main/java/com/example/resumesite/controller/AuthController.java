package com.example.resumesite.controller;

import com.example.resumesite.dto.UserSignupDto;
import com.example.resumesite.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; // ⭐ 추가

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("userSignupDto", new UserSignupDto());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute UserSignupDto dto,
                         BindingResult bindingResult,
                         Model model,
                         @RequestParam(value = "roleType", defaultValue = "user") String roleType) { // ⭐ roleType 파라미터 받기
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        try {
            userService.signup(dto, false, roleType); // ⭐ roleType 전달

            if ("company".equals(roleType)) {
                // 기업 회원 가입 성공 시 승인 대기 페이지로 리다이렉트
                return "redirect:/login?pendingApproval";
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/signup";
        }
        return "redirect:/login?signupSuccess";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}