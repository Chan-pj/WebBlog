<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui" %>

<style>
.ad-header {
    display:flex; align-items:center; justify-content:space-between; margin-bottom:1.5rem;
}
.ad-title { font-size:16px; font-weight:500; color:var(--color-text-primary, #1a1a1a); }
.ad-nav { display:flex; gap:8px; }
.ad-icon-btn {
    display:inline-flex; align-items:center; gap:5px;
    font-size:12px; color:var(--color-text-secondary, #666); text-decoration:none;
    padding:6px 12px; border:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    border-radius:8px; background:transparent; cursor:pointer;
}
.ad-icon-btn:hover { background:var(--color-background-secondary, #f5f5f0); }
.ad-icon-btn i { font-size:15px; }
.ad-grid {
    display:grid; grid-template-columns:1fr 2fr; gap:1rem; align-items:start;
}
.ad-section-title {
    font-size:14px; font-weight:500;
    color:var(--color-text-primary, #1a1a1a); margin-bottom:8px;
}
.ad-card {
    background:var(--color-background-primary, #fff);
    border:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    border-radius:12px; padding:1.25rem;
}
.ad-badge { font-size:11px; padding:2px 8px; border-radius:20px; font-weight:500; }
.ad-badge-pub   { background:#e1f5ee; color:#085041; }
.ad-badge-prv   { background:#f1efe8; color:#5f5e5a; }
.ad-badge-role  { background:#f1efe8; color:#5f5e5a; }
.ad-badge-blog  { background:#e6f1fb; color:#185fa5; }
.ad-item {
    border-bottom:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
}
.ad-item:last-child { border-bottom:none; }
.ad-row {
    display:flex; align-items:center; gap:10px;
    padding:12px 0; cursor:pointer; user-select:none;
}
.ad-row-label { font-size:13px; color:var(--color-text-primary, #1a1a1a); flex:1; }
.ad-row-sub   { font-size:12px; color:var(--color-text-secondary, #666); }
.ad-row:hover .ad-row-label { color:#185fa5; }
.ad-chevron {
    font-size:14px; color:var(--color-text-tertiary, #aaa);
    transition:transform 0.3s ease; display:inline-block;
}
.ad-chevron.open { transform:rotate(180deg); }
.ad-detail {
    max-height:0; overflow:hidden;
    transition:max-height 0.3s ease, opacity 0.3s ease;
    opacity:0;
}
.ad-detail.open { max-height:400px; opacity:1; }
.ad-perm-row { display:flex; align-items:center; gap:14px; flex-wrap:wrap; margin-bottom:10px; }
.ad-perm-label {
    display:flex; align-items:center; gap:5px;
    font-size:12px; color:var(--color-text-secondary, #666); margin:0;
}
.ad-quota-row { display:flex; gap:10px; flex-wrap:wrap; margin-bottom:10px; }
.ad-quota-item { display:flex; align-items:center; gap:5px; }
.ad-quota-item span { font-size:11px; color:var(--color-text-tertiary, #aaa); }
.ad-quota-item input {
    width:56px; font-size:11px; padding:3px 6px;
    border:0.5px solid var(--color-border-tertiary, rgba(0,0,0,0.12));
    border-radius:6px;
    background:var(--color-background-secondary, #f5f5f0);
    color:var(--color-text-primary, #1a1a1a);
}
.ad-quota-item input.wide { width:80px; }
.ad-restrict-row { display:flex; align-items:center; gap:14px; margin-bottom:10px; }
.ad-action-row { display:flex; gap:6px; padding-bottom:14px; }
.ad-save-btn {
    font-size:12px; padding:4px 14px; border-radius:6px;
    border:none; cursor:pointer; background:#185fa5; color:#fff;
}
.ad-save-btn:hover { background:#0c447c; }
.ad-del-btn {
    font-size:12px; padding:4px 14px; border-radius:6px;
    cursor:pointer; background:#fcebeb; color:#791f1f;
    border:0.5px solid #f7c1c1;
}
.ad-del-btn:hover { background:#f7c1c1; }
</style>

<div class="ad-header">
    <p class="ad-title">관리자 대시보드</p>
    <div class="ad-nav">
        <a href="${pageContext.request.contextPath}/admin/blacklist.do" class="ad-icon-btn">
            <i class="ti ti-shield-off" aria-hidden="true"></i> 금지어
        </a>
    </div>
</div>

<div class="ad-grid">

    <%-- 왼쪽: 블로그 목록 --%>
    <div>
        <p class="ad-section-title">블로그 목록</p>
        <div class="ad-card">
            <c:forEach var="blog" items="${blogs}">
                <div class="ad-item">
                    <div class="ad-row"
                         onclick="toggleItem('blog-${blog.blogId}', 'bc-${blog.blogId}')">
                        <span class="ad-row-label"><c:out value="${blog.blogName}"/></span>
                        <c:choose>
                            <c:when test="${blog.isPublic}">
                                <span class="ad-badge ad-badge-pub">공개</span>
                            </c:when>
                            <c:otherwise>
                                <span class="ad-badge ad-badge-prv">비공개</span>
                            </c:otherwise>
                        </c:choose>
                        <span class="ad-chevron" id="bc-${blog.blogId}">▾</span>
                    </div>

                    <div class="ad-detail" id="blog-${blog.blogId}">
                        <div class="ad-quota-row">
                            <div class="ad-quota-item">
                                <span>게시판</span>
                                <input type="number" id="boardLimit-${blog.blogId}"
                                       value="${blog.boardLimit}" min="0"/>
                            </div>
                            <div class="ad-quota-item">
                                <span>이미지 수</span>
                                <input type="number" id="imageLimitCount-${blog.blogId}"
                                       value="${blog.imageLimitCount}" min="0"/>
                            </div>
                            <div class="ad-quota-item">
                                <span>이미지 크기</span>
                                <input type="number" id="imageSizeLimitBytes-${blog.blogId}"
                                       value="${blog.imageSizeLimitBytes}" min="0" class="wide"/>
                            </div>
                        </div>
                        <div class="ad-action-row">
                            <form method="post"
                                  action="${pageContext.request.contextPath}/admin/blog/update.do"
                                  style="display:inline;">
                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"/>
                                <input type="hidden" name="blogId" value="${blog.blogId}"/>
                                <input type="hidden" name="blogName" value="<c:out value='${blog.blogName}'/>"/>
                                <input type="hidden" name="isPublic" value="${blog.isPublic}"/>
                                <input type="hidden" name="boardLimit"
                                       id="hBoardLimit-${blog.blogId}" value="${blog.boardLimit}"/>
                                <input type="hidden" name="imageLimitCount"
                                       id="hImageLimitCount-${blog.blogId}" value="${blog.imageLimitCount}"/>
                                <input type="hidden" name="imageSizeLimitBytes"
                                       id="hImageSizeLimitBytes-${blog.blogId}" value="${blog.imageSizeLimitBytes}"/>
                                <button type="submit" class="ad-save-btn"
                                        onclick="syncBlog('${blog.blogId}')">저장</button>
                            </form>
                            <form method="post"
                                  action="${pageContext.request.contextPath}/admin/blog/delete.do"
                                  style="display:inline;"
                                  onsubmit="return confirm('블로그를 삭제할까요?\n블로그의 모든 게시판·게시글·멤버 정보가 함께 삭제됩니다.');">
                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"/>
                                <input type="hidden" name="blogId" value="${blog.blogId}"/>
                                <button type="submit" class="ad-del-btn">삭제</button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
        <c:if test="${blogPaginationInfo.totalPageCount > 1}">
            <div class="wb-paging"><ui:pagination paginationInfo="${blogPaginationInfo}" type="image" jsFunction="goBlogPage"/></div>
        </c:if>
    </div>

    <%-- 오른쪽: 회원 목록 --%>
    <div>
        <p class="ad-section-title">회원 목록</p>
        <div class="ad-card">
            <c:forEach var="user" items="${users}">
                <c:if test="${user.role ne 'SUPER_ADMIN'}">
                    <div class="ad-item">
                        <div class="ad-row"
                             onclick="toggleItem('user-${user.userId}', 'uc-${user.userId}')">
                            <span class="ad-row-label"><c:out value="${user.email}"/></span>
                            <span class="ad-row-sub"><c:out value="${user.nickname}"/></span>
                            <c:choose>
                                <c:when test="${user.role eq 'BLOG_ADMIN'}">
                                    <span class="ad-badge ad-badge-blog">BLOG_ADMIN</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="ad-badge ad-badge-role">
                                        <c:out value="${user.role}"/>
                                    </span>
                                </c:otherwise>
                            </c:choose>
                            <span class="ad-chevron" id="uc-${user.userId}">▾</span>
                        </div>

                        <div class="ad-detail" id="user-${user.userId}">
                            <form method="post"
                                  action="${pageContext.request.contextPath}/admin/user/approve.do">
                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"/>
                                <input type="hidden" name="userId" value="${user.userId}"/>
                                <div class="ad-perm-row">
                                    <label class="ad-perm-label">
                                        <input type="checkbox" name="approved" value="true"
                                            <c:if test="${user.approved}">checked</c:if>/> 승인
                                    </label>
                                    <label class="ad-perm-label">
                                        <input type="checkbox" name="canCreateBlog" value="true"
                                            <c:if test="${user.canCreateBlog}">checked</c:if>/> 블로그 생성
                                    </label>
                                    <label class="ad-perm-label">
                                        <input type="checkbox" name="canCreateBoard" value="true"
                                            <c:if test="${user.canCreateBoard}">checked</c:if>/> 게시판 생성
                                    </label>
                                </div>
                                <div class="ad-quota-row">
                                    <div class="ad-quota-item">
                                        <span>게시판 쿼터</span>
                                        <input type="number" name="boardCreateQuota"
                                               value="${user.boardCreateQuota}" min="0"/>
                                    </div>
                                    <div class="ad-quota-item">
                                        <span>이미지 수</span>
                                        <input type="number" name="imageUploadLimitCount"
                                               value="${user.imageUploadLimitCount}" min="0"/>
                                    </div>
                                    <div class="ad-quota-item">
                                        <span>이미지 크기</span>
                                        <input type="number" name="imageSizeLimitBytes"
                                               value="${user.imageSizeLimitBytes}" min="0" class="wide"/>
                                    </div>
                                </div>
                                <div class="ad-restrict-row">
                                    <label class="ad-perm-label">
                                        <input type="checkbox" name="managementBlocked" value="true"
                                            <c:if test="${user.managementBlocked}">checked</c:if>/> 관리 차단
                                    </label>
                                    <label class="ad-perm-label">
                                        <input type="checkbox" name="profileRestricted" value="true"
                                            <c:if test="${user.profileRestricted}">checked</c:if>/> 프로필 제한
                                    </label>
                                </div>
                                <div class="ad-action-row">
                                    <button type="submit" class="ad-save-btn">저장</button>
                                </div>
                            </form>
                            <div class="ad-action-row">
                                <form method="post"
                                      action="${pageContext.request.contextPath}/admin/user/delete.do"
                                      onsubmit="return confirm('회원을 삭제할까요?\n회원이 소유한 블로그(모든 글 포함)와 회원이 쓴 글이 함께 삭제됩니다.');">
                                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"/>
                                    <input type="hidden" name="userId" value="${user.userId}"/>
                                    <button type="submit" class="ad-del-btn">삭제</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:if>
            </c:forEach>
        </div>
        <c:if test="${userPaginationInfo.totalPageCount > 1}">
            <div class="wb-paging"><ui:pagination paginationInfo="${userPaginationInfo}" type="image" jsFunction="goUserPage"/></div>
        </c:if>
    </div>

</div>

<script>
function toggleItem(detailId, chevronId) {
    var detail  = document.getElementById(detailId);
    var chevron = document.getElementById(chevronId);
    var isOpen  = detail.classList.contains('open');
    detail.classList.toggle('open', !isOpen);
    chevron.classList.toggle('open', !isOpen);
}

function syncBlog(blogId) {
    document.getElementById('hBoardLimit-' + blogId).value =
        document.getElementById('boardLimit-' + blogId).value;
    document.getElementById('hImageLimitCount-' + blogId).value =
        document.getElementById('imageLimitCount-' + blogId).value;
    document.getElementById('hImageSizeLimitBytes-' + blogId).value =
        document.getElementById('imageSizeLimitBytes-' + blogId).value;
}
</script>