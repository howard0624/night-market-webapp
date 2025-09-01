package com.nightmarket.webapp.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/member/*"})
public class AuthFilter implements Filter {
  @Override public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest r = (HttpServletRequest) req;
    HttpServletResponse p = (HttpServletResponse) resp;
    HttpSession session = r.getSession(false);

    if (session == null || session.getAttribute("user") == null) {
      r.setAttribute("error", "請先登入。");
      r.getRequestDispatcher("/login.jsp").forward(r, p);
      return;
    }
    chain.doFilter(req, resp);
  }
}
