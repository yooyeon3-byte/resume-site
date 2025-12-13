package com.example.resumesite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 프로젝트 루트의 'uploads/' 디렉토리 경로를 가져옵니다.
        // 'file:' 접두사는 이 경로가 파일 시스템의 물리적 경로임을 스프링에 알려줍니다.
        String rootPath = System.getProperty("user.dir");
        String uploadPath = "file:" + rootPath + "/uploads/";

        // 2. 웹에서 '/uploads/**' 경로로 요청이 들어오면
        // 실제로는 'uploads/' 디렉토리(물리적 경로)에서 파일을 찾도록 매핑합니다.
        // ResumeController에서 저장한 웹 경로(예: /uploads/photos/...)와 일치합니다.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}