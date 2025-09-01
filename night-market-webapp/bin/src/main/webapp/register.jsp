<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-Hant">
<head>
  <meta charset="UTF-8">
  <title>會員註冊</title>
  <link rel="stylesheet" href="assets/style.css">
</head>
<body>
  <div class="container">
    <h2>會員註冊</h2>
    <form method="post" action="register">
      <label>姓名</label>
      <input class="input" type="text" name="name" required>
      <label>Email</label>
      <input class="input" type="email" name="email" required>
      <label>密碼（至少 6 碼）</label>
      <input class="input" type="password" name="password" minlength="6" required>
      <div style="margin-top:12px">
        <button class="btn" type="submit">註冊</button>
        <a class="btn" style="background:#374151" href="login.jsp">已有帳號？去登入</a>
      </div>
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
