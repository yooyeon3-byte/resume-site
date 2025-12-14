package com.example.resumesite.service;

import com.example.resumesite.domain.User;
import com.example.resumesite.dto.UserSignupDto;
import com.example.resumesite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ⭐ 수정: roleType 매개변수 추가 (일반 유저 vs 기업 유저 구분)
    public User signup(UserSignupDto dto, boolean admin, String roleType) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 서로 다릅니다.");
        }

        boolean isFirstUser = userRepository.count() == 0;

        User.Role role;
        if (isFirstUser || admin) {
            role = User.Role.ADMIN;
        } else if ("company".equals(roleType)) {
            // 기업 회원은 PENDING 상태로 시작
            role = User.Role.PENDING; // ⭐ 기업 유저는 PENDING으로 가입
        } else {
            role = User.Role.USER;
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .email(dto.getEmail())
                .role(role)
                .build();

        return userRepository.save(user);
    }

    // ⭐ 추가: 관리자가 PENDING 유저를 COMPANY로 승인하는 메소드
    public void approveCompany(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getRole() != User.Role.PENDING) {
            throw new IllegalStateException("승인 대기 중인 사용자만 승인할 수 있습니다.");
        }

        // 역할을 COMPANY로 변경
        user.setRole(User.Role.COMPANY);
        // 트랜잭션(@Transactional) 덕분에 별도의 save 호출 없이 변경사항이 반영됩니다.
    }

    // ⭐ 추가: 관리자가 PENDING 유저를 거절/삭제하는 메소드
    public void disapproveCompany(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getRole() != User.Role.PENDING) {
            throw new IllegalStateException("승인 대기 중인 사용자만 거절/삭제할 수 있습니다.");
        }

        // PENDING 상태의 사용자 계정을 삭제
        userRepository.delete(user);
    }
}