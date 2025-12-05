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
                         Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }
        try {
            // 첫 회원은 관리자, 나머지는 일반 유저로 하고 싶으면 여기 로직 추가 가능
            userService.signup(dto, false);
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
