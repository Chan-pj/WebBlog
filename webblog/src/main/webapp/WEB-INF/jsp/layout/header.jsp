<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h1><a href="${pageContext.request.contextPath}/">webblog 블로그 프로젝트</a></h1>
<span class="member-badge">현재 회원가입 수: <strong><c:out value="${memberCount}"/></strong>명</span>