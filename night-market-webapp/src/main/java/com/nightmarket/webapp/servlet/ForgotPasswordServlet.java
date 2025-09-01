package com.nightmarket.webapp.servlet;

import com.nightmarket.webapp.dao.UserDAO;
import com.nightmarket.webapp.util.EmailUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@WebServlet("/forgot")
public class ForgotPasswordServlet extends HttpServlet {
  private final UserDAO userDAO = new UserDAO();
  private final SecureRandom RAND = new SecureRandom();

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.getRequestDispatcher("/WEB-INF/views/forgot_password.jsp").forward(req, resp);
  }

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String email = req.getParameter("email");
    if (email == null || email.isBlank()) {
      req.setAttribute("error", "請輸入 Email。");
      doGet(req, resp);
      return;
    }

    // 產 token（無論 email 是否存在都顯示成功，避免洩漏）
    byte[] buf = new byte[32];
    RAND.nextBytes(buf);
    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(buf);

    try {
      userDAO.saveResetToken(email, token, Instant.now().plus(1, ChronoUnit.HOURS));
      String base = req.getScheme() + "://" + req.getServerName()
          + (req.getServerPort() == 80 || req.getServerPort() == 443 ? "" : ":" + req.getServerPort())
          + req.getContextPath();
      String link = base + "/reset?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);

      EmailUtil.sendVerificationMail(email, link); // 沿用你的 EmailUtil（內容文字可改）
      req.setAttribute("msg", "若信箱存在，重設連結已寄出（1 小時內有效）。");
      doGet(req, resp);
    } catch (Exception e) {
      req.setAttribute("error", "寄送失敗：" + e.getMessage());
      doGet(req, resp);
    }
  }
}
