package com.nightmarket.webapp.servlet;

import com.nightmarket.webapp.dao.UserDAO;
import com.nightmarket.webapp.model.User;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.Locale;

@WebServlet("/profile")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024, maxRequestSize = 3 * 1024 * 1024)
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private Path root;

    @Override
    public void init() throws ServletException {
        root = Paths.get(System.getProperty("catalina.base"), "night-market-uploads", "avatars");
        try { Files.createDirectories(root); } catch (IOException ignored) {}
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User sessionUser = (User) req.getSession().getAttribute("user");
        if (sessionUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        try {
            User fresh = userDAO.findById(sessionUser.getId());
            req.setAttribute("user", fresh);
        } catch (Exception e) { throw new ServletException(e); }
        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("user");
        if (u == null) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }

        String action = req.getParameter("action");
        try {
            if ("info".equals(action)) {
                String name = req.getParameter("name");
                if (name == null || name.trim().isEmpty()) {
                    req.setAttribute("error", "姓名不可為空白");
                } else {
                    userDAO.updateName(u.getId(), name.trim());
                    // 重新撈最新資料，更新 session 與本次請求顯示
                    User fresh = userDAO.findById(u.getId());
                    req.getSession().setAttribute("user", fresh);
                    req.setAttribute("user", fresh);
                    req.setAttribute("msg", "基本資料已更新");
                }
                req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
                return;
            }
            else if ("avatar".equals(action)) {
                boolean ajax = "1".equals(req.getParameter("ajax")) ||
                               "fetch".equalsIgnoreCase(req.getHeader("X-Requested-With"));

                try {
                    Part part = req.getPart("avatar");
                    if (part == null || part.getSize() == 0) {
                        if (ajax) returnJson(resp, 400, false, "請選擇圖片", null);
                        else { req.setAttribute("error", "請選擇圖片"); forwardProfile(req, resp, u); }
                        return;
                    }
                    if (part.getSize() > 2L * 1024 * 1024) {
                        if (ajax) returnJson(resp, 400, false, "圖片請小於 2MB", null);
                        else { req.setAttribute("error", "圖片請小於 2MB"); forwardProfile(req, resp, u); }
                        return;
                    }

                    String ct = (part.getContentType() == null ? "" : part.getContentType()).toLowerCase(Locale.ROOT);
                    String ext = ct.contains("png") ? "png"
                               : (ct.contains("jpeg") || ct.contains("jpg")) ? "jpg"
                               : ct.contains("gif") ? "gif" : null;
                    if (ext == null) {
                        if (ajax) returnJson(resp, 400, false, "僅支援 JPG/PNG/GIF", null);
                        else { req.setAttribute("error", "僅支援 JPG/PNG/GIF"); forwardProfile(req, resp, u); }
                        return;
                    }

                    // 二次驗證真的為圖片
                    try (InputStream in = part.getInputStream()) {
                        BufferedImage img = ImageIO.read(in);
                        if (img == null) throw new IOException("檔案格式錯誤，請上傳圖片");
                    }

                    String filename = "u" + u.getId() + "_" + System.currentTimeMillis() + "." + ext;
                    Path dest = root.resolve(filename).normalize();
                    try (InputStream in = part.getInputStream()) {
                        Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
                    }

                    // 對外公開 URL（由 /u/* 提供）
                    String publicUrl = req.getContextPath() + "/u/" + filename;

                    // 更新 DB
                    userDAO.updateAvatar(u.getId(), publicUrl);

                    // 重新撈最新資料並更新 session
                    User fresh = userDAO.findById(u.getId());
                    req.getSession().setAttribute("user", fresh);
                    req.setAttribute("user", fresh);

                    if (ajax) {
                        returnJson(resp, 200, true, "已更新大頭貼", publicUrl);
                    } else {
                        req.setAttribute("msg", "已更新大頭貼");
                        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
                    }
                    return;

                } catch (Exception e) {
                    boolean ajaxErr = "1".equals(req.getParameter("ajax")) ||
                                      "fetch".equalsIgnoreCase(req.getHeader("X-Requested-With"));
                    if (ajaxErr) {
                        returnJson(resp, 400, false, "上傳失敗：" + e.getMessage(), null);
                    } else {
                        req.setAttribute("error", "上傳失敗：" + e.getMessage());
                        forwardProfile(req, resp, u);
                    }
                    return;
                }
            }

            // 其它 action：回個人資料頁
            forwardProfile(req, resp, u);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /* ==================== 小工具 ==================== */

    private void forwardProfile(HttpServletRequest req, HttpServletResponse resp, User u) throws ServletException, IOException {
        try {
            req.setAttribute("user", userDAO.findById(u.getId()));
        } catch (Exception ignored) {}
        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }

    private void returnJson(HttpServletResponse resp, int status, boolean ok, String message, String url) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json; charset=UTF-8");
        String msg = message == null ? "" : jsonEscape(message);
        String u = url == null ? null : jsonEscape(url);
        String body = "{\"ok\":" + ok + ",\"message\":\"" + msg + "\"" + (u != null ? ",\"url\":\"" + u + "\"" : "") + "}";
        resp.getWriter().write(body);
    }

    private static String jsonEscape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
