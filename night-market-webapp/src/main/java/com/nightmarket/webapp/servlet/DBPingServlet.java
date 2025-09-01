package com.nightmarket.webapp.servlet;

import com.nightmarket.webapp.util.DB;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.Connection;

public class DBPingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain; charset=UTF-8");
        try (Connection c = DB.getConnection()) {
            resp.getWriter().println("DB Connected");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().println("DB Failed: " + e.getMessage());
        }
    }
}
