package com.example.resumesite.controller;

import com.example.resumesite.domain.User;
import com.example.resumesite.dto.BoardForm;
import com.example.resumesite.security.CustomUserDetails;
import com.example.resumesite.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    // 게시글 목록
    @GetMapping
    public String list(Model model) {
        model.addAttribute("posts", boardService.findAll());
        return "board/list";
    }

    // 게시글 작성 폼 (로그인 필수)
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("boardForm", new BoardForm());
        return "board/form";
    }

    // 게시글 작성 처리
    @PostMapping
    public String create(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @Valid @ModelAttribute BoardForm form,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "board/form";
        }

        try {
            User author = userDetails.getUser();
            boardService.create(author, form);
        } catch (Exception e) {
            bindingResult.reject(null, "게시글 작성 중 오류가 발생했습니다.");
            return "board/form";
        }

        return "redirect:/board";
    }

    // 게시글 상세 보기
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        boardService.incrementViewCount(id); // 조회수 증가
        model.addAttribute("post", boardService.findById(id));
        return "board/detail";
    }

    // 게시글 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        var post = boardService.findById(id);

        if (!post.getAuthor().getId().equals(userDetails.getUser().getId())) {
            return "redirect:/board/" + id + "?error=unauthorized";
        }

        model.addAttribute("boardForm", BoardForm.from(post));
        return "board/form";
    }

    // 게시글 수정 처리
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         @Valid @ModelAttribute BoardForm form,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "board/form";
        }

        try {
            boardService.update(id, userDetails.getUser(), form);
        } catch (IllegalStateException e) {
            // 권한 오류 처리
            return "redirect:/board/" + id + "?error=unauthorized";
        } catch (Exception e) {
            bindingResult.reject(null, "게시글 수정 중 오류가 발생했습니다.");
            return "board/form";
        }

        return "redirect:/board/" + id;
    }

    // 게시글 삭제 처리
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            boardService.delete(id, userDetails.getUser());
        } catch (IllegalStateException e) {
            return "redirect:/board/" + id + "?error=unauthorized";
        } catch (Exception e) {
            // 다른 종류의 오류 처리
            return "redirect:/board/" + id + "?error=deleteFailed";
        }

        return "redirect:/board";
    }
}