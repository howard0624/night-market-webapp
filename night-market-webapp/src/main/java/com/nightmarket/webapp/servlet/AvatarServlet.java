package com.nightmarket.webapp.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.*;

@WebServlet("/u/*")
public class AvatarServlet extends HttpServlet {
    private Path root;

    @Override
    public void init() throws ServletException {
        root = Paths.get(System.getProperty("catalina.base"), "night-market-uploads", "avatars");
        try { Files.createDirectories(root); } catch (IOException ignored) {}
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String p = req.getPathInfo(); // /u/<filename>
        if (p == null || p.length() <= 1) { resp.sendError(404); return; }
        String name = p.substring(1);
        if (!name.matches("[A-Za-z0-9_.-]+")) { resp.sendError(404); return; }

        Path file = root.resolve(name).normalize();
        if (!file.startsWith(root) || !Files.exists(file)) { resp.sendError(404); return; }

        String ct = Files.probeContentType(file);
        if (ct == null) ct = "application/octet-stream";
        resp.setContentType(ct);
        resp.setHeader("Cache-Control", "public, max-age=86400");

        try (OutputStream out = resp.getOutputStream()) {
            Files.copy(file, out);
        }
    }
}
