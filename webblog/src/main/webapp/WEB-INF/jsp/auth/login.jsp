<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>로그인</h2>
<p>${message}로그인하세요</p>
<c:if test="${param.error eq 'kakao_key_missing'}">
    <p>서버에 카카오 REST API 키가 설정되지 않았습니다.</p>
</c:if>
<c:if test="${param.error eq 'login_required'}">
    <p>관리자 페이지는 로그인 후 이용할 수 있습니다.</p>
</c:if>
<c:if test="${param.error eq 'admin_only'}">
    <p>관리자 권한이 필요한 페이지입니다.</p>
</c:if>
<form method="post" action="${pageContext.request.contextPath}/auth/login.do">
    이메일: <input type="text" name="email"/><br/>
    <input type="submit" value="로그인"/>
</form>
<p>
    <a href="${pageContext.request.contextPath}/auth/kakao/login.do">
        카카오로 시작하기
    </a><br>
    <a href="${pageContext.request.contextPath}/auth/register.do">
        회원가입
    </a>
</p>