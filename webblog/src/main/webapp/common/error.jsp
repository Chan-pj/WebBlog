<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<h2>오류 발생</h2>
<p>${pageContext.exception.message}</p>
<pre><% exception.printStackTrace(new java.io.PrintWriter(out)); %></pre>