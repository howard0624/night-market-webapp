<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-Hant">
<head>
  <meta charset="UTF-8">
  <title>登入</title>
  <link rel="stylesheet" href="assets/style.css">
</head>
<body>
  <div class="container">
    <h2>登入</h2>
    <form method="post" action="login">
      <label>Email</label>
      <input class="input" type="email" name="email" required>
      <label>密碼</label>
      <input class="input" type="password" name="password" required>
      <div style="margin-top:12px">
        <button class="btn" type="submit">登入</button>
        <a class="btn" style="background:#374151" href="register.jsp">沒帳號？去註冊</a>
      </div>
      <%
        if ("1".equals(request.getParameter("registered"))) {
      %>
      <div class="success">註冊成功，請登入。</div>
      <% } %>
      <%
        String err = (String)request.getAttribute("error");
        if (err != null) {
      %>
      <div class="error"><%= err %></div>
      <% } %>
    </form>
  </div>
</body>
</html>
