package com.chan.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.chan.mapper.BlogMapper;
import com.chan.mapper.ModerationMapper;
import com.chan.mapper.UserMapper;
import com.chan.service.AdminSearchService;
import com.chan.service.ReportService;
import com.chan.vo.BlacklistWordVO;
import com.chan.vo.ModerationViolationVO;

@Service("reportService")
public class ReportServiceImpl implements ReportService {

    private static final String TEMPLATE_PATH = "template/word_template.docx";
    private static final String FONT_PATH = "fonts/NotoSansKR-Regular.ttf";

    @Resource
    private UserMapper userMapper;

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private ModerationMapper moderationMapper;

    @Resource
    private AdminSearchService adminSearchService;

    @Override
    public Map<String, String> buildReportData() {
        Map<String, String> data = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new Date());

        long totalUsers = userMapper.countAllUsers();
        int totalBlogs = blogMapper.countAllBlogs();
        int activeMembers = blogMapper.countActiveMembers();
        int violationTotal = moderationMapper.countViolations();
        int blockedWordCount = moderationMapper.countViolationsByType("BLOCKED_WORD");
        int personalInfoCount = moderationMapper.countViolationsByType("PERSONAL_INFO");

        List<BlacklistWordVO> blacklist = moderationMapper.selectAllBlacklistWords();
        int postsWithForbidden = adminSearchService.search(AdminSearchService.TYPE_FORBIDDEN, "", 500).size();

