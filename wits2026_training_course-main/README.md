# WITS 2026 Training Course

這個工作區是 2026 訓練課程的總入口，內容依學習路徑拆成六個主題模組，從 Git 與協作開始，逐步進入 Java 後端、資料一致性、API 交付、舊系統維運，最後走到 Vue SPA 整合實務。

## 課程定位

這套課程不是把技術零散拆開教，而是以實務交付順序來安排：

1. 先建立版本控制與協作基礎。
2. 再建立 Java 後端專案啟動與部署能力。
3. 接著進入 SQL、JDBC、MyBatis 與交易控制。
4. 然後把後端能力包裝成可交付 API。
5. 再理解 JSP / jQuery 舊系統維運與專題啟動。
6. 最後整合到 Vue 3 + Vite + Quasar 的現代前台。

## 建議修課順序

| 順序 | 模組 | 主題焦點 | 交付能力 |
| --- | --- | --- | --- |
| 01 | [01_git-training-master](01_git-training-master) | Git 核心觀念、分支協作、衝突處理、遠端合作 | 能安全協作、提交、回溯與整合變更 |
| 02 | [02_spring_boot_maven_tomcat](02_spring_boot_maven_tomcat) | Spring Boot、Maven、Tomcat、Eclipse runtime | 能啟動、封裝、部署與理解 Java Web 專案骨架 |
| 03 | [03_sql_jdbc_mybatis_transaction](03_sql_jdbc_mybatis_transaction) | SQL、JDBC、MyBatis、交易控制、rollback | 能處理資料查詢正確性與交易一致性 |
| 04 | [04_rest_api_exception_swagger_jwt](04_rest_api_exception_swagger_jwt) | REST API、例外處理、OpenAPI、JWT、安全整合 | 能交付可測、可保護、可文件化的 API |
| 05 | [05_jsp_jquery_ajax_project_planning](05_jsp_jquery_ajax_project_planning) | JSP、jQuery、AJAX、舊系統維運、專題企劃 | 能理解 legacy 前台、補測試頁並啟動專題規劃 |
| 06 | [06_vue3_vite_quasar_spa_integration](06_vue3_vite_quasar_spa_integration) | Vue 3、Vite、Quasar、Router、Pinia、JWT 前台整合 | 能建立現代前台並與既有 API / JSP 並存 |

## 模組導覽

### Module 01

- 入口：[01_git-training-master/README.md](01_git-training-master/README.md)
- 重點：Git 基礎、branch strategy、conflict resolution、remote collaboration
- 適合對象：剛開始進入團隊協作、需要建立提交與合併基本功的學員

### Module 02

- 入口：[02_spring_boot_maven_tomcat/README.md](02_spring_boot_maven_tomcat/README.md)
- 重點：Spring Boot 專案骨架、Maven 打包、Tomcat runtime、IDE 設定
- 適合對象：需要把 Java 專案從原始碼帶到可執行、可部署狀態的學員

### Module 03

- 入口：[03_sql_jdbc_mybatis_transaction/README.md](03_sql_jdbc_mybatis_transaction/README.md)
- 重點：SQL 查詢思維、JDBC 底層流程、MyBatis 結構化、交易與 rollback
- 適合對象：要從 Controller / Service 走進資料層與一致性問題的學員

### Module 04

- 入口：[04_rest_api_exception_swagger_jwt/README.md](04_rest_api_exception_swagger_jwt/README.md)
- 重點：RESTful API、Global Exception、Postman、OpenAPI、JWT、CORS
- 適合對象：要把後端能力轉成前後端都能協作的可交付 API 的學員

### Module 05

- 入口：[05_jsp_jquery_ajax_project_planning/README.md](05_jsp_jquery_ajax_project_planning/README.md)
- 重點：JSP server-side rendering、jQuery / Prototype.js、AJAX、legacy 維運、project kickoff
- 適合對象：要理解企業舊系統、局部改造與專題啟動流程的學員

### Module 06

- 入口：[06_vue3_vite_quasar_spa_integration/README.md](06_vue3_vite_quasar_spa_integration/README.md)
- 重點：Vue 3、TypeScript、Router、Pinia、Quasar、SPA 與 JWT API 整合
- 適合對象：要建立現代前台並處理與既有 Spring Boot / JSP 並存問題的學員

## 如果你是學員

建議閱讀順序：

1. 先讀每個模組的頂層 README，理解課程目標與學習順序。
2. 再進入各模組下的 module README，按單元逐步閱讀。
3. 接著看 exercises，練習排查順序、修正理由與驗證方法。
4. 若模組內有 runnable demo，再回到 demo README 與程式碼對照操作。

## 如果你是講師

每個主題模組都已預留講師資料：

- [03_sql_jdbc_mybatis_transaction/instructor-guide](03_sql_jdbc_mybatis_transaction/instructor-guide)
- [04_rest_api_exception_swagger_jwt/instructor-guide](04_rest_api_exception_swagger_jwt/instructor-guide)
- [05_jsp_jquery_ajax_project_planning/instructor-guide](05_jsp_jquery_ajax_project_planning/instructor-guide)
- [06_vue3_vite_quasar_spa_integration/instructor-guide](06_vue3_vite_quasar_spa_integration/instructor-guide)

建議先看各模組的 instructor guide，再搭配 exercises 與 demo-script 安排授課節奏。

## 如果你要快速找範例

- Java 後端與部署示範：[02_spring_boot_maven_tomcat/spring-boot-demo](02_spring_boot_maven_tomcat/spring-boot-demo)
- SQL / JDBC / MyBatis runnable demo：[03_sql_jdbc_mybatis_transaction/jdbc-mybatis-demo](03_sql_jdbc_mybatis_transaction/jdbc-mybatis-demo)
- REST API / JWT runnable demo：[04_rest_api_exception_swagger_jwt/spring-rest-jwt-demo](04_rest_api_exception_swagger_jwt/spring-rest-jwt-demo)
- JSP / jQuery / Axios 示範頁：[05_jsp_jquery_ajax_project_planning/frontend-demo](05_jsp_jquery_ajax_project_planning/frontend-demo)
- Vue 3 / Quasar 前台骨架：[06_vue3_vite_quasar_spa_integration/frontend-demo](06_vue3_vite_quasar_spa_integration/frontend-demo)

## 工作區使用方式

這個工作區目前以單一 Git repo 管理，方便整套課程一起維護與版本控管。

- 工作區根目錄是唯一 Git 根目錄。
- 各課程資料夾是課程模組，不再視為獨立 Git 專案。
- 共通忽略規則集中在根層 [.gitignore](.gitignore)。

## 建議入口

- 想了解整體路線：本檔案
- 想開始第一門課：[01_git-training-master/README.md](01_git-training-master/README.md)
- 想直接看資料庫與交易控制：[03_sql_jdbc_mybatis_transaction/README.md](03_sql_jdbc_mybatis_transaction/README.md)
- 想直接看 API 與 JWT：[04_rest_api_exception_swagger_jwt/README.md](04_rest_api_exception_swagger_jwt/README.md)
- 想直接看 Vue 前台整合：[06_vue3_vite_quasar_spa_integration/README.md](06_vue3_vite_quasar_spa_integration/README.md)
