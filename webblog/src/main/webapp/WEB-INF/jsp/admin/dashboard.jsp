<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>관리자 대시보드</h2>
<p>
    <a href="${pageContext.request.contextPath}/admin/blacklist.do">전역 금지어(블랙리스트) 관리</a>
    ·
    <a href="${pageContext.request.contextPath}/admin/blogs.do">블로그 설정</a>
</p>

<h3>회원 목록</h3>
<table style="border-collapse: collapse; width: 100%;">
    <tr>
        <th style="border: 1px solid #999; padding: 6px;">ID</th>
        <th style="border: 1px solid #999; padding: 6px;">이메일</th>
        <th style="border: 1px solid #999; padding: 6px;">닉네임</th>
        <th style="border: 1px solid #999; padding: 6px;">역할</th>
        <th style="border: 1px solid #999; padding: 6px;">현재 상태</th>
        <th style="border: 1px solid #999; padding: 6px;">설정</th>
    </tr>
    <c:forEach var="user" items="${users}">
        <tr>
            <td style="border: 1px solid #999; padding: 6px;">${user.userId}</td>
            <td style="border: 1px solid #999; padding: 6px;">${user.email}</td>
            <td style="border: 1px solid #999; padding: 6px;">${user.nickname}</td>
            <td style="border: 1px solid #999; padding: 6px;">${user.role}</td>
            <td style="border: 1px solid #999; padding: 6px;">
                승인:${user.approved}<br/>
                블로그생성:${user.canCreateBlog}<br/>
                게시판생성:${user.canCreateBoard}<br/>
                게시판쿼터:${user.boardCreateQuota}<br/>
                이미지개수:${user.imageUploadLimitCount}<br/>
                이미지크기:${user.imageSizeLimitBytes}<br/>
                관리차단:${user.managementBlocked}<br/>
                프로파일제한:${user.profileRestricted}
            </td>
            <td style="border: 1px solid #999; padding: 6px;">
                <c:if test="${user.role ne 'SUPER_ADMIN'}">
                    <form method="post" action="${pageContext.request.contextPath}/admin/user/approve.do" style="margin-bottom: 8px;">
                        <input type="hidden" name="userId" value="${user.userId}"/>
                        승인 <input type="checkbox" name="approved" value="true" <c:if test="${user.approved}">checked</c:if> /><br/>
                        블로그 생성권한 <input type="checkbox" name="canCreateBlog" value="true" <c:if test="${user.canCreateBlog}">checked</c:if> /><br/>
                        게시판 생성권한 <input type="checkbox" name="canCreateBoard" value="true" <c:if test="${user.canCreateBoard}">checked</c:if> /><br/>
                        게시판 쿼터 <input type="number" name="boardCreateQuota" value="${user.boardCreateQuota}" min="0" style="width: 80px;"/><br/>
                        이미지 개수 <input type="number" name="imageUploadLimitCount" value="${user.imageUploadLimitCount}" min="0" style="width: 80px;"/><br/>
                        이미지 크기(Byte) <input type="number" name="imageSizeLimitBytes" value="${user.imageSizeLimitBytes}" min="0" style="width: 120px;"/><br/>
                        <button type="submit">권한 저장</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/admin/user/protection.do">
                        <input type="hidden" name="userId" value="${user.userId}"/>
                        관리 차단 <input type="checkbox" name="managementBlocked" value="true" <c:if test="${user.managementBlocked}">checked</c:if> /><br/>
                        프로파일 제한 <input type="checkbox" name="profileRestricted" value="true" <c:if test="${user.profileRestricted}">checked</c:if> /><br/>
                        <button type="submit">제한 저장</button>
                    </form>
                </c:if>
                <c:if test="${user.role eq 'SUPER_ADMIN'}">
                    수퍼관리자는 수정 대상이 아닙니다.
                </c:if>
            </td>
        </tr>
    </c:forEach>
</table>

<h3>블로그 목록</h3>
<ul>
    <c:forEach var="blog" items="${blogs}">
        <li>${blog.blogId} / ${blog.blogName} / 공개여부: ${blog.isPublic}</li>
    </c:forEach>
</ul>