package com.example.resumesite.service;

import com.openhtmltopdf.pdfbox.PdfBoxRenderer;
import com.openhtmltopdf.pdfbox.PdfBoxRenderer.PdfBoxRendererBuilder;
import com.openhtmltopdf.extend.FailsafeResourceResolver;
import com.openhtmltopdf.extend.FSResource; // ⭐ 추가
import com.openhtmltopdf.extend.ResourceResolver; // ⭐ 추가
import com.openhtmltopdf.read.ReadContext; // ⭐ 추가
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
     * @param templateName 템플릿 경로 (예: "admin/resume-detail")
     * @param variables 템플릿에 전달할 모델 데이터
     * @return 렌더링된 HTML 문자열
     */
    public String renderHtml(String templateName, Map<String, Object> variables) {
        Context context = new Context(Locale.KOREA, variables);
        // ⭐ PDF 변환 시 Thymeleaf 레이아웃(fragments/layout)을 제외하기 위해 템플릿 이름만 사용합니다.
        //    (admin/resume-detail.html 템플릿의 내용만 추출하여 PDF로 변환)
        return templateEngine.process(templateName, context);
    }

    /**
     * HTML 문자열을 PDF 바이트 배열로 변환합니다.
     * @param htmlContent 렌더링된 HTML 문자열
     * @return PDF 파일의 바이트 배열
     * @throws IOException
     */
    public byte[] convertHtmlToPdf(String htmlContent) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        PdfBoxRendererBuilder builder = PdfBoxRenderer.builder()
                .withWkhtmltopdf(false)
                .defaultResourceResolver(new FailsafeResourceResolver())
                // ⭐ 폰트 리소스 로더 추가 (한글 깨짐 방지)
                .defaultResourceResolver(new FontResourceResolver());

        PdfBoxRenderer renderer = builder.build();

        renderer.setHtmlContent(htmlContent, "/");
        renderer.layout();
        renderer.create(os);

        return os.toByteArray();
    }

    // ⭐ 헬퍼 클래스: OpenHTMLToPDF가 한글 폰트를 로드할 수 있도록 합니다.
    //    주의: 실제 운영 환경에서는 /src/main/resources/static/fonts/NanumGothic.ttf 파일이 필요합니다.
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
                // 폰트 로드 실패 시 무시하고 진행
            }
            return null;
        }

        @Override
        public FSResource resolve(String uri, ReadContext readContext) {
            return resolve(uri);
        }
    }
}