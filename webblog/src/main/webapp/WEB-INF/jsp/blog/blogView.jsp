<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
.blog-view-card {
    background: var(--color-background-primary, #fff);
    border: 0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    border-radius: 12px;
    padding: 1.5rem;
}
.blog-badge {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    padding: 3px 10px;
    border-radius: 20px;
    font-weight: 500;
}
.blog-badge.public  { background: #e1f5ee; color: #085041; }
.blog-badge.private { background: #f1efe8; color: #5f5e5a; }
.blog-alert {
    padding: 10px 14px;
    border-radius: 8px;
    font-size: 13px;
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    gap: 8px;
}
.blog-alert.success { background: #e1f5ee; color: #085041; border: 0.5px solid #9fe1cb; }
.blog-alert.danger  { background: #fcebeb; color: #791f1f; border: 0.5px solid #f7c1c1; }
.blog-divider {
    border: none;
    border-top: 0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    margin: 1.25rem 0;
}
.blog-actions { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.blog-action-link {
    display: inline-flex;
    align-items: center;
    gap: 5px;
    font-size: 13px;
    color: var(--color-text-secondary, #666);
    text-decoration: none;
    padding: 6px 12px;
    border: 0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    border-radius: 8px;
    background: transparent;
}
.blog-action-link:hover { background: var(--color-background-secondary, #f5f5f0); }
.blog-action-link.primary { color: #185fa5; border-color: #b5d4f4; background: #e6f1fb; }
.blog-action-link.primary:hover { background: #b5d4f4; }
.blog-join-btn {
    display: inline-flex;
    align-items: center;
    gap: 5px;
    font-size: 13px;
    font-weight: 500;
    padding: 6px 14px;
    border-radius: 8px;
    background: #185fa5;
    color: #fff;
    border: none;
    cursor: pointer;
}
.blog-join-btn:hover { background: #0c447c; }
</style>

<c:choose>
<c:when test="${not empty blog}">

    <c:if test="${param.joinOk eq '1'}">
        <div class="blog-alert success">멤버 가입이 완료되었습니다.</div>
    </c:if>
    <c:if test="${param.joinError eq '1'}">
        <div class="blog-alert danger">멤버 가입을 처리할 수 없습니다.</div>
    </c:if>
    <c:if test="${param.error eq 'forbidden'}">
        <div class="blog-alert danger">접근 권한이 없습니다.</div>
    </c:if>

    <div class="blog-view-card">
        <div style="display:flex; align-items:flex-start; justify-content:space-between; gap:12px;">
            <div>
                <p style="font-size:20px; font-weight:500; color:var(--color-text-primary);">
                    <c:out value="${blog.blogName}"/>
                </p>
                <p style="font-size:12px; color:var(--color-text-tertiary); margin-top:4px;">
                    블로그 #<c:out value="${blog.blogId}"/>
                </p>
            </div>
            <span class="blog-badge ${blog.isPublic ? 'public' : 'private'}">
                ${blog.isPublic ? '공개' : '비공개'}
            </span>
        </div>

        <hr class="blog-divider">

        <div class="blog-actions">
            <a href="${pageContext.request.contextPath}/blog/${blog.blogId}/boards.do"
               class="blog-action-link primary">게시판 보기</a>

            <c:if test="${isOwner}">
                <a href="${pageContext.request.contextPath}/blog/${blog.blogId}/members.do"
                   class="blog-action-link">멤버 관리</a>
                <a href="${pageContext.request.contextPath}/blog/${blog.blogId}/blacklist.do"
                   class="blog-action-link">금지어 관리</a>
            </c:if>

            <c:if test="${canJoinAsMember}">
                <form method="post"
                      action="${pageContext.request.contextPath}/blog/${blog.blogId}/join.do"
                      style="margin:0;">
                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"/>
                    <button type="submit" class="blog-join-btn">멤버 가입</button>
                </form>
            </c:if>
        </div>
    </div>

</c:when>
<c:otherwise>
    <p style="font-size:13px; color:var(--color-text-tertiary);">블로그 정보를 찾을 수 없습니다.</p>
</c:otherwise>
</c:choose>