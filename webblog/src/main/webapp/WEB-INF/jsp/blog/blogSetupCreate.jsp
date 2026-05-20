<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>블로그 개설</h2>
<c:if test="${not empty message}">
    <p style="color:#c00;"><c:out value="${message}"/></p>
</c:if>
<p>슈퍼 관리자가 블로그 생성 권한을 부여한 <strong>블로그 관리자</strong>만 개설할 수 있습니다.</p>
<form method="post" action="${pageContext.request.contextPath}/blog/setup/create.do">
    블로그 이름: <input type="text" name="blogName" placeholder="미입력 시 닉네임 기반으로 생성" style="width:100%;max-width:420px;box-sizing:border-box;"/><br/><br/>
    <button type="submit">개설</button>
</form>
<p><a href="${pageContext.request.contextPath}/blog/explore.do">공개 블로그 목록</a></p>