<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
.bb-nav { display:flex; gap:8px; margin-bottom:1.25rem; }
.bb-nav a {
    font-size:13px; color:#185fa5; text-decoration:none;
    padding:5px 10px; border:0.5px solid #b5d4f4;
    border-radius:8px; background:#e6f1fb;
}
.bb-alert {
    padding:10px 14px; border-radius:8px; font-size:13px;
    margin-bottom:16px; background:#fcebeb; color:#791f1f;
    border:0.5px solid #f7c1c1;
}
.bb-add-row { display:flex; gap:8px; margin-bottom:1.25rem; }
.bb-add-row input {
    flex:1; font-size:13px; padding:7px 12px;
    border:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    border-radius:8px;
    background:var(--color-background-primary, #fff);
    color:var(--color-text-primary, #1a1a1a);
}
.bb-add-row button {
    font-size:13px; padding:7px 16px; border-radius:8px;
    background:#185fa5; color:#fff; border:none; cursor:pointer;
}
.bb-table { width:100%; border-collapse:collapse; font-size:13px; }
.bb-table th {
    text-align:left; font-size:12px; font-weight:500;
    color:var(--color-text-secondary, #666); padding:0 0 8px;
    border-bottom:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
}
.bb-table td {
    padding:12px 0;
    border-bottom:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    color:var(--color-text-primary, #1a1a1a);
}
.bb-table tr:last-child td { border-bottom:none; }
.bb-table .board-link {
    color:var(--color-text-primary, #1a1a1a);
    text-decoration:none; font-size:14px;
}
.bb-table .board-link:hover { color:#185fa5; }
.bb-del-btn {
    font-size:12px; padding:4px 10px; border-radius:6px;
    border:0.5px solid #f7c1c1; background:#fcebeb;
    color:#791f1f; cursor:pointer;
}
.bb-hint {
    font-size:12px; color:var(--color-text-tertiary, #aaa); margin-top:8px;
}
</style>

<p style="font-size:16px; font-weight:500; color:var(--color-text-primary); margin-bottom:4px;">
    게시판 목록 — <c:out value="${blog.blogName}"/>
</p>

<div class="bb-nav">
    <a href="${pageContext.request.contextPath}/blog/${blog.blogId}/view.do">블로그 정보</a>
</div>

<c:if test="${param.error eq 'no_board'}">
    <div class="bb-alert">게시판이 없습니다. 관리자에게 문의하세요.</div>
</c:if>
<c:if test="${param.error eq 'board' or param.error eq 'board_del'}">
    <div class="bb-alert">게시판 작업을 처리할 수 없습니다. 권한·개수 제한·게시글 유무를 확인하세요.</div>
</c:if>

<c:if test="${isOwner}">
    <form method="post"
          action="${pageContext.request.contextPath}/blog/${blog.blogId}/board/add.do">
        <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"/>
        <div class="bb-add-row">
            <input type="text" name="boardName" required maxlength="200" placeholder="새 게시판 이름"/>
            <button type="submit">추가</button>
        </div>
    </form>
    <p class="bb-hint">
        게시판 개수는 블로그 상한(<c:out value="${blog.boardLimit}"/>) 이내이며,
        <strong>게시판 생성 권한</strong>이 있어야 합니다.
    </p>
</c:if>

<table class="bb-table">
    <thead>
        <tr>
            <th>게시판</th>
            <c:if test="${isOwner}"><th>삭제</th></c:if>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="bd" items="${boards}">
            <tr>
                <td>
                    <a href="${pageContext.request.contextPath}/post/${blog.blogId}/board/${bd.boardId}/list.do"
                       class="board-link">
                        <c:out value="${bd.boardName}"/>
                    </a>
                </td>
                <c:if test="${isOwner}">
                    <td>
                        <form method="post"
                              action="${pageContext.request.contextPath}/blog/${blog.blogId}/board/delete.do"
                              style="display:inline;"
                              onsubmit="return confirm('삭제할까요?');">
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"/>
                            <input type="hidden" name="boardId" value="${bd.boardId}"/>
                            <button type="submit" class="bb-del-btn">삭제</button>
                        </form>
                    </td>
                </c:if>
            </tr>
        </c:forEach>
    </tbody>
</table>