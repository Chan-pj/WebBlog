<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>관리자 통합 검색</h2>
<p>게시글·회원·블로그·금지어 포함 게시글·위반 시도 로그를 검색합니다. 특수문자가 포함된 검색어도 그대로 조회합니다.</p>

<form method="get" action="${pageContext.request.contextPath}/admin/search.do" style="margin-bottom:16px;">
    <label>검색 영역</label>
    <select name="searchType">
        <option value="ALL" ${searchType == 'ALL' ? 'selected' : ''}>전체</option>
        <option value="POST" ${searchType == 'POST' ? 'selected' : ''}>게시글</option>
        <option value="USER" ${searchType == 'USER' ? 'selected' : ''}>회원</option>
        <option value="BLOG" ${searchType == 'BLOG' ? 'selected' : ''}>블로그</option>
        <option value="FORBIDDEN" ${searchType == 'FORBIDDEN' ? 'selected' : ''}>금지어 포함 게시글</option>
        <option value="VIOLATION" ${searchType == 'VIOLATION' ? 'selected' : ''}>위반 시도 로그</option>
    </select>
    <input type="text" name="keyword" value="<c:out value='${keyword}'/>" placeholder="검색어 (금지어·이메일·제목 등)" size="40"/>
    <button type="submit">검색</button>
</form>

<p>검색 결과: <strong>${resultCount}</strong>건</p>

<table style="border-collapse:collapse; width:100%; font-size:14px;">
    <tr style="background:#eee;">
        <th style="border:1px solid #999; padding:6px;">유형</th>
        <th style="border:1px solid #999; padding:6px;">블로그 사용자</th>
        <th style="border:1px solid #999; padding:6px;">블로그</th>
        <th style="border:1px solid #999; padding:6px;">요약</th>
        <th style="border:1px solid #999; padding:6px;">일시 / IP</th>
        <th style="border:1px solid #999; padding:6px;">바로가기</th>
    </tr>
    <c:forEach var="r" items="${results}">
        <tr>
            <td style="border:1px solid #999; padding:6px;"><c:out value="${r.resultType}"/></td>
            <td style="border:1px solid #999; padding:6px;">
                <c:out value="${r.userNickname}"/><br/>
                <small><c:out value="${r.userEmail}"/></small>
            </td>
            <td style="border:1px solid #999; padding:6px;">
                <c:if test="${not empty r.blogId}">
                    <a href="${pageContext.request.contextPath}/blog/${r.blogId}/view.do"><c:out value="${r.blogName}"/></a>
                </c:if>
            </td>
            <td style="border:1px solid #999; padding:6px;">
                <c:if test="${not empty r.title}"><strong><c:out value="${r.title}"/></strong><br/></c:if>
                <c:out value="${r.snippet}"/>
                <c:if test="${not empty r.matchedWord}"><br/><small>매칭: <c:out value="${r.matchedWord}"/></small></c:if>
            </td>
            <td style="border:1px solid #999; padding:6px;">
                <c:out value="${r.createdAt}"/><br/>
                <c:if test="${not empty r.writerIp}">IP: <c:out value="${r.writerIp}"/></c:if>
            </td>
            <td style="border:1px solid #999; padding:6px;">
                <c:if test="${not empty r.detailUrl}">
                    <a href="${pageContext.request.contextPath}${r.detailUrl}">이동</a>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty results}">
        <tr><td colspan="6" style="border:1px solid #999; padding:12px; text-align:center;">검색 결과가 없습니다.</td></tr>
    </c:if>
</table>

<p>
    <a href="${pageContext.request.contextPath}/admin/dashboard.do">대시보드</a> |
    <a href="${pageContext.request.contextPath}/admin/report.do">보고서</a>
</p>