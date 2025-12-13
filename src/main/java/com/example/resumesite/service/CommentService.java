package com.example.resumesite.service;

import com.example.resumesite.domain.Board;
import com.example.resumesite.domain.Comment;
import com.example.resumesite.domain.User;
import com.example.resumesite.dto.CommentForm;
import com.example.resumesite.repository.BoardRepository;
import com.example.resumesite.repository.CommentRepository;
import com.example.resumesite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public Comment create(User author, Long boardId, CommentForm form) {
        User managedAuthor = userRepository.findById(author.getId())
                .orElseThrow(() -> new IllegalArgumentException("작성자 정보를 찾을 수 없습니다."));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .content(form.getContent())
                .author(managedAuthor)
                .board(board)
                .build();

        return commentRepository.save(comment);
    }

    public void delete(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}