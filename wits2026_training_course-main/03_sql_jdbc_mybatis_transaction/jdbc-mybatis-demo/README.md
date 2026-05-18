# jdbc-mybatis-demo

> 用來示範 MySQL 8、JDBC、MyBatis 與 @Transactional 的最小可執行範例。

## 專案目標

這個範例專案刻意只聚焦兩件事：

1. 用 MyBatis 執行複雜 SQL 查詢保單財務摘要
2. 用 JDBC + @Transactional 示範理賠核准與失敗回滾

## 教材對照索引

- SQL 基礎與資料模型：`src/main/resources/sql/schema-mysql.sql`
- 初始化資料：`src/main/resources/sql/data-mysql.sql`
- JDBC repository：`src/main/java/com/company/training/jdbcdemo/repository/ClaimLedgerJdbcRepository.java`
- MyBatis mapper interface：`src/main/java/com/company/training/jdbcdemo/mapper/PolicyQueryMapper.java`
- MyBatis XML：`src/main/resources/mapper/PolicyQueryMapper.xml`
- 交易控制 service：`src/main/java/com/company/training/jdbcdemo/service/ClaimProcessingService.java`
- 測試與 rollback 驗證：`src/test/java/com/company/training/jdbcdemo/JdbcMybatisDemoApplicationTests.java`

## 建議教學順序

1. 先開 SQL schema 與 data，讓學員建立資料表心智模型。
2. 再看 mapper 與 XML，理解複雜查詢如何進入專案。
3. 接著看 JDBC repository，理解 framework 之下的底層動作。
4. 最後看 `ClaimProcessingService` 與 tests，理解 rollback 與故障驗證。

## 可延伸補強點

若要把這個 demo 當作進階教學範本，建議後續可補：

- dynamic SQL 查詢
- batch insert / update
- propagation / readOnly / timeout 測試案例
- 更完整的報表查詢與 explain 分析

## 範例路徑

- `GET /api/policies/POL20260001/financial-summary`
- `POST /api/claims/CLM20260511001/approve?approvedBy=trainer&failAfterLedger=false`
- `POST /api/claims/CLM20260511001/approve?approvedBy=trainer&failAfterLedger=true`

第三條路徑會故意在寫入 ledger 後拋出例外，方便教學時驗證 rollback。

## MySQL 8 設定

預設連線資訊在 `src/main/resources/application.properties`：

- database：`training_jdbc_demo`
- username：`root`
- password：`root`

可依本機環境自行調整。

## 初始化資料庫

請先執行：

- `src/main/resources/sql/schema-mysql.sql`
- `src/main/resources/sql/data-mysql.sql`

## 測試說明

專案測試使用 H2 in-memory database 驗證 Spring context、MyBatis 查詢與 rollback 行為，因此不需要先啟動 MySQL 才能跑 `mvn test`。