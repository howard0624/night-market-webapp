<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<html><head><meta charset="UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
<title>修改密碼</title></head>
<body>
<div class="container">
  <h1>修改密碼</h1>
  <c:if test="${not empty error}"><div class="error">${error}</div></c:if>
  <c:if test="${not empty msg}"><div class="success">${msg}</div></c:if>

  <form method="post">
    <label>目前密碼</label>
    <input type="password" name="current" required>
    <label>新密碼</label>
    <input type="password" name="password" required>
    <label>確認新密碼</label>
    <input type="password" name="password2" required>
    <div style="margin-top:12px">
      <button type="submit" class="btn">更新</button>
      <a class="btn" href="${pageContext.request.contextPath}/member/profile">返回</a>
    </div>
  </form>
</div>
</body></html>
