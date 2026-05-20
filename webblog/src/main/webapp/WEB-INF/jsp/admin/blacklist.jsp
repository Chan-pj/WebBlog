<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>전역 금지어(블랙리스트)</h2>
<p><a href="${pageContext.request.contextPath}/admin/dashboard.do">대시보드</a></p>
<c:if test="${param.error eq '1'}">
    <p style="color:#c00;">입력값을 확인해 주세요.</p>
</c:if>
<form method="post" action="${pageContext.request.contextPath}/admin/blacklist/add.do">
    단어: <input type="text" name="word" required maxlength="200"/>
    <button type="submit">추가</button>
</form>
<table class="wb-table" style="margin-top:12px;">
    <thead><tr><th>ID</th><th>단어</th><th>삭제</th></tr></thead>
    <tbody>
    <c:forEach var="w" items="${words}">
        <tr>
            <td>${w.blacklistId}</td>
            <td><c:out value="${w.word}"/></td>
            <td>
                <form method="post" action="${pageContext.request.contextPath}/admin/blacklist/delete.do" style="display:inline;">
                    <input type="hidden" name="blacklistId" value="${w.blacklistId}"/>
                    <button type="submit">삭제</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>