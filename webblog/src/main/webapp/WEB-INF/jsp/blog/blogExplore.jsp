<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
.be-title {
    font-size:16px; font-weight:500;
    color:var(--color-text-primary, #1a1a1a); margin-bottom:4px;
}
.be-hint {
    font-size:13px; color:var(--color-text-tertiary, #aaa); margin-bottom:1.25rem;
}
.be-card {
    background:var(--color-background-primary, #fff);
    border:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    border-radius:12px; padding:1.25rem;
}
.be-table { width:100%; border-collapse:collapse; font-size:13px; }
.be-table th {
    text-align:left; font-size:12px; font-weight:500;
    color:var(--color-text-secondary, #666); padding:0 12px 8px;
    border-bottom:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
}
.be-table th.center { text-align:center; }
.be-table td {
    padding:12px; vertical-align:middle;
    border-bottom:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    color:var(--color-text-primary, #1a1a1a);
}
.be-table tr:last-child td { border-bottom:none; }
.be-table tr:hover { background:var(--color-background-secondary, #f5f5f0); }
.be-table td.num {
    width:44px; text-align:center;
    color:var(--color-text-tertiary, #aaa); font-size:12px;
}
.be-table td.center { text-align:center; }
.be-table td.blog-name a {
    color:var(--color-text-primary, #1a1a1a);
    text-decoration:none; font-size:14px;
}
.be-table td.blog-name a:hover { color:#185fa5; }
.member-badge {
    display:inline-flex; align-items:center; gap:4px;
    font-size:12px; padding:2px 8px; border-radius:20px;
    background:#e6f1fb; color:#185fa5;
}
</style>

<p class="be-title">공개 블로그 찾기</p>
<p class="be-hint">멤버로 가입하려면 로그인 후 각 블로그에서 가입 신청을 하세요.</p>

<div class="be-card">
    <table class="be-table">
        <thead>
            <tr>
                <th class="center">ID</th>
                <th>블로그 이름</th>
                <th class="center">멤버 수</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="b" items="${blogs}">
                <tr>
                    <td class="num">${b.blogId}</td>
                    <td class="blog-name">
                        <a href="${pageContext.request.contextPath}/blog/${b.blogId}/view.do">
                            <c:out value="${b.blogName}"/>
                        </a>
                    </td>
                    <td class="center">
                        <span class="member-badge">${b.memberCount}명</span>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>