package com.example.resumesite.controller;

import com.example.resumesite.domain.User;
import com.example.resumesite.dto.ResumeForm;
import com.example.resumesite.security.CustomUserDetails;
import com.example.resumesite.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    @GetMapping
    public String myResumes(@AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model) {
        User user = userDetails.getUser();
        model.addAttribute("resumes", resumeService.findMyResumes(user));
        return "resume/my-list";
    }

    // ⭐ 새 이력서 작성 (GET /resumes/new)
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("resumeForm", new ResumeForm());
        return "resume/form";
    }

    // ⭐ 이력서 생성 처리 (POST /resumes)
    @PostMapping
    public String create(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @Valid @ModelAttribute ResumeForm form,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "resume/form";
        }
        resumeService.create(userDetails.getUser(), form);
        return "redirect:/resumes";
    }

    // ⭐ 이력서 수정 폼 (GET /resumes/{id}/edit)
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        var resume = resumeService.findById(id);

        // 소유자 검증
        if (!resume.getOwner().getId().equals(userDetails.getUser().getId())) {
            return "redirect:/resumes";
        }

        // ResumeForm.from()을 사용하여 DTO 생성
        ResumeForm form = ResumeForm.from(resume);

        model.addAttribute("resumeForm", form);
        return "resume/form";
    }

    // ⭐ 이력서 수정 처리 (POST /resumes/{id})
    @PostMapping("/{id}")
    public String update(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @PathVariable Long id,
                         @Valid @ModelAttribute ResumeForm form,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "resume/form";
        }
        form.setId(id);
        resumeService.update(userDetails.getUser(), form);
        return "redirect:/resumes";
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @PathVariable Long id) {
        resumeService.delete(userDetails.getUser(), id);
        return "redirect:/resumes";
    }
}