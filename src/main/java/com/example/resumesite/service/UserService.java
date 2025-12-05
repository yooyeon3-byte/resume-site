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

    public User signup(UserSignupDto dto, boolean admin) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 서로 다릅니다.");
        }

        // ⭐ 수정된 부분: 첫 번째 가입자인지 확인
        boolean isFirstUser = userRepository.count() == 0;

        // ⭐ 첫 사용자이거나 admin 플래그가 true인 경우 ADMIN 권한 부여
        User.Role role = (isFirstUser || admin) ? User.Role.ADMIN : User.Role.USER;

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .email(dto.getEmail())
                .role(role) // ⭐ 수정된 role 사용
                .build();

        return userRepository.save(user);
    }
}