<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>게시글 — <c:out value="${board.boardName}"/></h2>
<p>
    <a href="${pageContext.request.contextPath}/blog/${blogId}/boards.do">게시판 목록</a> |
    <a href="${pageContext.request.contextPath}/blog/${blogId}/view.do">블로그 정보</a>
</p>
<c:if test="${param.error eq 'write_not_allowed'}">
    <p style="color: #c00;">해당 블로그의 활성 멤버 또는 블로그 소유자만 글을 작성할 수 있습니다.</p>
</c:if>
<c:if test="${param.error eq 'validation'}">
    <p style="color: #c00;">금지어·개인정보 정책 또는 답글 규칙을 위반했습니다.</p>
</c:if>
<c:choose>
    <c:when test="${canWrite}">
        <p><a href="${pageContext.request.contextPath}/post/${blogId}/board/${boardId}/write.do">글쓰기</a> (멤버는 원글만 작성, 답글은 블로그 관리자만 가능)</p>
    </c:when>
    <c:otherwise>
        <p>읽기 전용(글쓰기 권한 없음)</p>
    </c:otherwise>
</c:choose>
<ul style="list-style:none;padding:0;">
    <c:forEach var="post" items="${posts}">
        <li style="border-bottom:1px solid #ddd;padding:10px 0;">
            <a href="${pageContext.request.contextPath}/post/view/${post.postId}.do"><c:out value="${post.title}"/></a>
        </li>
    </c:forEach>
</ul>