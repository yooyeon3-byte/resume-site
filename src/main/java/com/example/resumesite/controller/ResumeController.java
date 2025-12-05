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

    // ⭐ 기존 newForm과 editForm을 대체하는 통합 폼 메소드
    @GetMapping({"/new", "/{id}/edit"}) // /resumes/new 또는 /resumes/{id}/edit 모두 처리
    public String form(Model model,
                       // /new 경로에서는 id가 없으므로 required = false
                       @PathVariable(required = false) Long id,
                       @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (id != null) {
            // 이력서 수정 시: 기존 editForm의 로직 사용
            var resume = resumeService.findById(id);

            // 소유자 검증
            if (!resume.getOwner().getId().equals(userDetails.getUser().getId())) {
                return "redirect:/resumes"; // 권한 없음
            }

            // Resume 엔티티를 ResumeForm DTO로 변환 (기존 로직 유지)
            ResumeForm form = new ResumeForm();
            form.setId(resume.getId());
            form.setTitle(resume.getTitle());
            form.setContent(resume.getContent());
            model.addAttribute("resumeForm", form);

        } else {
            // 새 이력서 작성 시: /resumes/new 로 접속
            model.addAttribute("resumeForm", new ResumeForm());
        }
        return "resume/form";
    }

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

    // ⭐ update 메소드의 매핑은 /resumes/{id}로 유지 (POST)
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