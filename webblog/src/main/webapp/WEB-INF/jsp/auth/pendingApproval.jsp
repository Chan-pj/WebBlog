<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>슈퍼 관리자 승인 대기</h2>
<p>
    계정(<strong><c:out value="${sessionScope.loginUser.email}"/></strong>)은 아직
    <strong>승인되지 않았거나</strong>, 승인 후에도 소유 블로그·멤버 블로그가 없어 대기 중입니다.
</p>
<p>
    슈퍼 관리자가 회원을 승인하면 서비스를 이용할 수 있습니다.
    블로그 관리자로 가입한 경우에는 <strong>블로그 생성 권한</strong>까지 부여받은 뒤
    메뉴의 <a href="${pageContext.request.contextPath}/blog/setup/create.do">블로그 개설</a>을 진행하세요.
</p>
<p>
    <a href="${pageContext.request.contextPath}/auth/logout.do">로그아웃</a>
</p>