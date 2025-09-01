package com.nightmarket.webapp.service;

import com.nightmarket.webapp.util.DB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

@WebServlet("/dbping")
public class DBPingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            try (Connection conn = DB.getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    out.println("✅ Database connection successful!");
                } else {
                    out.println("❌ Database connection failed!");
                }
            } catch (Exception e) {
                out.println("❌ Error: " + e.getMessage());
                e.printStackTrace(out);
            }
        }
    }
}
