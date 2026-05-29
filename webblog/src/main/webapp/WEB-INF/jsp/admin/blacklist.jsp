<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
.bl-nav { margin-bottom:1.25rem; }
.bl-nav a {
    font-size:13px; color:#185fa5; text-decoration:none;
    padding:5px 10px; border:0.5px solid #b5d4f4;
    border-radius:8px; background:#e6f1fb;
}
.bl-alert {
    padding:10px 14px; border-radius:8px; font-size:13px;
    margin-bottom:16px; background:#fcebeb; color:#791f1f;
    border:0.5px solid #f7c1c1;
}
.bl-card {
    background:var(--color-background-primary, #fff);
    border:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    border-radius:12px; padding:1.5rem;
}
.bl-hint {
    font-size:13px; color:var(--color-text-secondary, #666);
    margin-bottom:1.25rem;
}
.bl-add-row {
    display:flex; gap:8px; align-items:center; margin-bottom:1.5rem;
}
.bl-add-row input {
    flex:1; font-size:13px; padding:8px 12px;
    border:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    border-radius:8px;
    background:var(--color-background-secondary, #f5f5f0);
    color:var(--color-text-primary, #1a1a1a);
}
.bl-add-btn {
    font-size:13px; padding:8px 18px; border-radius:8px;
    background:#185fa5; color:#fff; border:none; cursor:pointer;
    white-space:nowrap;
}
.bl-add-btn:hover { background:#0c447c; }
.bl-table { width:100%; border-collapse:collapse; font-size:13px; }
.bl-table th {
    text-align:left; font-size:12px; font-weight:500;
    color:var(--color-text-secondary, #666); padding:0 0 8px;
    border-bottom:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
}
.bl-table td {
    padding:12px 0; vertical-align:middle;
    border-bottom:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    color:var(--color-text-primary, #1a1a1a);
}
.bl-table tr:last-child td { border-bottom:none; }
.bl-del-btn {
    font-size:12px; padding:4px 10px; border-radius:6px;
    background:#fcebeb; color:#791f1f;
    border:0.5px solid #f7c1c1; cursor:pointer;
}
.bl-del-btn:hover { background:#f7c1c1; }
.bl-empty {
    font-size:13px; color:var(--color-text-tertiary, #aaa);
    padding:16px 0; text-align:center;
}
.bl-id { font-size:12px; color:var(--color-text-tertiary, #aaa); width:44px; }
</style>

<p style="font-size:16px; font-weight:500; color:var(--color-text-primary); margin-bottom:4px;">
    전역 금지어
</p>

<div class="bl-nav">
    <a href="${pageContext.request.contextPath}/admin/dashboard.do">대시보드로</a>
</div>

<c:if test="${param.error eq '1'}">
    <div class="bl-alert">입력값을 확인해 주세요.</div>
</c:if>

<div class="bl-card">
    <p class="bl-hint">모든 블로그에 공통으로 적용되는 금지어입니다.</p>

    <form method="post"
          action="${pageContext.request.contextPath}/admin/blacklist/add.do">
        <div class="bl-add-row">
            <input type="text" name="word" required maxlength="200" placeholder="금지어 입력"/>
            <button type="submit" class="bl-add-btn">추가</button>
        </div>
    </form>

    <table class="bl-table">
        <thead>
            <tr>
                <th class="bl-id">ID</th>
                <th>단어</th>
                <th style="width:60px; text-align:right;">삭제</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty globalBlacklist}">
                    <tr>
                        <td colspan="3" class="bl-empty">등록된 금지어가 없습니다.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="w" items="${globalBlacklist}">
                        <tr>
                            <td class="bl-id">${w.blacklistId}</td>
                            <td><c:out value="${w.word}"/></td>
                            <td style="text-align:right;">
                                <form method="post"
                                      action="${pageContext.request.contextPath}/admin/blacklist/delete.do"
                                      style="display:inline;">
                                    <input type="hidden" name="blacklistId" value="${w.blacklistId}"/>
                                    <button type="submit" class="bl-del-btn">삭제</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>