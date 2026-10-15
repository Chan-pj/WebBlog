<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<ul>
    <li><a href="${pageContext.request.contextPath}/">홈</a></li>
    <li><a href="${pageContext.request.contextPath}/blog/explore.do">블로그 찾기</a></li>
    <c:if test="${not empty sessionScope.loginUser}">
        <c:if test="${sessionScope.loginUser.approved and sessionScope.loginUser.canCreateBlog}">
            <li><a href="${pageContext.request.contextPath}/blog/setup/create.do">블로그 개설</a></li>
        </c:if>
        <c:if test="${sessionScope.loginUser.role eq 'SUPER_ADMIN'}">
            <li><a href="${pageContext.request.contextPath}/admin/dashboard.do">관리자</a></li>
        </c:if>
        <li><a href="${pageContext.request.contextPath}/auth/logout.do">로그아웃</a></li>
    </c:if>
    <c:if test="${empty sessionScope.loginUser}">
        <li><a href="${pageContext.request.contextPath}/auth/login.do">로그인</a></li>
    </c:if>
</ul>