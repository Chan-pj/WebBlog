<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:choose>
<c:when test="${not empty blog}">

    <c:if test="${param.joinOk eq '1'}">
        <div class="alert" style="background:#e8f4ee; color:#2a6040; border-color:#a8d4b8;">멤버 가입이 완료되었습니다.</div>
    </c:if>
    <c:if test="${param.joinError eq '1'}">
        <div class="alert" style="background:#fde8e8; color:#904040; border-color:#f0b8b8;">멤버 가입을 처리할 수 없습니다.</div>
    </c:if>
    <c:if test="${param.error eq 'forbidden'}">
        <div class="alert" style="background:#fde8e8; color:#904040; border-color:#f0b8b8;">접근 권한이 없습니다.</div>
    </c:if>

    <div style="display:flex; align-items:baseline; justify-content:space-between; margin-bottom:6px;">
        <h2 style="border:none; padding:0; margin:0; font-size:20px;">
            <c:out value="${blog.blogName}"/>
        </h2>
        <span style="font-size:12px; padding:3px 10px; border-radius:20px;
            background:${blog.isPublic ? '#e8f4ee' : '#f0ebe5'};
            color:${blog.isPublic ? '#2a6040' : '#888'};">
            ${blog.isPublic ? '공개' : '비공개'}
        </span>
    </div>

    <p style="font-size:12px; color:#bbb; margin-bottom:24px;">블로그 #${blog.blogId}</p>

    <div style="display:flex; align-items:center; gap:16px; padding-top:16px; border-top:1px solid #f0ebe5; flex-wrap:wrap;">
        <a href="${pageContext.request.contextPath}/blog/${blog.blogId}/boards.do"
           style="font-size:13px; color:#6b9fd4;">게시판 보기</a>

        <c:if test="${isOwner}">
            <a href="${pageContext.request.contextPath}/blog/${blog.blogId}/members.do"
               style="font-size:13px; color:#9a9a9a;">멤버 관리</a>
            <a href="${pageContext.request.contextPath}/blog/${blog.blogId}/blacklist.do"
               style="font-size:13px; color:#9a9a9a;">금지어 관리</a>
        </c:if>

        <c:if test="${canJoinAsMember}">
            <form method="post" action="${pageContext.request.contextPath}/blog/${blog.blogId}/join.do" style="margin:0;">
                <button type="submit" class="btn btn-sm btn-primary">멤버 가입</button>
            </form>
        </c:if>
    </div>

</c:when>
<c:otherwise>
    <p style="color:#bbb; font-size:13px;">블로그 정보를 찾을 수 없습니다.</p>
</c:otherwise>
</c:choose>