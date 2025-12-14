package com.example.resumesite.service;

// 오류 해결을 위해 openhtmltopdf 관련 import를 모두 주석 처리합니다.
/*
import com.openhtmltopdf.pdfbox.PdfBoxRenderer;
import com.openhtmltopdf.pdfbox.PdfBoxRenderer.PdfBoxRendererBuilder;
import com.openhtmltopdf.extend.FailsafeResourceResolver;
import com.openhtmltopdf.extend.FSResource;
import com.openhtmltopdf.extend.ResourceResolver;
import com.openhtmltopdf.read.ReadContext;
*/
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final TemplateEngine templateEngine;

    /**
     * Thymeleaf 템플릿을 HTML 문자열로 렌더링합니다.
     */
    public String renderHtml(String templateName, Map<String, Object> variables) {
        Context context = new Context(Locale.KOREA, variables);
        // fragments/layout을 제외하고 템플릿 내용만 추출하여 PDF로 변환합니다.
        return templateEngine.process(templateName, context);
    }

    /**
     * HTML 문자열을 PDF 바이트 배열로 변환합니다. (기능 임시 비활성화)
     */
    public byte[] convertHtmlToPdf(String htmlContent) throws IOException {
        // PDF 라이브러리 오류로 인해 이 기능을 임시 비활성화합니다.
        throw new UnsupportedOperationException("PDF 다운로드 기능은 현재 라이브러리 문제로 인해 임시 비활성화되었습니다.");
    }

    // FontResourceResolver 클래스도 오류가 나므로 주석 처리합니다.
    /*
    private static class FontResourceResolver implements ResourceResolver {
        @Override
        public FSResource resolve(String uri) {
            try {
                // NanumGothic.ttf 파일을 resources/static/fonts/ 경로에서 로드한다고 가정
                String fontPath = "static/fonts/NanumGothic.ttf";
                InputStream is = getClass().getClassLoader().getResourceAsStream(fontPath);
                if (is != null) {
                    return new FSResource(is);
                }
            } catch (Exception e) {
                // 폰트 로드 실패 시 무시
            }
            return null;
        }

        @Override
        public FSResource resolve(String uri, ReadContext readContext) {
            return resolve(uri);
        }
    }
    */
}