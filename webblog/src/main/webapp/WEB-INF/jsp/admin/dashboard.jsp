<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div style="display:flex; align-items:baseline; justify-content:space-between; margin-bottom:24px;">
    <h2 style="margin:0; border:none; padding:0;">관리자 대시보드</h2>
    <div style="display:flex; gap:16px;">
        <a href="${pageContext.request.contextPath}/admin/blacklist.do" style="font-size:13px; color:#9a9a9a;">금지어 관리</a>
        <a href="${pageContext.request.contextPath}/admin/blogs.do" style="font-size:13px; color:#9a9a9a;">블로그 설정</a>
    </div>
</div>

<h3 style="margin-bottom:14px;">회원 목록</h3>

<table>
    <thead>
        <tr>
            <th style="width:40px;">ID</th>
            <th>이메일</th>
            <th style="width:90px;">닉네임</th>
            <th style="width:110px;">역할</th>
            <th style="width:200px;">권한 설정</th>
            <th style="width:160px;">제한 설정</th>
        </tr>
    </thead>
    <tbody>
    <c:forEach var="user" items="${users}">
        <tr>
            <td style="color:#bbb;">${user.userId}</td>
            <td style="font-size:13px;">${user.email}</td>
            <td>${user.nickname}</td>
            <td>
                <span style="font-size:11px; padding:3px 8px; border-radius:20px; background:#f0ebe5; color:#888;">
                    ${user.role}
                </span>
            </td>

            <c:choose>
                <c:when test="${user.role eq 'SUPER_ADMIN'}">
                    <td colspan="2" style="color:#bbb; font-size:12px;">수정 불가</td>
                </c:when>
                <c:otherwise>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/admin/user/approve.do">
                            <input type="hidden" name="userId" value="${user.userId}"/>
                            <div style="display:flex; flex-direction:column; gap:6px; margin-bottom:10px;">
                                <label style="display:flex; align-items:center; gap:6px; font-size:12px; color:#555; margin:0;">
                                    <input type="checkbox" name="approved" value="true" <c:if test="${user.approved}">checked</c:if>/> 승인
                                </label>
                                <label style="display:flex; align-items:center; gap:6px; font-size:12px; color:#555; margin:0;">
                                    <input type="checkbox" name="canCreateBlog" value="true" <c:if test="${user.canCreateBlog}">checked</c:if>/> 블로그 생성
                                </label>
                                <label style="display:flex; align-items:center; gap:6px; font-size:12px; color:#555; margin:0;">
                                    <input type="checkbox" name="canCreateBoard" value="true" <c:if test="${user.canCreateBoard}">checked</c:if>/> 게시판 생성
                                </label>
                            </div>
                            <div style="display:flex; flex-direction:column; gap:6px; margin-bottom:12px;">
                                <div style="display:flex; align-items:center; gap:8px;">
                                    <span style="font-size:12px; color:#aaa; width:70px;">게시판 쿼터</span>
                                    <input type="number" name="boardCreateQuota" value="${user.boardCreateQuota}" min="0" style="width:60px; padding:4px 8px; font-size:12px;"/>
                                </div>
                                <div style="display:flex; align-items:center; gap:8px;">
                                    <span style="font-size:12px; color:#aaa; width:70px;">이미지 개수</span>
                                    <input type="number" name="imageUploadLimitCount" value="${user.imageUploadLimitCount}" min="0" style="width:60px; padding:4px 8px; font-size:12px;"/>
                                </div>
                                <div style="display:flex; align-items:center; gap:8px;">
                                    <span style="font-size:12px; color:#aaa; width:70px;">이미지 크기</span>
                                    <input type="number" name="imageSizeLimitBytes" value="${user.imageSizeLimitBytes}" min="0" style="width:90px; padding:4px 8px; font-size:12px;"/>
                                </div>
                            </div>
                            <button type="submit" class="btn btn-sm btn-primary">저장</button>
                        </form>
                    </td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/admin/user/protection.do">
                            <input type="hidden" name="userId" value="${user.userId}"/>
                            <div style="display:flex; flex-direction:column; gap:6px; margin-bottom:12px;">
                                <label style="display:flex; align-items:center; gap:6px; font-size:12px; color:#555; margin:0;">
                                    <input type="checkbox" name="managementBlocked" value="true" <c:if test="${user.managementBlocked}">checked</c:if>/> 관리 차단
                                </label>
                                <label style="display:flex; align-items:center; gap:6px; font-size:12px; color:#555; margin:0;">
                                    <input type="checkbox" name="profileRestricted" value="true" <c:if test="${user.profileRestricted}">checked</c:if>/> 프로필 제한
                                </label>
                            </div>
                            <button type="submit" class="btn btn-sm" style="background:#e0a0a0;">저장</button>
                        </form>
                    </td>
                </c:otherwise>
            </c:choose>
        </tr>
    </c:forEach>
    </tbody>
</table>

<h3 style="margin-top:32px; margin-bottom:14px;">블로그 목록</h3>
<table>
    <thead>
        <tr>
            <th style="width:50px;">ID</th>
            <th>블로그명</th>
            <th style="width:80px;">공개 여부</th>
        </tr>
    </thead>
    <tbody>
    <c:forEach var="blog" items="${blogs}">
        <tr>
            <td style="color:#bbb;">${blog.blogId}</td>
            <td>
                <a href="${pageContext.request.contextPath}/blog/${blog.blogId}/view.do" style="font-size:13px;">
                    ${blog.blogName}
                </a>
            </td>
            <td>
                <c:choose>
                    <c:when test="${blog.isPublic}">
                        <span style="font-size:11px; padding:3px 8px; border-radius:20px; background:#e8f4ee; color:#2a6040;">공개</span>
                    </c:when>
                    <c:otherwise>
                        <span style="font-size:11px; padding:3px 8px; border-radius:20px; background:#f0ebe5; color:#888;">비공개</span>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>