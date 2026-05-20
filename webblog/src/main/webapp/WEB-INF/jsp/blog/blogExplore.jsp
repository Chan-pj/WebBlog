<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>공개 블로그 찾기</h2>
<p>멤버로 가입하려면 로그인 후 각 블로그에서 가입 신청을 하세요.</p>
<table class="wb-table">
    <thead>
    <tr>
        <th>ID</th>
        <th>이름</th>
        <th>이동</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="b" items="${blogs}">
        <tr>
            <td>${b.blogId}</td>
            <td><c:out value="${b.blogName}"/></td>
            <td><a href="${pageContext.request.contextPath}/blog/${b.blogId}/view.do">보기</a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>