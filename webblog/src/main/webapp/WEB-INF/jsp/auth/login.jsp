<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="center-box">
    <h2>로그인</h2>

    <c:if test="${not empty message}">
        <div class="alert">${message}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/auth/login.do">
        <div style="margin-bottom: 12px;">
            <label>이메일</label>
            <input type="email" name="email" placeholder="example@email.com" autofocus/>
        </div>
        <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 4px;">로그인</button>
    </form>

    <div style="margin-top: 16px; display: flex; flex-direction: column; gap: 8px;">
        <a href="${pageContext.request.contextPath}/auth/kakao/login.do" class="kakao-btn">카카오로 시작하기</a>
    </div>

    <p style="margin-top: 20px; font-size: 12px; color: #bbb; text-align: center;">
        계정이 없으신가요?
        <a href="${pageContext.request.contextPath}/auth/register.do" style="color: #6b9fd4;">회원가입</a>
    </p>
</div>