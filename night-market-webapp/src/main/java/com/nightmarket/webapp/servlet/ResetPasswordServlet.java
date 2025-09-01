package com.nightmarket.webapp.servlet;

import com.nightmarket.webapp.dao.UserDAO;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/reset")
public class ResetPasswordServlet extends HttpServlet {
  private final UserDAO userDAO = new UserDAO();

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String token = req.getParameter("token");
    req.setAttribute("token", token);
    req.getRequestDispatcher("/WEB-INF/views/reset_password.jsp").forward(req, resp);
  }

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String token = req.getParameter("token");
    String p1 = req.getParameter("password");
    String p2 = req.getParameter("password2");
    if (token == null || token.isBlank()) {
      req.setAttribute("error", "連結無效。");
      doGet(req, resp);
      return;
    }
    if (p1 == null || p1.length() < 6 || !p1.equals(p2)) {
      req.setAttribute("error", "密碼不一致或長度不足（>=6）。");
      doGet(req, resp);
      return;
    }
    try {
      String email = userDAO.findEmailByValidResetToken(token);
      if (email == null) {
        req.setAttribute("error", "重設連結已失效，請重新申請。");
        doGet(req, resp);
        return;
      }
      String hash = BCrypt.hashpw(p1, BCrypt.gensalt(12));
      // 透過 email 取 id 更新密碼（若你已有 findByEmail 也可先查 id）
      try (var c = com.nightmarket.webapp.util.DB.getConnection();
           var ps = c.prepareStatement("UPDATE users SET password_hash=?, reset_token=NULL, reset_expires=NULL WHERE email=?")) {
        ps.setString(1, hash);
        ps.setString(2, email);
        ps.executeUpdate();
      }
      req.setAttribute("msg", "密碼已更新，請登入。");
      req.getRequestDispatcher("/login.jsp").forward(req, resp);
    } catch (Exception e) {
      req.setAttribute("error", "處理失敗：" + e.getMessage());
      doGet(req, resp);
    }
  }
}
