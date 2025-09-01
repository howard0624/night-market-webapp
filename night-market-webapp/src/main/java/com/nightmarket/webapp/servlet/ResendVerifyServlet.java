package com.nightmarket.webapp.servlet;

import com.nightmarket.webapp.dao.UserDAO;
import com.nightmarket.webapp.model.User;
import com.nightmarket.webapp.service.AuthService;
import com.nightmarket.webapp.util.EmailUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@WebServlet("/resend-verify")
public class ResendVerifyServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");

        try {
            // 防止亂寄：要求使用者先輸入密碼驗證身分（你也可改允許已登入才重寄）
            String password = req.getParameter("password"); // 可改為從 session 取得或省略
            User u = (password == null) ? userDAO.findByEmail(email) : authService.login(email, password);
            if (u == null) {
                req.setAttribute("error", "帳號或密碼錯誤");
                req.setAttribute("email", email);
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }
            if (u.getIsVerified() == 1) {
                req.setAttribute("msg", "此帳號已完成驗證，請直接登入。");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }

            // 重新產 token（24h）
            String token = userDAO.regenerateToken(email, Instant.now().plus(24, ChronoUnit.HOURS));

            String scheme = req.getScheme();
            String host = req.getServerName();
            int port = req.getServerPort();
            String ctx = req.getContextPath();
            StringBuilder base = new StringBuilder();
            base.append(scheme).append("://").append(host);
            boolean needPort = (scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443);
            if (needPort) base.append(":").append(port);
            base.append(ctx);

            String link = base + "/verify?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                        + "&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8);

            EmailUtil.sendVerificationMail(email, link);

            req.setAttribute("msg", "驗證信已重新寄送，請在 24 小時內完成驗證。");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "系統錯誤：" + e.getMessage());
            req.setAttribute("email", email);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
