<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>블로그 정보</h2>
<c:choose>
    <c:when test="${not empty blog}">
        <c:if test="${param.joinOk eq '1'}"><p style="color:green;">멤버 가입이 완료되었습니다.</p></c:if>
        <c:if test="${param.joinError eq '1'}"><p style="color:#c00;">멤버 가입을 처리할 수 없습니다.</p></c:if>
        <c:if test="${param.error eq 'forbidden'}"><p style="color:#c00;">접근 권한이 없습니다.</p></c:if>
        <p>블로그 ID: ${blog.blogId}</p>
        <p>블로그 이름: <c:out value="${blog.blogName}"/></p>
        <p>공개 여부: ${blog.isPublic}</p>
        <p>
            <a href="${pageContext.request.contextPath}/blog/${blog.blogId}/boards.do">게시판(응답형 목록)</a> |
            <c:if test="${isOwner}">
                <a href="${pageContext.request.contextPath}/blog/${blog.blogId}/members.do">멤버 관리</a> |
                <a href="${pageContext.request.contextPath}/blog/${blog.blogId}/blacklist.do">금지어(블랙리스트)</a>
            </c:if>
        </p>
        <c:if test="${canJoinAsMember}">
            <form method="post" action="${pageContext.request.contextPath}/blog/${blog.blogId}/join.do" style="margin-top:8px;">
                <button type="submit">이 블로그에 멤버로 가입</button>
            </form>
        </c:if>
    </c:when>
    <c:otherwise>
        <p>블로그 정보를 찾을 수 없습니다.</p>
    </c:otherwise>
</c:choose>