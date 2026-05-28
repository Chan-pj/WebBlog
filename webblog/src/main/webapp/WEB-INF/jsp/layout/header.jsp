<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div>
    <h1><a href="${pageContext.request.contextPath}/">webblog</a></h1>
    <c:if test="${not empty sessionScope.loginUser}">
        <p style="font-size: 12px; color: #9a9a9a; margin-top: 3px;">
            <strong style="color: #2e2e2e; font-weight: 500;">${sessionScope.loginUser.nickname}</strong>님 환영합니다
        </p>
    </c:if>
</div>
<span class="member-badge">회원 <c:out value="${memberCount}"/>명</span>