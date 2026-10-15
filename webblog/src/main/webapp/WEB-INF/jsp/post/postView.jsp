<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2><c:out value="${post.title}"/></h2>
<p>
    <a href="${pageContext.request.contextPath}/post/${post.blogId}/board/${post.boardId}/list.do">목록</a>
</p>
<c:if test="${param.error eq 'validation'}">
    <p style="color:#c00;">금지어·개인정보 정책을 확인해 주세요.</p>
</c:if>
<c:if test="${param.error eq 'forbidden'}">
    <p style="color:#c00;">권한이 없습니다.</p>
</c:if>
<div style="white-space:pre-wrap;border:1px solid #eee;padding:12px;"><c:out value="${post.content}"/></div>

<c:if test="${canManagePost}">
    <h3>수정</h3>
    <form method="post" action="${pageContext.request.contextPath}/post/update.do">
        <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"/>
        <input type="hidden" name="postId" value="${post.postId}"/>
        제목: <input type="text" name="title" value="<c:out value='${post.title}'/>" style="width:100%;max-width:520px;box-sizing:border-box;"/><br/><br/>
        내용:<br/>
        <textarea name="content" rows="8" style="width:100%;box-sizing:border-box;"><c:out value="${post.content}"/></textarea><br/><br/>
        <button type="submit">저장</button>
    </form>
    <form method="post" action="${pageContext.request.contextPath}/post/delete.do" style="margin-top:12px;" onsubmit="return confirm('삭제할까요?');">
        <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"/>
        <input type="hidden" name="postId" value="${post.postId}"/>
        <input type="hidden" name="blogId" value="${post.blogId}"/>
        <input type="hidden" name="boardId" value="${post.boardId}"/>
        <button type="submit">삭제</button>
    </form>
</c:if>

<c:if test="${not empty replies}">
    <h3>답글 (블로그 관리자)</h3>
    <c:forEach var="r" items="${replies}">
        <div style="border-left:3px solid #369;margin:10px 0;padding:8px 12px;background:#f9f9f9;">
            <strong><c:out value="${r.title}"/></strong>
            <div style="white-space:pre-wrap;"><c:out value="${r.content}"/></div>
        </div>
    </c:forEach>
</c:if>

<c:if test="${canReply}">
    <h3>답글 작성 (관리자 전용)</h3>
    <form method="post" action="${pageContext.request.contextPath}/post/${post.blogId}/board/${post.boardId}/reply.do">
        <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"/>
        <input type="hidden" name="parentPostId" value="${post.postId}"/>
        제목: <input type="text" name="title" placeholder="답글" style="width:100%;max-width:520px;box-sizing:border-box;"/><br/><br/>
        내용:<br/>
        <textarea name="content" rows="6" style="width:100%;box-sizing:border-box;" required></textarea><br/><br/>
        <button type="submit">답글 등록</button>
    </form>
</c:if>