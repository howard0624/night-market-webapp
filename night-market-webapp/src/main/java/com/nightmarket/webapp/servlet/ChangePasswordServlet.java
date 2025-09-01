package com.nightmarket.webapp.servlet;

import com.nightmarket.webapp.dao.UserDAO;
import com.nightmarket.webapp.model.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/member/change-password")
public class ChangePasswordServlet extends HttpServlet {
  private final UserDAO userDAO = new UserDAO();

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.getRequestDispatcher("/WEB-INF/views/change_password.jsp").forward(req, resp);
  }

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    User u = (User) req.getSession().getAttribute("user");

    String current = req.getParameter("current");
    String np = req.getParameter("password");
    String np2 = req.getParameter("password2");

    if (np == null || np.length() < 6 || !np.equals(np2)) {
      req.setAttribute("error", "新密碼不一致或長度不足（>=6）。");
      doGet(req, resp);
      return;
    }

    try {
      String hash = userDAO.findPasswordHashById(u.getId());
      if (hash == null || !BCrypt.checkpw(current, hash)) {
        req.setAttribute("error", "目前密碼錯誤。");
        doGet(req, resp);
        return;
      }
      String newHash = BCrypt.hashpw(np, BCrypt.gensalt(12));
      userDAO.updatePassword(u.getId(), newHash);
      req.setAttribute("msg", "密碼已更新。");
      doGet(req, resp);
    } catch (Exception e) {
      req.setAttribute("error", "更新失敗：" + e.getMessage());
      doGet(req, resp);
    }
  }
}
