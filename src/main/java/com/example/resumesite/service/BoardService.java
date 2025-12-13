package com.example.resumesite.service;

import com.example.resumesite.domain.Board;
import com.example.resumesite.domain.User;
import com.example.resumesite.dto.BoardForm;
import com.example.resumesite.repository.BoardRepository;
import com.example.resumesite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional(readOnly = true)
    public List<Board> findAll() {
        // 최신 글이 위에 오도록 id를 내림차순 정렬
        return boardRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
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

        return board; // @Transactional 덕분에 자동 저장
    }

    public void delete(Long boardId, User currentUser) {
        Board board = findById(boardId);

        // 작성자 검증
        if (!board.getAuthor().getId().equals(currentUser.getId())) {
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