package com.example.resumesite.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocxService {

    private final TemplateEngine templateEngine;

    /**
     * Thymeleaf 템플릿을 HTML 문자열로 렌더링합니다.
     */
    public String renderHtml(String templateName, Map<String, Object> variables) {
        Context context = new Context(Locale.KOREA, variables);
        // fragments/layout을 제외하고 템플릿 내용만 추출하여 DOCX로 변환합니다.
        return templateEngine.process(templateName, context);
    }

    /**
     * HTML 문자열을 DOCX (Word 파일) 바이트 배열로 변환합니다.
     */
    public byte[] convertHtmlToDocx(String htmlContent, String imageBaseUri) throws Exception { // ⭐ imageBaseUri 파라미터 추가
        // 1. DOCX 패키지 생성
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

        // 2. HTML 임포터 설정 (Docx4j의 HTML to DOCX 변환기)
        XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);

        // 3. HTML을 DOCX 문서 요소로 변환하여 추가
        documentPart.getContent().addAll(
                // ⭐ 수정: imageBaseUri를 두 번째 인수로 전달합니다.
                xhtmlImporter.convert(htmlContent, imageBaseUri)
        );

        // 4. DOCX를 바이트 스트림으로 변환
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        wordMLPackage.save(os);

        return os.toByteArray();
    }
}