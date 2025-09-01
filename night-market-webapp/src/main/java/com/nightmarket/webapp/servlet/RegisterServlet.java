package com.nightmarket.webapp.servlet;

import com.nightmarket.webapp.dao.UserDAO;
import com.nightmarket.webapp.util.EmailUtil;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private static final SecureRandom RAND = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String pwd = req.getParameter("password");

        try {
            // 🚨 檢查帳號是否已存在
            if (userDAO.existsByEmail(email)) {
                req.setAttribute("error", "此 Email 已經註冊過，請直接登入或重寄驗證信。");
                req.setAttribute("email", email);
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }

            // ✅ 下面是你原本的註冊流程
            String hash = BCrypt.hashpw(pwd, BCrypt.gensalt(12));

            byte[] buf = new byte[32];
            RAND.nextBytes(buf);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
            Instant expires = Instant.now().plus(24, ChronoUnit.HOURS);

            userDAO.insertForVerify(name, email, hash, token, expires);

            String link = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort()
                    + req.getContextPath() + "/verify-email?token=" + token;
            EmailUtil.sendVerificationMail(email, link);

            req.setAttribute("msg", "註冊成功！請到信箱收驗證信（24 小時內有效）。");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "系統忙碌中，請稍後再試。");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }

}
