<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<h2>회원가입</h2>
<form method="post" action="${pageContext.request.contextPath}/auth/register.do">
    이메일: <input type="text" name="email"/><br/>
    닉네임: <input type="text" name="nickname"/><br/>
    가입유형:
    <select name="registerRole">
        <option value="BLOG_MEMBER">블로그 멤버 (다른 사람 블로그에 가입해 글 작성)</option>
        <option value="BLOG_ADMIN">블로그 관리자(승인 필요)</option>
    </select><br/>
    <input type="submit" value="회원가입"/>
</form>