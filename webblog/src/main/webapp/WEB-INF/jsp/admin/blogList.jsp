<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>관리자 블로그 목록</h2>
<table style="border-collapse: collapse;">
    <tr>
        <th style="border: 1px solid #999; padding: 6px;">블로그 ID</th>
        <th style="border: 1px solid #999; padding: 6px;">블로그 이름</th>
        <th style="border: 1px solid #999; padding: 6px;">공개 여부</th>
        <th style="border: 1px solid #999; padding: 6px;">게시판 제한</th>
        <th style="border: 1px solid #999; padding: 6px;">이미지 개수 제한</th>
        <th style="border: 1px solid #999; padding: 6px;">이미지 크기 제한(Byte)</th>
        <th style="border: 1px solid #999; padding: 6px;">파일 제한(Byte)</th>
        <th style="border: 1px solid #999; padding: 6px;">총 용량(Byte)</th>
    </tr>
    <c:forEach var="blog" items="${blogs}">
        <tr>
            <td style="border: 1px solid #999; padding: 6px;">${blog.blogId}</td>
            <td style="border: 1px solid #999; padding: 6px;">${blog.blogName}</td>
            <td style="border: 1px solid #999; padding: 6px;">${blog.isPublic}</td>
            <td style="border: 1px solid #999; padding: 6px;">${blog.boardLimit}</td>
            <td style="border: 1px solid #999; padding: 6px;">${blog.imageLimitCount}</td>
            <td style="border: 1px solid #999; padding: 6px;">${blog.imageSizeLimitBytes}</td>
            <td style="border: 1px solid #999; padding: 6px;">${blog.fileSizeLimitBytes}</td>
            <td style="border: 1px solid #999; padding: 6px;">${blog.totalCapacityBytes}</td>
        </tr>
    </c:forEach>
</table>
