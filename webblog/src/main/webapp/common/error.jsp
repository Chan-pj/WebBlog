<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // 예외 상세(메시지·스택 트레이스)는 화면에 노출하지 않고 서버 로그에만 남긴다.
    if (exception != null) {
        org.slf4j.LoggerFactory.getLogger("com.chan.error").error(
                "Unhandled exception: uri={}", request.getAttribute("javax.servlet.error.request_uri"), exception);
    }
    Integer status = (Integer) request.getAttribute("javax.servlet.error.status_code");
    pageContext.setAttribute("status", status != null ? status : 500);
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>오류 - webblog</title>
    <style>
        body { font-family: 'Noto Sans KR', sans-serif; background: #f7f4f1; color: #2e2e2e;
               margin: 0; padding: 80px 16px; text-align: center; }
        .box { max-width: 420px; margin: 0 auto; background: #fff; border: 1px solid #e8e2dc;
               border-radius: 8px; padding: 32px 24px; }
        .code { font-size: 32px; color: #9a9a9a; margin: 0 0 8px; }
        a { color: #4d84bc; }
    </style>
</head>
<body>
<div class="box">
    <p class="code">${status}</p>
    <c:choose>
        <c:when test="${status == 404}"><h2>페이지를 찾을 수 없습니다.</h2></c:when>
        <c:when test="${status == 403}"><h2>접근 권한이 없습니다.</h2></c:when>
        <c:otherwise><h2>요청을 처리하는 중 오류가 발생했습니다.</h2></c:otherwise>
    </c:choose>
    <p>잠시 후 다시 시도해 주세요.</p>
    <p><a href="${pageContext.request.contextPath}/">홈으로</a></p>
</div>
</body>
</html>