        data.put("${REPORT_TITLE}", "webblog 운영 보고서");
        data.put("${REPORT_DATE}", now);
        data.put("${TOTAL_USERS}", String.valueOf(totalUsers));
        data.put("${TOTAL_BLOGS}", String.valueOf(totalBlogs));
        data.put("${ACTIVE_MEMBERS}", String.valueOf(activeMembers));
        data.put("${BLACKLIST_WORD_COUNT}", String.valueOf(blacklist.size()));
        data.put("${POSTS_WITH_FORBIDDEN}", String.valueOf(postsWithForbidden));
        data.put("${VIOLATION_TOTAL}", String.valueOf(violationTotal));
        data.put("${VIOLATION_BLOCKED_WORD}", String.valueOf(blockedWordCount));
        data.put("${VIOLATION_PERSONAL_INFO}", String.valueOf(personalInfoCount));
        data.put("${MEMBER_SUMMARY}", buildMemberSummary(totalUsers, totalBlogs, activeMembers));
        data.put("${FORBIDDEN_STATS}", buildForbiddenStats(blacklist, postsWithForbidden, violationTotal, blockedWordCount, personalInfoCount));
        data.put("${VIOLATION_DETAIL}", buildViolationDetail(moderationMapper.selectViolationsForReport(100)));
        data.put("${GENERATED_BY}", "webblog Admin ReportService");
        return data;
    }

    @Override
    public byte[] generateWordReport() throws Exception {
        Map<String, String> data = buildReportData();
        ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH);
        if (resource.exists()) {
            try (InputStream in = resource.getInputStream();
                 XWPFDocument doc = new XWPFDocument(in);
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                replaceInDocument(doc, data);
                doc.write(out);
                return out.toByteArray();
            }
        }
        return buildFallbackWord(data);
    }

    @Override
    public byte[] generatePdfReport() throws Exception {
        Map<String, String> data = buildReportData();
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDType0Font font = loadPdfFont(document);
            float margin = 50;
            float y = page.getMediaBox().getHeight() - margin;
            float leading = 16;

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                y = writePdfLine(cs, font, 16, margin, y, data.get("${REPORT_TITLE}"));
                y -= 8;
                y = writePdfLine(cs, font, 11, margin, y, "작성일: " + data.get("${REPORT_DATE}"));
                y -= leading;
                for (String line : data.get("${MEMBER_SUMMARY}").split("\n")) {
                    y = writePdfLine(cs, font, 10, margin, y, line);
                    if (y < margin + 40) {
                        break;
                    }
                }
                y -= leading;
                y = writePdfLine(cs, font, 11, margin, y, "--- 금지어 통계 ---");
                for (String line : data.get("${FORBIDDEN_STATS}").split("\n")) {
                    y = writePdfLine(cs, font, 10, margin, y, line);
                    if (y < margin + 40) {
                        break;
                    }
                }
                y -= leading;
                y = writePdfLine(cs, font, 11, margin, y, "--- 위반 시도 상세 ---");
                for (String line : data.get("${VIOLATION_DETAIL}").split("\n")) {
                    y = writePdfLine(cs, font, 9, margin, y, line);
                    if (y < margin + 20) {
                        break;
                    }
                }
            }
            document.save(out);
            return out.toByteArray();
        }
    }

    private PDType0Font loadPdfFont(PDDocument document) throws IOException {
        ClassPathResource fontRes = new ClassPathResource(FONT_PATH);
        if (fontRes.exists()) {
            try (InputStream in = fontRes.getInputStream()) {
                return PDType0Font.load(document, in);
            }
        }
        File malgun = new File("C:/Windows/Fonts/malgun.ttf");
        if (malgun.isFile()) {
            return PDType0Font.load(document, malgun);
        }
        throw new IOException("PDF 한글 폰트가 없습니다. src/main/resources/" + FONT_PATH
                + " 를 추가하거나 Windows 맑은 고딕 폰트를 사용할 수 있어야 합니다.");
    }

    private float writePdfLine(PDPageContentStream cs, PDType0Font font, float size, float x, float y, String text)
            throws IOException {
        if (text == null) {
            text = "";
        }
        if (text.length() > 120) {
            text = text.substring(0, 120) + "...";
        }
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - (size + 4);
    }

    private byte[] buildFallbackWord(Map<String, String> data) throws IOException {
        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XWPFParagraph title = doc.createParagraph();
            XWPFRun run = title.createRun();
            run.setText(data.get("${REPORT_TITLE}") + " (템플릿 미배치)");
            run.setBold(true);
            for (Map.Entry<String, String> e : data.entrySet()) {
                if ("${REPORT_TITLE}".equals(e.getKey())) {
                    continue;
                }
                XWPFParagraph p = doc.createParagraph();
                p.createRun().setText(e.getKey() + " = " + e.getValue());
            }
            doc.write(out);
            return out.toByteArray();
        }
    }

    private void replaceInDocument(XWPFDocument doc, Map<String, String> replacements) {
        for (XWPFParagraph p : doc.getParagraphs()) {
            replaceInParagraph(p, replacements);
        }
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        replaceInParagraph(p, replacements);
                    }
                }
            }
        }
    }

    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> replacements) {
        String text = paragraph.getText();
        if (text == null || text.isEmpty()) {
            return;
        }
        String replaced = text;
        for (Map.Entry<String, String> e : replacements.entrySet()) {
            if (replaced.contains(e.getKey())) {
                replaced = replaced.replace(e.getKey(), e.getValue() != null ? e.getValue() : "");
            }
        }
        if (!replaced.equals(text)) {
            while (paragraph.getRuns().size() > 0) {
                paragraph.removeRun(0);
            }
            XWPFRun run = paragraph.createRun();
            run.setText(replaced);
        }
    }

    private String buildMemberSummary(long users, int blogs, int members) {
        StringBuilder sb = new StringBuilder();
        sb.append("전체 가입 회원 수: ").append(users).append("명\n");
        sb.append("개설된 블로그 수: ").append(blogs).append("개\n");
        sb.append("활성 블로그 멤버 수: ").append(members).append("명\n");
        return sb.toString();
    }

    private String buildForbiddenStats(List<BlacklistWordVO> blacklist, int postsHit, int vTotal, int vBlocked, int vPersonal) {
        StringBuilder sb = new StringBuilder();
        sb.append("등록된 금지어 수: ").append(blacklist.size()).append("개\n");
        sb.append("게시글 내 금지어 포함 건수(스캔): ").append(postsHit).append("건\n");
        sb.append("등록 차단 시도(전체): ").append(vTotal).append("건\n");
        sb.append("  - 금지어: ").append(vBlocked).append("건, 개인정보: ").append(vPersonal).append("건\n");
        return sb.toString();
    }

    private String buildViolationDetail(List<ModerationViolationVO> violations) {
        if (violations == null || violations.isEmpty()) {
            return "(기록된 위반 시도 없음)";
        }
        StringBuilder sb = new StringBuilder();
        int n = 0;
        for (ModerationViolationVO v : violations) {
            n++;
            sb.append(n).append(". ")
                    .append(v.getCreatedAt()).append(" | ")
                    .append(v.getUserNickname()).append("(").append(v.getUserEmail()).append(") | IP:")
                    .append(v.getWriterIp() != null ? v.getWriterIp() : "-").append(" | ")
                    .append(v.getViolationType());
            if (v.getMatchedWord() != null) {
                sb.append(" | 단어:").append(v.getMatchedWord());
            }
            if (v.getBlogName() != null) {
                sb.append(" | 블로그:").append(v.getBlogName());
            }
            sb.append("\n");
            if (v.getAttemptedTitle() != null) {
                sb.append("   제목: ").append(v.getAttemptedTitle()).append("\n");
            }
        }
        return sb.toString();
    }
}