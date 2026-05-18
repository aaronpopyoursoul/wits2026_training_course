# SQL / JDBC / MyBatis / Transaction 課程

> 課程資料夾：03_sql_jdbc_mybatis_transaction  
> 對象：已完成基礎 Spring Boot 訓練，準備進入資料存取與交易控制的 Java 後端工程師  
> 技術基線：Java 8、Spring Boot 2.7.x、MySQL 8、JDBC、MyBatis、MyBatis Plus

## 課程簡介

這門課把 SQL、JDBC、MyBatis 與交易控制放在同一條學習路徑中處理，目的不是讓學員只會寫查詢或加上 `@Transactional`，而是能判斷資料為什麼會算錯、寫一半、回滾失敗，並知道該從哪一層開始排查。

課程主線以保單、保費與理賠場景為例，讓學員從資料模型、查詢設計、DAO 組織方式一路走到交易一致性與故障驗證。

## 這份資料夾適合誰

- 需要把 SQL 與 Java 資料存取整合進 Spring Boot 專案的人
- 想理解 JDBC 與 ORM / Mapper 之間責任邊界的人
- 想釐清 `@Transactional` 成功與失敗條件的人
- 講師、自學者、AI 代理需要快速掌握本課內容的人

## 你可以從這裡開始

### 1. 想先理解整門課在教什麼

- 讀各模組索引與學習順序：本文件
- 看練習設計與評核方式：[exercises/README.md](exercises/README.md)
- 看講師教學節奏：[instructor-guide/README.md](instructor-guide/README.md)

### 2. 想先跑可執行範例

- 進入 runnable demo：[jdbc-mybatis-demo/README.md](jdbc-mybatis-demo/README.md)
- 先建立 MySQL schema 與測試資料：
  - [jdbc-mybatis-demo/src/main/resources/sql/schema-mysql.sql](jdbc-mybatis-demo/src/main/resources/sql/schema-mysql.sql)
  - [jdbc-mybatis-demo/src/main/resources/sql/data-mysql.sql](jdbc-mybatis-demo/src/main/resources/sql/data-mysql.sql)
- 再啟動 Spring Boot 專案：`mvn spring-boot:run`

### 3. 想讓 AI 協助維護或補教材

- GitHub Copilot 協作規範：[.github/copilot-instructions.md](.github/copilot-instructions.md)
- 通用代理入口說明：[AGENTS.md](AGENTS.md)

## 快速啟動

### 環境需求

- Java 8
- Maven 3.8+
- MySQL 8

### Demo 啟動方式

1. 建立資料庫 `training_jdbc_demo`
2. 執行初始化 SQL
3. 進入 `jdbc-mybatis-demo`
4. 執行 `mvn spring-boot:run`

預設設定如下：

- Port：`8081`
- DB：`jdbc:mysql://localhost:3306/training_jdbc_demo`
- User / Password：`root` / `root`

設定檔位置：

- [jdbc-mybatis-demo/src/main/resources/application.properties](jdbc-mybatis-demo/src/main/resources/application.properties)

### 測試方式

在 `jdbc-mybatis-demo` 目錄執行：

- `mvn test`

測試使用 H2 in-memory database，不需要先啟動 MySQL。測試環境設定如下：

- [jdbc-mybatis-demo/src/test/resources/application.properties](jdbc-mybatis-demo/src/test/resources/application.properties)
- [jdbc-mybatis-demo/src/test/resources/schema-test.sql](jdbc-mybatis-demo/src/test/resources/schema-test.sql)
- [jdbc-mybatis-demo/src/test/resources/data-test.sql](jdbc-mybatis-demo/src/test/resources/data-test.sql)

## 學習重點

- SQL 決定查詢與更新是否正確
- JDBC 決定你如何控制 connection、commit、rollback
- MyBatis 決定 SQL 如何被收斂進專案結構
- Transaction 決定跨多筆資料更新時是否保持一致

如果這四塊分開學，學員通常只會記得語法或註解，不知道故障時該從 SQL、例外型別、交易邊界還是呼叫路徑開始查。

## 建議學習順序

1. 先學 SQL 與資料模型思維
2. 再看 JDBC 查詢流程與手動交易控制
3. 接著看 MyBatis 與 MyBatis Plus 如何組織資料層
4. 最後再進入 `@Transactional`、propagation、rollback 判讀與觀測

## 模組地圖

