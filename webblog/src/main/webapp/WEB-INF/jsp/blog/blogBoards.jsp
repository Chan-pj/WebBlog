<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>게시판 목록 — <c:out value="${blog.blogName}"/></h2>
<p><a href="${pageContext.request.contextPath}/blog/${blog.blogId}/view.do">블로그 정보</a></p>
<c:if test="${param.error eq 'no_board'}">
    <p style="color:#c00;">게시판이 없습니다. 관리자에게 문의하세요.</p>
</c:if>
<c:if test="${param.error eq 'board' or param.error eq 'board_del'}">
    <p style="color:#c00;">게시판 작업을 처리할 수 없습니다. 권한·개수 제한·게시글 유무를 확인하세요.</p>
</c:if>
<table class="wb-table">
    <thead>
    <tr>
        <th>게시판</th>
        <th>이동</th>
        <c:if test="${isOwner}"><th>삭제</th></c:if>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="bd" items="${boards}">
        <tr>
            <td><c:out value="${bd.boardName}"/></td>
            <td><a href="${pageContext.request.contextPath}/post/${blog.blogId}/board/${bd.boardId}/list.do">글 목록</a></td>
            <c:if test="${isOwner}">
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/blog/${blog.blogId}/board/delete.do" style="display:inline;"
                          onsubmit="return confirm('삭제할까요?');">
                        <input type="hidden" name="boardId" value="${bd.boardId}"/>
                        <button type="submit">삭제</button>
                    </form>
                </td>
            </c:if>
        </tr>
    </c:forEach>
    </tbody>
</table>
<c:if test="${isOwner}">
    <h3>게시판 추가</h3>
    <form method="post" action="${pageContext.request.contextPath}/blog/${blog.blogId}/board/add.do">
        이름: <input type="text" name="boardName" required maxlength="200" style="width:100%;max-width:360px;box-sizing:border-box;"/>
        <button type="submit">추가</button>
    </form>
    <p><small>게시판 개수는 슈퍼 관리자가 설정한 블로그의 상한(<c:out value="${blog.boardLimit}"/>) 이내이며, <strong>게시판 생성 권한</strong>이 있어야 합니다.</small></p>
</c:if>