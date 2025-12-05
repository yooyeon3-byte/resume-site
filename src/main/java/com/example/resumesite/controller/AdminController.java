package com.example.resumesite.controller;

import com.example.resumesite.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ResumeService resumeService;

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
}
