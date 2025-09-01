<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="user" value="${sessionScope.user}" />
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"/>

<div class="container">
  <h1>個人資料</h1>

  <c:if test="${not empty error}"><div class="error">${error}</div></c:if>
  <c:if test="${not empty msg}"><div class="success">${msg}</div></c:if>

  <div style="display:grid;grid-template-columns:280px 1fr;gap:24px;align-items:start">
    <!-- 大頭貼（已改成：即時預覽 + 自動上傳 + 成功回填） -->
    <div>
      <h3>大頭貼</h3>

      <!-- 圓形預覽圖 -->
      <img id="avatarPreview"
           src="${empty user.avatarUrl ? pageContext.request.contextPath.concat('/assets/default-avatar.png') : user.avatarUrl}"
           style="width:140px;height:140px;border-radius:50%;object-fit:cover;border:1px solid #ddd">

      <!-- 自動上傳表單（AJAX） -->
      <form id="avatarForm"
            method="post"
            action="${pageContext.request.contextPath}/profile?action=avatar&ajax=1"
            enctype="multipart/form-data"
            style="margin-top:8px">
        <input id="avatarInput" type="file" name="avatar" accept="image/*" />
        <div class="muted">支援 JPG / PNG / GIF，最大 2MB</div>
      </form>

      <div id="avatarMsg" class="success" style="margin-top:6px"></div>
    </div>

    <!-- 基本資料（保持原樣） -->
    <div>
      <h3>基本資料</h3>
      <form method="post" action="${pageContext.request.contextPath}/profile">
        <input type="hidden" name="action" value="info"/>
        <label>姓名</label>
        <input type="text" name="name" value="${user.name}" required />
        <label>Email</label>
        <input type="email" value="${user.email}" disabled />
        <div style="margin-top:12px">
          <button class="btn" type="submit">儲存</button>
          <a class="btn" style="background:#374151" href="${pageContext.request.contextPath}/change_password.jsp">變更密碼</a>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- 尾端 JS：選檔即預覽、背景上傳、完成後更新預覽與（若存在）右上角頭像 -->
<script>
document.addEventListener('DOMContentLoaded', () => {
  const form   = document.getElementById('avatarForm');
  const input  = document.getElementById('avatarInput');
  const img    = document.getElementById('avatarPreview');
  const msg    = document.getElementById('avatarMsg');
  const navImg = document.getElementById('navAvatar'); // 若 header.jspf 有加此 id，會一併更新

  // 避免快取的小工具
  const bust = (url) => url + (url.includes('?') ? '&' : '?') + 't=' + Date.now();

  input.addEventListener('change', () => {
    const file = input.files && input.files[0];
    if (!file) return;

    // 1) 先在前端直接預覽
    const blobURL = URL.createObjectURL(file);
    img.src = blobURL;
    img.onload = () => URL.revokeObjectURL(blobURL);

    // 2) 立即用 fetch 送出表單（AJAX）
    const fd = new FormData(form);
    fetch(form.action, {
      method: 'POST',
      body: fd,
      headers: { 'X-Requested-With': 'fetch' }
    })
    .then(r => r.json().catch(() => null).then(data => ({ ok: r.ok, data })))
    .then(({ ok, data }) => {
      if (!ok) throw new Error((data && data.message) || '上傳失敗');
      if (data && data.url) {
        // 後端回傳的新頭像網址，刷新預覽與（若存在）右上角頭像
        img.src = bust(data.url);
        if (navImg) navImg.src = bust(data.url);
        msg.className = 'success';
        msg.textContent = '頭像已更新！';
      } else {
        // 若後端未回 JSON，保險起見整頁刷新
        location.reload();
      }
    })
    .catch(err => {
      msg.className = 'error';
      msg.textContent = '上傳失敗：' + err.message;
    });
  });
});
</script>
