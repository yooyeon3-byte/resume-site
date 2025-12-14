package com.example.resumesite.service;

import com.example.resumesite.domain.Board;
import com.example.resumesite.domain.User;
import com.example.resumesite.dto.BoardForm;
import com.example.resumesite.repository.BoardRepository;
import com.example.resumesite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // ⭐ 수정: 페이징, 검색, 분류를 통합 처리하는 메서드
    @Transactional(readOnly = true)
    public Page<Board> findBoardList(int page, String category, String keyword) {
        // 페이지 번호는 0부터 시작, 한 페이지에 10개, id를 내림차순 정렬
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCategory = category != null && !category.equals("all");

        if (hasKeyword) {
            String searchKeyword = "%" + keyword + "%";

            if (hasCategory) {
                // 분류 + 검색어 (제목 또는 내용)
                return boardRepository.findByCategoryAndTitleContainingOrCategoryAndContentContaining(
                        category, searchKeyword, category, searchKeyword, pageable
                );
            } else {
                // 전체 분류 + 검색어 (제목 또는 내용)
                return boardRepository.findAll((root, query, criteriaBuilder) ->
                                criteriaBuilder.or(
                                        criteriaBuilder.like(root.get("title"), searchKeyword),
                                        criteriaBuilder.like(root.get("content"), searchKeyword)
                                )
                        , pageable);
            }
        } else if (hasCategory) {
            // 분류만 선택된 경우
            return boardRepository.findByCategory(category, pageable);
        }

        // 키워드 없고, 분류도 전체인 경우 (전체 페이징)
        return boardRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Board findById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    public Board create(User author, BoardForm form) {
        User managedAuthor = userRepository.findById(author.getId())
                .orElseThrow(() -> new IllegalArgumentException("작성자 정보를 찾을 수 없습니다."));

        Board board = Board.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .category(form.getCategory() != null ? form.getCategory() : "자유") // ⭐ 분류 추가
                .author(managedAuthor)
                .viewCount(0)
                .build();

        return boardRepository.save(board);
    }

    public Board update(Long boardId, User currentUser, BoardForm form) {
        Board board = findById(boardId);

        // 작성자 검증
        if (!board.getAuthor().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("게시글을 수정할 권한이 없습니다.");
        }

        board.setTitle(form.getTitle());
        board.setContent(form.getContent());
        board.setCategory(form.getCategory()); // ⭐ 분류 업데이트
        return board; // @Transactional 덕분에 자동 저장
    }

    public void delete(Long boardId, User currentUser) {
        Board board = findById(boardId);

        // 작성자 검증
        if (!board.getAuthor().getId().equals(currentUser.getId()) && currentUser.getRole() != User.Role.ADMIN) {
            throw new IllegalStateException("게시글을 삭제할 권한이 없습니다.");
        }

        boardRepository.delete(board);
    }

    public void incrementViewCount(Long id) {
        Board board = findById(id);
        board.setViewCount(board.getViewCount() + 1);
        // @Transactional 덕분에 자동 저장
    }
}