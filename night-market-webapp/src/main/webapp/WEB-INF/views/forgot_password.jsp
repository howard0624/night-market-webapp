<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<html><head><meta charset="UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
<title>忘記密碼</title></head>
<body>
<div class="container">
  <h1>忘記密碼</h1>
  <c:if test="${not empty error}"><div class="error">${error}</div></c:if>
  <c:if test="${not empty msg}"><div class="success">${msg}</div></c:if>

  <form method="post">
    <label>Email</label>
    <input type="email" name="email" required>
    <div style="margin-top:12px">
      <button type="submit" class="btn">寄送重設連結</button>
      <a class="btn" href="${pageContext.request.contextPath}/login.jsp">回登入</a>
    </div>
  </form>
</div>
</body></html>
