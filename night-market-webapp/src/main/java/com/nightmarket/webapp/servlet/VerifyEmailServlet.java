package com.nightmarket.webapp.servlet;

import com.nightmarket.webapp.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/verify")
public class VerifyEmailServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String token = req.getParameter("token");
        try {
            String email = (token == null) ? null : userDAO.findEmailByValidToken(token);
            if (email == null) {
                req.setAttribute("error", "驗證連結無效或已過期，請重新寄送驗證信。");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }

            userDAO.markVerified(email);
            req.setAttribute("msg", "驗證成功，現在可以登入。");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "系統錯誤：" + e.getMessage());
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
