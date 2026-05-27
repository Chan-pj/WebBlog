<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<ul>
    <li><a href="${pageContext.request.contextPath}/">홈</a></li>
    <c:choose>
        <c:when test="${not empty sessionScope.loginUser}">
            <li><span>${sessionScope.loginUser.nickname}님</span></li>
            <li><a href="${pageContext.request.contextPath}/blog/explore.do">블로그 찾기</a></li>
            <c:if test="${sessionScope.loginUser.approved and sessionScope.loginUser.canCreateBlog and sessionScope.loginUser.role eq 'BLOG_ADMIN'}">
                <li><a href="${pageContext.request.contextPath}/blog/setup/create.do">블로그 개설</a></li>
            </c:if>
            <li><a href="${pageContext.request.contextPath}/auth/logout.do">로그아웃</a></li>
        </c:when>
        <c:otherwise>
            <li><a href="${pageContext.request.contextPath}/auth/login.do">로그인</a></li>
            <li><a href="${pageContext.request.contextPath}/auth/register.do">회원가입</a></li>
        </c:otherwise>
    </c:choose>
    <c:if test="${not empty sessionScope.loginUser and sessionScope.loginUser.role eq 'SUPER_ADMIN'}">
        <li><a href="${pageContext.request.contextPath}/admin/dashboard.do">슈퍼관리자</a></li>
    </c:if>
</ul>