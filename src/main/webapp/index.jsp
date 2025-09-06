<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  // 預設自動導向到登入頁；帶 ?home=1 可停用導向以檢視首頁
  if (!"1".equals(request.getParameter("home"))) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
  }
%>
<!DOCTYPE html>
<html lang="zh-Hant">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>寧夏夜市智慧管理與數據分析系統</title>

  <link rel="stylesheet" href="<c:url value='/assets/style.css'/>">
  <!-- qrcodejs -->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>

  <style>
    .btn{padding:10px 16px;border-radius:8px;border:0;background:#2d6cdf;color:#fff;cursor:pointer}
    #qrcode canvas,#qrcode img{border-radius:12px;box-shadow:0 2px 12px rgba(0,0,0,.08)}
  </style>
</head>
<body>
  <!-- 共同導覽 -->
  <jsp:include page="/WEB-INF/jspf/header.jspf"/>

  <div class="container" style="padding-top:16px">
    <h1>寧夏夜市智慧管理與數據分析系統</h1>
    <p>三大子系統：顧客管理、店家管理、營運管理；另有推薦管理。</p>

    <div class="grid">
      <div class="card">
        <h3>顧客管理系統</h3>
        <p>註冊、登入、修改個人資訊、查詢熱門攤位、地圖與營業狀態、個人化推薦、線上點餐。</p>
      </div>
      <div class="card">
        <h3>店家管理系統</h3>
        <p>登入後台、餐點新增修改刪除、自動接單、查看訂單與出餐狀態。</p>
      </div>
      <div class="card">
        <h3>營運管理系統</h3>
        <p>每週任務與紅利、促銷與優惠、統計會員回饋與數據。</p>
      </div>
    </div>

    <!-- QRCode 區塊（自動以目前主機 + 專案 ContextPath 產生） -->
    <div style="margin:16px 0;">
      <div id="qrcode"></div>
      <p style="margin-top:8px;font-size:14px;">
        網址：<span id="pageUrl"></span>
      </p>
    </div>
  </div>

  <footer>© 2025 寧夏夜市專案</footer>

  <script>
    (function () {
      // 自動產生：主機 + contextPath + "/"（例如 https://xxx.onrender.com/）
      const targetUrl = window.location.origin + '<%= request.getContextPath() %>/';
      document.getElementById('pageUrl').textContent = targetUrl;

      new QRCode(document.getElementById("qrcode"), {
        text: targetUrl,
        width: 220,
        height: 220,
        correctLevel: QRCode.CorrectLevel.M
      });
    })();
  </script>
</body>
</html>
