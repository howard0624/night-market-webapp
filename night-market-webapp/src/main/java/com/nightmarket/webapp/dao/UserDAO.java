
package com.nightmarket.webapp.dao;

import com.nightmarket.webapp.model.User;
import com.nightmarket.webapp.util.DB;

import java.sql.*;
import java.time.Instant;
import java.util.Base64;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

  // 依 email 取 user（登入或顯示）
  public User findByEmail(String email) throws Exception {
    String sql = "SELECT id,name,email,phone,avatar_url,is_verified,password_hash FROM users WHERE email=?";
    try (Connection c = DB.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, email);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return null;
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setAvatarUrl(rs.getString("avatar_url"));
        u.setIsVerified(rs.getInt("is_verified"));
        u.setPasswordHash(rs.getString("password_hash"));
        return u;
      }
    }
  }

  // 註冊：插入未驗證帳號 + 驗證 token
  public void insertForVerify(String name, String email, String passwordHash,
                              String token, Instant expires) throws Exception {
    String sql = "INSERT INTO users(name,email,password_hash,is_verified,verify_token,verify_expires,created_at) " +
                 "VALUES(?,?,?,0,?,?,NOW())";
    try (Connection c = DB.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, name);
      ps.setString(2, email);
      ps.setString(3, passwordHash);
      ps.setString(4, token);
      ps.setTimestamp(5, Timestamp.from(expires));
      ps.executeUpdate();
    }
  }

  // 驗證用：token 還有效就回 email
  public String findEmailByValidToken(String token) throws Exception {
    String sql = "SELECT email FROM users WHERE verify_token=? AND verify_expires>NOW()";
    try (Connection c = DB.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, token);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getString(1) : null;
      }
    }
  }

  // 驗證成功：設為已驗證、清 token
  public void markVerified(String email) throws Exception {
    String sql = "UPDATE users SET is_verified=1, verify_token=NULL, verify_expires=NULL, updated_at=NOW() WHERE email=?";
    try (Connection c = DB.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, email);
      ps.executeUpdate();
    }
  }

  // 重寄驗證信：產新 token 並回傳
  public String regenerateToken(String email, Instant expires) throws Exception {
    byte[] b = new byte[32];
    new SecureRandom().nextBytes(b);
    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(b);

    String sql = "UPDATE users SET verify_token=?, verify_expires=?, updated_at=NOW() WHERE email=?";
    try (Connection c = DB.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, token);
      ps.setTimestamp(2, Timestamp.from(expires));
      ps.setString(3, email);
      ps.executeUpdate();
    }
    return token;
  }

  // 會員資料更新（名稱/電話/頭像）
  public void updateName(long id, String name) throws Exception {
	    String sql = "UPDATE users SET name=?, updated_at=NOW() WHERE id=?";
	    try (Connection conn = DB.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, name);
	        ps.setLong(2, id);
	        ps.executeUpdate();
	    }
	}

  // 依 id 讀密碼雜湊（改密碼時用）
  public String findPasswordHashById(long id) throws Exception {
    try (Connection c = DB.getConnection();
         PreparedStatement ps = c.prepareStatement(
             "SELECT password_hash FROM users WHERE id=?")) {
      ps.setLong(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getString(1) : null;
      }
    }
  }

  public void updatePassword(long id, String newHash) throws Exception {
    try (Connection c = DB.getConnection();
         PreparedStatement ps = c.prepareStatement(
             "UPDATE users SET password_hash=?, updated_at=NOW() WHERE id=?")) {
      ps.setString(1, newHash);
      ps.setLong(2, id);
      ps.executeUpdate();
    }
  }

  // 忘記密碼：寫入 reset_token + 期限
  public void saveResetToken(String email, String token, Instant expires) throws Exception {
    String sql = "UPDATE users SET reset_token=?, reset_expires=?, updated_at=NOW() WHERE email=?";
    try (Connection c = DB.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, token);
      ps.setTimestamp(2, Timestamp.from(expires));
      ps.setString(3, email);
      ps.executeUpdate();
    }
  }

  public String findEmailByValidResetToken(String token) throws Exception {
    String sql = "SELECT email FROM users WHERE reset_token=? AND reset_expires>NOW()";
    try (Connection c = DB.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, token);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getString(1) : null;
      }
    }
  }

  public void clearResetToken(String email) throws Exception {
    try (Connection c = DB.getConnection();
         PreparedStatement ps = c.prepareStatement(
             "UPDATE users SET reset_token=NULL, reset_expires=NULL, updated_at=NOW() WHERE email=?")) {
      ps.setString(1, email);
      ps.executeUpdate();
    }
  }

  // 依 id 讀取會員（會員中心載入）
  public User findById(long id) throws Exception {
    String sql = "SELECT id,name,email,phone,avatar_url,is_verified FROM users WHERE id=?";
    try (Connection c = DB.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return null;
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setAvatarUrl(rs.getString("avatar_url"));
        u.setIsVerified(rs.getInt("is_verified"));
        return u;
      }
    }
  }
  private boolean isDuplicateKey(SQLException e) {
	    // MySQL/MariaDB 重複鍵：SQLState 23000、ErrorCode 1062
	    if ("23000".equals(e.getSQLState()) && e.getErrorCode() == 1062) return true;
	    // 某些 MariaDB 驅動會包在 cause
	    Throwable c = e.getCause();
	    return c instanceof java.sql.SQLIntegrityConstraintViolationException;
	}
  public boolean existsByEmail(String email) throws SQLException {
      final String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
      try (Connection conn = DB.getConnection();
           PreparedStatement ps = conn.prepareStatement(sql)) {
          ps.setString(1, email); // 若你有統一用小寫，可用 email.trim().toLowerCase()
          try (ResultSet rs = ps.executeQuery()) {
              return rs.next();    // 有資料就代表已存在
          }
      }
  }
  public void updateAvatar(long id, String avatarUrl) throws Exception {
	    String sql = "UPDATE users SET avatar_url=?, updated_at=NOW() WHERE id=?";
	    try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
	        ps.setString(1, avatarUrl);
	        ps.setLong(2, id);
	        ps.executeUpdate();
	    }
	}


}
