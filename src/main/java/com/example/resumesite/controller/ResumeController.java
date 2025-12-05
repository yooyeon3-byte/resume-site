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

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("resumeForm", new ResumeForm());
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

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        var resume = resumeService.findById(id);
        if (!resume.getOwner().getId().equals(userDetails.getUser().getId())) {
            return "redirect:/resumes";
        }
        ResumeForm form = new ResumeForm();
        form.setId(resume.getId());
        form.setTitle(resume.getTitle());
        form.setContent(resume.getContent());
        model.addAttribute("resumeForm", form);
        return "resume/form";
    }

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
