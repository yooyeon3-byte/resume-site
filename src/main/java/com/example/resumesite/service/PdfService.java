package com.example.resumesite.service;

import com.openhtmltopdf.pdfbox.PdfBoxRenderer;
import com.openhtmltopdf.pdfbox.PdfBoxRenderer.PdfBoxRendererBuilder;
import com.openhtmltopdf.extend.FailsafeResourceResolver;
import com.openhtmltopdf.extend.FSResource;
import com.openhtmltopdf.extend.ResourceResolver;
import com.openhtmltopdf.read.ReadContext;
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
     * HTML 문자열을 PDF 바이트 배열로 변환합니다.
     */
    public byte[] convertHtmlToPdf(String htmlContent) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        PdfBoxRendererBuilder builder = PdfBoxRenderer.builder()
                .withWkhtmltopdf(false)
                .defaultResourceResolver(new FailsafeResourceResolver())
                // 폰트 리소스 로더 추가 (한글 깨짐 방지)
                .defaultResourceResolver(new FontResourceResolver());

        PdfBoxRenderer renderer = builder.build();

        renderer.setHtmlContent(htmlContent, "/");
        renderer.layout();
        renderer.create(os);

        return os.toByteArray();
    }

    // 헬퍼 클래스: OpenHTMLToPDF가 한글 폰트를 로드할 수 있도록 합니다.
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
}