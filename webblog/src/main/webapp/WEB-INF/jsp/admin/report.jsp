<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>운영 보고서</h2>
<p>Word 템플릿(<code>src/main/resources/template/word_template.docx</code>)의 플레이스홀더를 채워 제출용 문서를 생성합니다. PDF는 동일 데이터로 다운로드합니다.</p>

<h3>미리보기</h3>
<pre style="background:#f8f8f8; padding:12px; white-space:pre-wrap; font-size:13px;"><c:forEach var="entry" items="${reportPreview}"><c:out value="${entry.key}"/>: <c:out value="${entry.value}"/>
</c:forEach></pre>

<p>
    <a href="${pageContext.request.contextPath}/admin/report/word.do" class="btn" style="display:inline-block; padding:8px 16px; background:#2563eb; color:#fff; text-decoration:none; border-radius:4px;">Word 보고서 다운로드 (.docx)</a>
    &nbsp;
    <a href="${pageContext.request.contextPath}/admin/report/pdf.do" style="display:inline-block; padding:8px 16px; background:#dc2626; color:#fff; text-decoration:none; border-radius:4px;">PDF 보고서 다운로드</a>
</p>
<p><small>PDF 한글 출력을 위해 <code>src/main/resources/fonts/NotoSansKR-Regular.ttf</code> 가 필요합니다.</small></p>

<p>
    <a href="${pageContext.request.contextPath}/admin/search.do">통합 검색</a> |
    <a href="${pageContext.request.contextPath}/admin/dashboard.do">대시보드</a>
</p>