| 目錄 | 重點 |
| --- | --- |
| [module-01-sql-foundations](module-01-sql-foundations) | SQL 語法、join、group by、資料模型與商業規則連結 |
| [module-02-jdbc-and-transaction](module-02-jdbc-and-transaction) | JDBC 基礎、手動 transaction、commit / rollback |
| [module-03-mybatis-integration](module-03-mybatis-integration) | Mapper、XML、SQL 結構化與資料層切分 |
| [module-04-transactional-deep-dive](module-04-transactional-deep-dive) | `@Transactional` 失效場景、rollback 條件、self-invocation |
| [module-05-sql-performance-and-edge-cases](module-05-sql-performance-and-edge-cases) | 索引、explain、NULL / duplicate / reconciliation edge cases |
| [module-06-mybatis-plus-and-dynamic-sql](module-06-mybatis-plus-and-dynamic-sql) | MyBatis Plus 邊界、dynamic SQL、batch patterns |
| [module-07-transaction-propagation-and-observability](module-07-transaction-propagation-and-observability) | propagation、readOnly、timeout、交易觀測與排查 |
| [exercises](exercises) | 故障情境題、驗收方式與參考方向 |
| [instructor-guide](instructor-guide) | 講師節奏、示範腳本與常見錯誤 |
| [jdbc-mybatis-demo](jdbc-mybatis-demo) | Spring Boot 可執行範例與測試 |

## Demo 重要路徑

- Demo 導覽：[jdbc-mybatis-demo/README.md](jdbc-mybatis-demo/README.md)
- MySQL schema：[jdbc-mybatis-demo/src/main/resources/sql/schema-mysql.sql](jdbc-mybatis-demo/src/main/resources/sql/schema-mysql.sql)
- 初始化資料：[jdbc-mybatis-demo/src/main/resources/sql/data-mysql.sql](jdbc-mybatis-demo/src/main/resources/sql/data-mysql.sql)
- JDBC repository：[jdbc-mybatis-demo/src/main/java/com/company/training/jdbcdemo/repository/ClaimLedgerJdbcRepository.java](jdbc-mybatis-demo/src/main/java/com/company/training/jdbcdemo/repository/ClaimLedgerJdbcRepository.java)
- MyBatis mapper：[jdbc-mybatis-demo/src/main/java/com/company/training/jdbcdemo/mapper/PolicyQueryMapper.java](jdbc-mybatis-demo/src/main/java/com/company/training/jdbcdemo/mapper/PolicyQueryMapper.java)
- MyBatis XML：[jdbc-mybatis-demo/src/main/resources/mapper/PolicyQueryMapper.xml](jdbc-mybatis-demo/src/main/resources/mapper/PolicyQueryMapper.xml)
- 交易控制 service：[jdbc-mybatis-demo/src/main/java/com/company/training/jdbcdemo/service/ClaimProcessingService.java](jdbc-mybatis-demo/src/main/java/com/company/training/jdbcdemo/service/ClaimProcessingService.java)

## 範例 API

- `GET /api/policies/{policyId}/financial-summary`
- `POST /api/claims/{claimId}/approve?approvedBy=trainer&failAfterLedger=false`
- `POST /api/claims/{claimId}/approve?approvedBy=trainer&failAfterLedger=true`

第三個 API 會在 ledger 寫入後故意丟出例外，用來示範 rollback 是否真的發生。

## 常見誤區

- 只背 SQL 語法，不先理解資料表關聯
- 把複雜查詢塞進 service，導致 SQL 難以維護
- 以為 `@Transactional` 一加就一定會回滾
- 忽略 checked exception 與 runtime exception 的差異
- 用 MyBatis Plus 快速開發後，就停止思考 SQL 成本與查詢計畫

## 文件導覽

- 課程總覽：本文件
- 練習導覽：[exercises/README.md](exercises/README.md)
- 講師導覽：[instructor-guide/README.md](instructor-guide/README.md)
- Demo 導覽：[jdbc-mybatis-demo/README.md](jdbc-mybatis-demo/README.md)
- 各模組教材：各 `module-*` 目錄下 README

## AI 協作文件

- Copilot 指南：[.github/copilot-instructions.md](.github/copilot-instructions.md)
- 通用代理說明：[AGENTS.md](AGENTS.md)

AI 代理在修改本資料夾時，應優先連結既有教材，而不是把模組內容重複拷貝到新文件中。

## 與後續課程的銜接

完成本課後，學員可進入 04 的 REST API、全域例外處理、Swagger / OpenAPI 與 JWT。到了那一階段，這裡建立的資料一致性與交易控制能力，會直接成為 API 可交付性的基礎。