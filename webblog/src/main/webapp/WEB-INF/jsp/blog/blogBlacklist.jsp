<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>블로그 금지어 — <c:out value="${blog.blogName}"/></h2>
<p><a href="${pageContext.request.contextPath}/blog/${blog.blogId}/view.do">블로그로</a></p>
<c:if test="${param.error eq 'word'}">
    <p style="color:#c00;">금지어를 확인해 주세요.</p>
</c:if>
<p>전역 금지어에 더해, 이 블로그에만 적용되는 단어를 등록할 수 있습니다.</p>
<form method="post" action="${pageContext.request.contextPath}/blog/${blog.blogId}/blacklist/add.do">
    단어: <input type="text" name="word" required maxlength="200"/>
    <button type="submit">추가</button>
</form>
<table class="wb-table" style="margin-top:12px;">
    <thead><tr><th>단어</th><th>삭제</th></tr></thead>
    <tbody>
    <c:forEach var="w" items="${words}">
        <tr>
            <td><c:out value="${w.word}"/></td>
            <td>
                <form method="post" action="${pageContext.request.contextPath}/blog/${blog.blogId}/blacklist/delete.do" style="display:inline;">
                    <input type="hidden" name="blacklistId" value="${w.blacklistId}"/>
                    <button type="submit">삭제</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>