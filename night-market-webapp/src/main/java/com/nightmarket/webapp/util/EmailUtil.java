package com.nightmarket.webapp.util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Date;
import java.util.Properties;

/**
 * 寄信工具：讀環境變數，不再寫死帳密
 *
 * 支援環境變數：
 *   SMTP_HOST         預設 smtp.gmail.com
 *   SMTP_PORT         預設 587 (TLS)，若設 465 則走 SSL
 *   SMTP_USER         必填：寄件者帳號 (e.g. nightmarket.webapp@gmail.com)
 *   SMTP_PASS         必填：寄件者密碼 (⚠ Gmail 要用應用程式密碼)
 *   SMTP_FROM_NAME    選填：寄件人顯示名稱 (例如「寧夏夜市」)
 *   SMTP_DEBUG        選填：true/false，是否輸出寄信 log
 */
public class EmailUtil {

    /** 對外：寄驗證信 */
    public static void sendVerificationMail(String to, String verifyLink) throws MessagingException {
        String subject = "寧夏夜市｜帳號驗證";
        String html = "<p>您好，請於 24 小時內完成 Email 驗證：</p>"
                + "<p><a href=\"" + verifyLink + "\">點此完成驗證</a></p>"
                + "<p>若非本人操作，請忽略本郵件。</p>";
        sendHtml(to, subject, html);
    }

    /** 通用：寄 HTML 信 */
    public static void sendHtml(String to, String subject, String html) throws MessagingException {
        final String host = getenvOrDefault("SMTP_HOST", "smtp.gmail.com");
        final String port = getenvOrDefault("SMTP_PORT", "587");
        final String user = getenvOrDefault("SMTP_USER", null);
        final String pass = getenvOrDefault("SMTP_PASS", null);
        final String fromName = getenvOrDefault("SMTP_FROM_NAME", null);
        final boolean debug = "true".equalsIgnoreCase(getenvOrDefault("SMTP_DEBUG", "false"));

        if (isBlank(user) || isBlank(pass)) {
            throw new MessagingException("缺少寄信帳密：請設定 SMTP_USER / SMTP_PASS");
        }

        Properties p = new Properties();
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.connectiontimeout", "15000");
        p.put("mail.smtp.timeout", "15000");
        p.put("mail.smtp.host", host);
        p.put("mail.smtp.port", port);
        p.put("mail.smtp.ssl.trust", host);

        // SSL (465) 或 TLS (587)
        if ("465".equals(port)) {
            p.put("mail.smtp.socketFactory.port", "465");
            p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            p.put("mail.smtp.ssl.enable", "true");
        } else {
            p.put("mail.smtp.starttls.enable", "true");
        }

        Session session = Session.getInstance(p, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });
        session.setDebug(debug);

        MimeMessage msg = new MimeMessage(session);
        if (!isBlank(fromName)) {
            try {
                msg.setFrom(new InternetAddress(user, fromName, "UTF-8"));
            } catch (Exception e) {
                msg.setFrom(new InternetAddress(user)); // fallback
            }
        } else {
            msg.setFrom(new InternetAddress(user));
        }
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        msg.setSubject(subject, "UTF-8");
        msg.setSentDate(new Date());
        msg.setContent(html, "text/html; charset=UTF-8");

        Transport.send(msg);
    }

    /* -------------------- helpers -------------------- */
    private static String getenvOrDefault(String key, String def) {
        String val = System.getenv(key);
        return (val != null && !val.trim().isEmpty()) ? val : def;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
