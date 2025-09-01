# 寧夏夜市智慧管理與數據分析系統（Eclipse Maven Webapp）

## 快速開始
1. 安裝 JDK 11、Maven、Tomcat 9。
2. MySQL 建立資料庫：
   ```sql
   SOURCE db/schema.sql;
   ```
3. Eclipse 匯入：`File > Import > Maven > Existing Maven Projects`，選擇此資料夾。
4. 在 Eclipse 建 Tomcat 9 Server，將本專案加入。
5. 設定 DB 連線（擇一）：
   - 在執行設定加入環境變數：`DB_URL`、`DB_USER`、`DB_PASS`
   - 或直接使用預設：`jdbc:mysql://localhost:3306/nightmarket`、`root`、空密碼。
6. Run on Server，瀏覽：`http://localhost:8080/night-market-webapp/`

## 功能
- 註冊、登入
- 簡易首頁與健康檢查 API `/api/health`

## 專案結構
- `src/main/webapp`：JSP 與靜態資源
- `src/main/java`：Servlet、Service、DAO、Model
- `db/schema.sql`：資料表

