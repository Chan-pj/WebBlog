<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>게시글 작성 — <c:out value="${board.boardName}"/></h2>
<p><a href="${pageContext.request.contextPath}/post/${blogId}/board/${boardId}/list.do">목록</a></p>
<form method="post" action="${pageContext.request.contextPath}/post/${blogId}/board/${boardId}/write.do">
    제목: <input type="text" name="title" required style="width:100%;max-width:520px;box-sizing:border-box;"/><br/><br/>
    내용:<br/>
    <textarea name="content" rows="12" style="width:100%;max-width:100%;box-sizing:border-box;" required></textarea><br/><br/>
    <input type="submit" value="등록"/>
</form>