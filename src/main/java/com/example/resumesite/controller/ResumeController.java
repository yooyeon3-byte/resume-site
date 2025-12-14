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
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private static final String UPLOAD_DIR = "uploads/photos"; // ⭐ 파일 저장 경로 정의 (프로젝트 루트 기준)

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
        // 동적 폼을 위해 최소 1개의 빈 리스트 요소를 미리 넣어줍니다.
        ResumeForm form = new ResumeForm();
        form.setIsPublic(false); // ⭐ 기본값 설정
        model.addAttribute("resumeForm", form);
        return "resume/form";
    }

    // ⭐ 이력서 생성 처리 (POST /resumes)
    @PostMapping
    public String create(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @Valid @ModelAttribute ResumeForm form,
                         BindingResult bindingResult,
                         @RequestParam("photoFile") MultipartFile photoFile) throws IOException { // ⭐ 파일 처리
        if (bindingResult.hasErrors()) {
            return "resume/form";
        }

        String photoPath = handleFileUpload(photoFile, null); // 신규 생성 시 기존 경로는 null
        form.setExistingPhotoPath(photoPath); // DTO에 최종 경로 설정 (서비스에서 사용)

        // 실제로 DTO를 JSON으로 직렬화하고 엔티티를 저장하는 로직은 ResumeService에 있다고 가정
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

        // ResumeForm.from()을 사용하여 DTO 생성 (JSON 역직렬화 포함)
        ResumeForm form = ResumeForm.from(resume);

        model.addAttribute("resumeForm", form);
        return "resume/form";
    }

    // ⭐ 이력서 수정 처리 (POST /resumes/{id})
    @PostMapping("/{id}")
    public String update(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @PathVariable Long id,
                         @Valid @ModelAttribute ResumeForm form,
                         BindingResult bindingResult,
                         @RequestParam("photoFile") MultipartFile photoFile) throws IOException { // ⭐ 파일 처리
        if (bindingResult.hasErrors()) {
            return "resume/form";
        }

        // 기존 경로를 DTO에서 가져와 파일 업로드 처리
        String photoPath = handleFileUpload(photoFile, form.getExistingPhotoPath());
        form.setExistingPhotoPath(photoPath); // DTO에 최종 경로 설정

        form.setId(id);
        resumeService.update(userDetails.getUser(), form);
        return "redirect:/resumes";
    }

    // ⭐ 파일 업로드 유틸리티: 새 파일이 있으면 저장하고, 없으면 기존 경로를 유지합니다.
    private String handleFileUpload(MultipartFile file, String existingPath) throws IOException {
        if (file != null && !file.isEmpty()) {
            Path uploadDir = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 기존 파일 삭제 로직 추가 가능 (여기서는 생략)

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            return "/" + UPLOAD_DIR + "/" + fileName; // 웹 접근 가능한 상대 경로 반환
        }
        return existingPath; // 새 파일이 없으면 기존 경로 반환
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @PathVariable Long id) {
        resumeService.delete(userDetails.getUser(), id);
        return "redirect:/resumes";
    }

    // ⭐ 수정: 이력서 공개 여부 토글 (로직을 Service로 위임)
    @PostMapping("/{id}/toggle-public")
    public String togglePublic(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            // ⭐ Service의 쓰기 가능한 트랜잭션 메서드를 호출
            resumeService.togglePublicStatus(id, userDetails.getUser());
        } catch (IllegalStateException e) {
            return "redirect:/resumes?error=unauthorized";
        } catch (Exception e) {
            // 실패 처리 (예: 이력서 ID를 찾을 수 없는 경우)
            return "redirect:/resumes?error=toggleFailed";
        }
        return "redirect:/resumes";
    }
}