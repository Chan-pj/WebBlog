<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
.ml-nav { margin-bottom:1.25rem; }
.ml-nav a {
    font-size:13px; color:#185fa5; text-decoration:none;
    padding:5px 10px; border:0.5px solid #b5d4f4;
    border-radius:8px; background:#e6f1fb;
}
.ml-table { width:100%; border-collapse:collapse; font-size:13px; }
.ml-table th {
    text-align:left; font-size:12px; font-weight:500;
    color:var(--color-text-secondary, #666); padding:0 0 8px;
    border-bottom:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
}
.ml-table td {
    padding:12px 0;
    border-bottom:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    color:var(--color-text-primary, #1a1a1a);
    vertical-align:middle;
}
.ml-table tr:last-child td { border-bottom:none; }
.status-badge {
    display:inline-block; font-size:11px; font-weight:500;
    padding:3px 9px; border-radius:20px;
}
.status-ACTIVE  { background:#e1f5ee; color:#085041; }
.status-PENDING { background:#faeeda; color:#633806; }
.status-BLOCKED { background:#fcebeb; color:#791f1f; }
.status-actions { display:flex; gap:6px; flex-wrap:wrap; }
.status-btn {
    font-size:11px; padding:4px 10px; border-radius:6px;
    border:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    background:transparent; cursor:pointer;
    color:var(--color-text-secondary, #666);
}
.status-btn:hover { background:var(--color-background-secondary, #f5f5f0); }
.status-btn.active-btn  { border-color:#9fe1cb; color:#085041; background:#e1f5ee; }
.status-btn.pending-btn { border-color:#fac775; color:#633806; background:#faeeda; }
.status-btn.blocked-btn { border-color:#f7c1c1; color:#791f1f; background:#fcebeb; }
</style>

<p style="font-size:16px; font-weight:500; color:var(--color-text-primary); margin-bottom:1rem;">
    블로그 멤버 목록
</p>

<div class="ml-nav">
    <a href="${pageContext.request.contextPath}/blog/${blogId}/view.do">블로그로 돌아가기</a>
</div>

<c:if test="${param.error eq 'status'}">
    <p style="color:#791f1f; font-size:13px;">멤버 상태를 변경할 수 없습니다.</p>
</c:if>

<table class="ml-table">
    <thead>
        <tr>
            <th>멤버 ID</th>
            <th>닉네임</th>
            <th>역할</th>
            <th>현재 상태</th>
            <th>상태 변경</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="member" items="${members}">
            <tr>
                <td>${member.memberId}</td>
                <td><c:out value="${member.nickname}"/></td>
                <td>${member.memberRole}</td>
                <td>
                    <span class="status-badge status-${member.status}">
                        ${member.status}
                    </span>
                </td>
                <td>
                    <div class="status-actions">
                        <form method="post"
                              action="${pageContext.request.contextPath}/blog/${blogId}/member/status.do"
                              style="display:inline;">
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"/>
                            <input type="hidden" name="memberId" value="${member.memberId}"/>
                            <input type="hidden" name="status" value="ACTIVE"/>
                            <button type="submit" class="status-btn active-btn">활성</button>
                        </form>
                        <form method="post"
                              action="${pageContext.request.contextPath}/blog/${blogId}/member/status.do"
                              style="display:inline;">
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"/>
                            <input type="hidden" name="memberId" value="${member.memberId}"/>
                            <input type="hidden" name="status" value="PENDING"/>
                            <button type="submit" class="status-btn pending-btn">대기</button>
                        </form>
                        <form method="post"
                              action="${pageContext.request.contextPath}/blog/${blogId}/member/status.do"
                              style="display:inline;">
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"/>
                            <input type="hidden" name="memberId" value="${member.memberId}"/>
                            <input type="hidden" name="status" value="BLOCKED"/>
                            <button type="submit" class="status-btn blocked-btn">차단</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>