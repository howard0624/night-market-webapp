package com.nightmarket.webapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    // 預設值：本機 MariaDB
    private static final String DEFAULT_URL  = "jdbc:mariadb://127.0.0.1:3306/nightmarket?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Taipei";
    private static final String DEFAULT_USER = "howard990113";
    private static final String DEFAULT_PASS = "123456789";

    public static Connection getConnection() throws SQLException {
        // 雲端部署時，優先讀環境變數 DB_URL / DB_USER / DB_PASS
        String url  = System.getenv().getOrDefault("DB_URL", DEFAULT_URL);
        String user = System.getenv().getOrDefault("DB_USER", DEFAULT_USER);
        String pass = System.getenv().getOrDefault("DB_PASS", DEFAULT_PASS);

        try {
            Class.forName("org.mariadb.jdbc.Driver");  // 使用 MariaDB Driver
        } catch (ClassNotFoundException e) {
            throw new SQLException("MariaDB Driver not found", e);
        }

        return DriverManager.getConnection(url, user, pass);
    }
}
