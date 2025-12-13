package com.example.resumesite.repository;

import com.example.resumesite.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; // ⭐ List import 추가
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    // ⭐ 추가된 부분: ROLE 필드를 기준으로 사용자를 조회하는 메소드
    List<User> findByRole(User.Role role);
}
