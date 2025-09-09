<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-Hant">
<head>
  <meta charset="UTF-8"/>
  <link rel="stylesheet" href="<c:url value='/assets/style.css'/>"/>
  <title>登入</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<div class="container">
  <h1>Login</h1>

  <c:if test="${param.registered == '1'}">
    <div class="success">註冊成功，請登入。</div>
  </c:if>

  <c:if test="${not empty error}">
    <div class="error"><c:out value="${error}"/></div>
  </c:if>
  <c:if test="${not empty msg}">
    <div class="success"><c:out value="${msg}"/></div>
  </c:if>

  <form method="post" action="<c:url value='/login'/>">
    <label>Email</label>
    <input type="email" name="email" value="<c:out value='${email}'/>" required autocomplete="email" autofocus />

    <label>密碼</label>
    <input type="password" name="password" required autocomplete="current-password" />

    <div class="form-actions">
      <button type="submit" class="btn">登入</button>
      <a class="btn" style="background:#374151;" href="register.jsp">沒有帳號？去註冊</a>
    </div>
  </form>

  <c:if test="${not empty email}">
    <form method="post" action="<c:url value='/resend-verify'/>" class="form-actions">
      <input type="hidden" name="email" value="<c:out value='${email}'/>"/>
      <button class="btn" type="submit">重新寄送驗證信</button>
    </form>
  </c:if>
</div>
</body>
</html>
