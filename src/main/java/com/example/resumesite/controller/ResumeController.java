package com.example.resumesite.controller;

import com.example.resumesite.domain.User;
import com.example.resumesite.dto.ResumeForm;
import com.example.resumesite.security.CustomUserDetails;
import com.example.resumesite.service.DocxService;
import com.example.resumesite.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final DocxService docxService;
    private static final String UPLOAD_DIR = "uploads/photos";
    private static final String UPLOADS_ROOT_DIR = "uploads"; // 사용하지 않지만 기존 변수 유지

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
        form.setIsPublic(false);

        // 폼 렌더링 시 NullPointerException 방지를 위해 빈 리스트로 초기화
        form.setEducationList(List.of());
        form.setExperienceList(List.of());
        form.setCertificationList(List.of());
        form.setActivityList(List.of());

        model.addAttribute("resumeForm", form);
        return "resume/form";
    }

    // ⭐ 이력서 생성 처리 (POST /resumes)
    @PostMapping
    public String create(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @Valid @ModelAttribute ResumeForm form,
                         BindingResult bindingResult,
                         // required = false 유지
                         @RequestParam(value = "photoFile", required = false) MultipartFile photoFile) throws IOException {
        if (bindingResult.hasErrors()) {
            return "resume/form";
        }

        String photoPath = handleFileUpload(photoFile, null);
        form.setExistingPhotoPath(photoPath);

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
                         // required = false 유지
                         @RequestParam(value = "photoFile", required = false) MultipartFile photoFile) throws IOException {
        if (bindingResult.hasErrors()) {
            return "resume/form";
        }

        // 기존 경로를 DTO에서 가져와 파일 업로드 처리
        String photoPath = handleFileUpload(photoFile, form.getExistingPhotoPath());
        form.setExistingPhotoPath(photoPath);

        form.setId(id);
        resumeService.update(userDetails.getUser(), form);
        return "redirect:/resumes";
    }

    // ⭐ 이력서 DOCX 다운로드 엔드포인트
    @GetMapping("/{id}/download-docx")
    public ResponseEntity<byte[]> downloadDocx(@PathVariable Long id,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {

        var resume = resumeService.findById(id);

        // 권한 검증: 본인 이력서만 다운로드 가능
        if (!resume.getOwner().getId().equals(userDetails.getUser().getId())) {
            return ResponseEntity.status(403).body("접근 권한이 없습니다.".getBytes());
        }

        try {
            // 1. 모델 데이터 준비 (이력서 상세 정보)

            // ⭐ DOCX 변환 시 사용할 사진의 절대 경로 URI를 계산합니다. (Docx4j 호환성 강화)
            String absolutePhotoUri = "";
            if (resume.getPhotoPath() != null && !resume.getPhotoPath().isEmpty()) {
                // `resume.getPhotoPath()`: `/uploads/photos/...` 형태

                // 1. 웹 경로(/uploads/photos/...)에서 선행 슬래시 제거 (있는 경우)
                String webPath = resume.getPhotoPath().startsWith("/")
                        ? resume.getPhotoPath().substring(1)
                        : resume.getPhotoPath();

                // 2. 프로젝트 루트 경로와 결합하여 절대 파일 시스템 경로를 얻습니다.
                Path fullPath = Paths.get(System.getProperty("user.dir"), webPath);

                // 3. URI 생성 후 템플릿에 전달. Docx4j에서 인코딩 문제를 피하기 위해 toASCIIString()을 사용하여 URI를 완벽하게 인코딩합니다.
                absolutePhotoUri = fullPath.toUri().toASCIIString();
            }

            // DOCX 변환 시 필요한 플래그와 변수들을 안전하게 추가합니다.
            Map<String, Object> variables = Map.of(
                    "resume", resume,
                    "isScrapped", false,
                    "isDocxDownload", true, // 템플릿의 조건부 렌더링을 위해 필요
                    "absolutePhotoUri", absolutePhotoUri // ⭐ 계산된 절대 경로 URI 전달
            );

            // 2. 템플릿을 HTML로 렌더링 (admin/resume-detail.html 템플릿 재활용)
            String htmlContent = docxService.renderHtml("admin/resume-detail", variables);

            // 3. HTML을 DOCX로 변환
            byte[] docxBytes = docxService.convertHtmlToDocx(htmlContent);

            // 4. HTTP 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            String filename = resume.getName() + "_이력서.docx";
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // 5. DOCX 파일 반환
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(docxBytes);

        } catch (Exception e) {
            e.printStackTrace();
            // 오류 메시지를 응답 본문에 담아 반환하여 디버깅에 도움을 줄 수 있습니다.
            return ResponseEntity.status(500).body(("DOCX 생성 중 오류가 발생했습니다: " + e.getMessage()).getBytes());
        }
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

            return "/" + UPLOAD_DIR + "/" + fileName; // 웹 접근 가능한 상대 경로 반환 (예: /uploads/photos/...)
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
            resumeService.togglePublicStatus(id, userDetails.getUser());
        } catch (IllegalStateException e) {
            return "redirect:/resumes?error=unauthorized";
        } catch (Exception e) {
            return "redirect:/resumes?error=toggleFailed";
        }
        return "redirect:/resumes";
    }
}