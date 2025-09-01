<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-Hant">
<head>
  <meta charset="UTF-8">
  <title>寧夏夜市智慧管理與數據分析系統</title>
  <link rel="stylesheet" href="assets/style.css">
</head>
<body>
  <div class="nav">
    <div class="container">
      <a href="<%=request.getContextPath()%>/">首頁</a>
      <a href="register.jsp">註冊</a>
      <a href="login.jsp">登入</a>
      <a href="api/health" target="_blank">健康檢查</a>
    </div>
  </div>
  <div class="container">
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
  </div>
  <footer>© 2025 寧夏夜市專案</footer>
</body>
</html>
