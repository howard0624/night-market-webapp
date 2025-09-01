package com.nightmarket.webapp.service;

import com.nightmarket.webapp.dao.UserDAO;
import com.nightmarket.webapp.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String plainPassword) throws Exception {
        User u = userDAO.findByEmail(email);
        if (u == null) return null;

        String hash = u.getPasswordHash(); // 從 DB 取出的 BCrypt 雜湊
        if (hash == null || hash.isEmpty()) return null;

        boolean ok = BCrypt.checkpw(plainPassword, hash);
        return ok ? u : null;
    }
}
