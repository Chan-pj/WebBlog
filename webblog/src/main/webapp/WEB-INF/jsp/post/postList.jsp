<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui" %>

<style>
.pl-nav { display:flex; gap:8px; margin-bottom:1.25rem; }
.pl-nav a {
    font-size:13px; color:#185fa5; text-decoration:none;
    padding:5px 10px; border:0.5px solid #b5d4f4;
    border-radius:8px; background:#e6f1fb;
}
.pl-alert {
    padding:10px 14px; border-radius:8px; font-size:13px;
    margin-bottom:16px; background:#fcebeb; color:#791f1f;
    border:0.5px solid #f7c1c1;
}
.pl-topbar {
    display:flex; align-items:center; justify-content:space-between;
    margin-bottom:1rem;
}
.pl-write-btn {
    font-size:13px; font-weight:500; padding:6px 14px;
    border-radius:8px; background:#185fa5; color:#fff;
    text-decoration:none;
}
.pl-write-btn:hover { background:#0c447c; }
.pl-readonly {
    font-size:13px; color:var(--color-text-tertiary, #aaa);
    padding:10px 0;
}
.pl-table { width:100%; border-collapse:collapse; }
.pl-table thead tr {
    border-bottom:1px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
}
.pl-table th {
    font-size:12px; font-weight:500;
    color:var(--color-text-secondary, #666);
    padding:8px 12px; text-align:left;
}
.pl-table th.center { text-align:center; }
.pl-table tbody tr {
    border-bottom:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    transition:background 0.15s;
}
.pl-table tbody tr:last-child { border-bottom:none; }
.pl-table tbody tr:hover {
    background:var(--color-background-secondary, #f5f5f0);
}
.pl-table td {
    padding:12px; font-size:13px;
    color:var(--color-text-primary, #1a1a1a);
    vertical-align:middle;
}
.pl-table td.num {
    width:52px; text-align:center;
    color:var(--color-text-tertiary, #aaa); font-size:12px;
}
.pl-table td.title a {
    color:var(--color-text-primary, #1a1a1a);
    text-decoration:none; font-size:14px;
}
.pl-table td.title a:hover { color:#185fa5; }
.pl-table td.writer {
    width:80px; text-align:center;
    color:var(--color-text-secondary, #666); font-size:12px;
}
.pl-table td.reply-col {
    width:60px; text-align:center;
}
.reply-badge {
    font-size:11px; padding:2px 7px; border-radius:20px;
    background:#e6f1fb; color:#185fa5;
}
.pl-hint {
    font-size:12px; color:var(--color-text-tertiary, #aaa);
    margin-top:1rem;
}
</style>

<p style="font-size:16px; font-weight:500; color:var(--color-text-primary); margin-bottom:4px;">
    게시글 — <c:out value="${board.boardName}"/>
</p>

<div class="pl-nav">
    <a href="${pageContext.request.contextPath}/blog/${blogId}/boards.do">게시판 목록</a>
    <a href="${pageContext.request.contextPath}/blog/${blogId}/view.do">블로그 정보</a>
</div>

<c:if test="${param.error eq 'write_not_allowed'}">
    <div class="pl-alert">해당 블로그의 활성 멤버 또는 블로그 소유자만 글을 작성할 수 있습니다.</div>
</c:if>
<c:if test="${param.error eq 'validation'}">
    <div class="pl-alert">금지어·개인정보 정책 또는 답글 규칙을 위반했습니다.</div>
</c:if>

<div class="pl-topbar">
    <span style="font-size:13px; color:var(--color-text-secondary, #666);">
        전체 <strong>${posts.size()}</strong>건
    </span>
    <c:if test="${canWrite}">
        <a href="${pageContext.request.contextPath}/post/${blogId}/board/${boardId}/write.do"
           class="pl-write-btn">글쓰기</a>
    </c:if>
</div>

<table class="pl-table">
    <thead>
        <tr>
            <th class="center">번호</th>
            <th>제목</th>
            <th class="center">작성자</th>
            <th class="center">구분</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="post" items="${posts}">
            <tr>
                <td class="num">${post.postId}</td>
                <td class="title">
                    <a href="${pageContext.request.contextPath}/post/view/${post.postId}.do">
                        <c:out value="${post.title}"/>
                    </a>
                </td>
                <td class="writer"><c:out value="${post.writerNickname}"/></td>
                <td class="reply-col">
                    <c:if test="${not empty post.parentPostId}">
                        <span class="reply-badge">답글</span>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<c:if test="${paginationInfo.totalPageCount > 1}">
    <div class="wb-paging"><ui:pagination paginationInfo="${paginationInfo}" type="image" jsFunction="goPage"/></div>
</c:if>

<c:if test="${not canWrite}">
    <p class="pl-hint">읽기 전용 — 글쓰기 권한이 없습니다.</p>
</c:if>