# spring-rest-jwt-demo

> 用來示範登入拿 token、基本 JWT 驗證、全域例外處理、Swagger UI、OpenAPI 檔案與 Postman 驗收流程的最小可執行範例。

## 專案目標

這個範例專案刻意聚焦五件事：

1. `POST /api/auth/login` 取得 JWT
2. 受保護 API 需帶 Bearer Token 才能存取
3. 錯誤回應使用統一格式
4. Swagger UI 與標準 OpenAPI YAML 同時存在
5. Postman collection 可直接驗收主要流程

## 教材對照索引

- Auth API：`src/main/java/com/company/training/restjwtdemo/controller/AuthController.java`
- Protected API：`src/main/java/com/company/training/restjwtdemo/controller/InsuranceApiController.java`
- Health check：`src/main/java/com/company/training/restjwtdemo/controller/HealthController.java`
- Error DTO：`src/main/java/com/company/training/restjwtdemo/dto/ApiErrorResponse.java`
- Auth DTO：`src/main/java/com/company/training/restjwtdemo/dto/LoginRequest.java`、`src/main/java/com/company/training/restjwtdemo/dto/AuthTokenResponse.java`
- Policy / Claim DTO：`src/main/java/com/company/training/restjwtdemo/dto/PolicySummaryResponse.java`、`src/main/java/com/company/training/restjwtdemo/dto/ClaimStatusResponse.java`
- Security 與 CORS：`src/main/java/com/company/training/restjwtdemo/config/SecurityConfig.java`
- OpenAPI：`src/main/resources/static/openapi/openapi.yaml`
- Postman：`postman/training-rest-jwt-demo.collection.json`

## 建議教學順序

1. 先從 `HealthController` 說明應用啟動與 API 存活檢查。
2. 再看 `AuthController`，建立登入與 token 流程。
3. 接著看 `InsuranceApiController`，理解受保護 API 如何設計。
4. 然後比對 DTO、OpenAPI 與 Postman，說明契約同步的重要性。
5. 最後回到 `SecurityConfig`，講解 JWT 保護、401 / 403 與 CORS。

## 建議延伸補強點

若要把這個 demo 當成進階教材，可再延伸：

- pagination / filtering API
- business exception 與 validation exception 分流
- requestId / traceId 產生與回傳
- idempotency key 或 duplicate create 防呆
- token refresh 與前端自動重登入策略

## 驗證路徑

- `GET /api/health`
- `POST /api/auth/login`
- `GET /api/policies/POL20260001/summary`
- `GET /api/claims/CLM20260511001/status`
- `GET /legacy-demo/jquery-ajax-tester.html`
- `GET /legacy-demo/axios-api-tester.html`
- `GET /swagger-ui/index.html`
- `GET /openapi/openapi.yaml`

## 預設登入帳號

- username：`trainer`
- password：`P@ssw0rd123`

## Postman 資產

- `postman/training-rest-jwt-demo.collection.json`
- `postman/training-rest-jwt-demo.local.environment.json`

登入 request 會把 token 寫回 environment，後續受保護 API 可直接驗收。

## 同源前端示範

本專案另外提供兩個靜態頁面，給 05 課程示範舊系統維運時如何補前端測試頁：

- `legacy-demo/jquery-ajax-tester.html`
- `legacy-demo/axios-api-tester.html`

兩者都由同一個 Spring Boot 應用提供，因此可直接用相對路徑呼叫 JWT API，不需要另外處理 CORS。