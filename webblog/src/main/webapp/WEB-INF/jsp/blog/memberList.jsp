<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>블로그 멤버 목록</h2>
<p><a href="${pageContext.request.contextPath}/blog/${blogId}/view.do">블로그로 돌아가기</a></p>
<table style="border-collapse: collapse;">
    <tr>
        <th style="border: 1px solid #999; padding: 6px;">멤버 ID</th>
        <th style="border: 1px solid #999; padding: 6px;">사용자 ID</th>
        <th style="border: 1px solid #999; padding: 6px;">역할</th>
        <th style="border: 1px solid #999; padding: 6px;">상태</th>
        <th style="border: 1px solid #999; padding: 6px;">변경</th>
    </tr>
    <c:forEach var="member" items="${members}">
        <tr>
            <td style="border: 1px solid #999; padding: 6px;">${member.memberId}</td>
            <td style="border: 1px solid #999; padding: 6px;">${member.userId}</td>
            <td style="border: 1px solid #999; padding: 6px;">${member.memberRole}</td>
            <td style="border: 1px solid #999; padding: 6px;">${member.status}</td>
            <td style="border: 1px solid #999; padding: 6px;">
                <form action="${pageContext.request.contextPath}/blog/${blogId}/member/status.do" method="post">
                    <input type="hidden" name="memberId" value="${member.memberId}" />
                    <select name="status">
                        <option value="ACTIVE">ACTIVE</option>
                        <option value="PENDING">PENDING</option>
                        <option value="BLOCKED">BLOCKED</option>
                    </select>
                    <button type="submit">저장</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>