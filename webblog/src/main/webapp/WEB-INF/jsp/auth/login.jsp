<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="center-box">
    <h2>로그인</h2>

    <c:if test="${not empty message}">
        <div class="alert"><c:out value="${message}"/></div>
    </c:if>
    <c:choose>
        <c:when test="${param.error eq 'login_required'}"><div class="alert">로그인이 필요합니다.</div></c:when>
        <c:when test="${param.error eq 'forbidden'}"><div class="alert">접근 권한이 없습니다.</div></c:when>
        <c:when test="${param.error eq 'admin_only'}"><div class="alert">슈퍼 관리자만 접근할 수 있습니다.</div></c:when>
        <c:when test="${param.error eq 'kakao_key_missing'}"><div class="alert">서버에 카카오 REST API 키가 설정되지 않았습니다.</div></c:when>
    </c:choose>

    <div style="margin-top: 16px; display: flex; flex-direction: column; gap: 8px;">
        <a href="${pageContext.request.contextPath}/auth/kakao/login.do" class="kakao-btn">카카오로 시작하기</a>
    </div>

    <p style="margin-top: 20px; font-size: 12px; color: #bbb; text-align: center;">
        처음 로그인하면 계정이 만들어지며, 슈퍼 관리자 승인 후 이용할 수 있습니다.
    </p>
</div>