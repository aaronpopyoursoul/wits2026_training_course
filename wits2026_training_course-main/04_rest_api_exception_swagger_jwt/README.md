# REST API / Exception / Swagger / JWT 課程

> 課程資料夾：04_rest_api_exception_swagger_jwt
> 對象：已完成 03 課程，準備把資料存取層包成可交付 API 的 Java 後端工程師
> 技術基線：Java 8、Spring Boot 2.7.x、Spring MVC、Postman、OpenAPI 3、JWT

## 課程定位

這門課把 03 的資料層能力往上接到 API 交付層，重點是讓學員能交出一個具備錯誤處理、文件化、身分驗證與本地測試證據的 REST API 模組。

很多學員在這個階段會卡在兩種誤區：

1. 以為 Controller 能回 JSON 就算完成 API。
2. 以為 Swagger UI 會開就算完成交付。

這門課要修正的，就是這兩種過度簡化的理解。真正可交付的 API，必須同時可讀、可測、可追、可保護。

## 為什麼這些主題要放在同一課

以下主題本質上屬於同一條 API 交付鏈：

- RestController
- 除錯及例外處理
- Postman
- API 文件化（Swagger / OpenAPI）
- 資安與身分驗證（JWT）

原因是，真正可交付的 API 不只是能回 200，還必須同時滿足：

1. 回應格式清楚。
2. 錯誤有一致結構。
3. 文件能被前後端與測試共同理解。
4. 介面有最基本的驗證與授權保護。
5. 本地端能用 Postman 重現主要流程。

## 你會學到什麼

- 能從業務需求設計 RESTful 路徑與資源模型
- 能把資料層例外整理成一致的 API 錯誤回應
- 能用 Postman 建立可重複執行的驗收流程
- 能維護 OpenAPI 文件，讓前後端與測試對齊
- 能完成登入、取得 token、帶 token 呼叫受保護 API 的最小閉環

## 建議先備知識

- 已完成 03 課程，知道 service / repository 分層與交易控制
- 理解 HTTP method 基本用途
- 知道 JSON 與 DTO 的基本概念
- 知道 401、403、404、500 代表什麼大方向

## 本週學習目標

- 能設計清楚的 RESTful API 路徑與回應
- 能實作 Global Exception Handler
- 能用 Postman 建立基本測試集合
- 能產出 Swagger UI / OpenAPI 文件
- 能完成 JWT 驗證流程與受保護 API

## 交付標準

1. 有一組可執行的 REST API。
2. 有統一的錯誤回應格式。
3. 有可開啟的 Swagger UI 或 OpenAPI 文件。
4. 有至少一組 JWT 保護路由。
5. 有本地端 Postman 驗證紀錄。
6. 文件能描述登入與受保護 API 的測試順序。

## 新版模組地圖

這一輪把 04 從 4 個大主題拆成 7 個更細的模組，目的是讓學員能明確區分 API 設計、錯誤處理、文件交付、認證授權與跨前端整合，而不是把這些能力混成單一「Controller 課」。

- module-01-restcontroller-design：RESTful API 與回應設計
- module-02-debugging-and-global-exception：除錯、錯誤碼與全域例外處理
- module-03-postman-and-openapi：Postman 測試、OpenAPI 與文件同步
- module-04-jwt-authentication：JWT 登入、授權與安全回應
- module-05-api-contract-and-versioning：API contract、版本管理、pagination / filtering / idempotency
- module-06-security-exception-and-cors-integration：401 / 403 處理、CORS、前後端分離整合
- module-07-observability-and-delivery-readiness：requestId、日誌、驗收流程與交付檢查
- exercises：故障情境練習、解題方向與完整參考答案
- instructor-guide：講師版教案、示範腳本與常見誤區
- spring-rest-jwt-demo：Runnable demo 與檔案對照索引

## 預估學習時數

- 主教材閱讀與授課：8-10 小時
- exercises 與參考答案演練：4-6 小時
- Postman / Swagger / JWT / CORS 實作驗證：4-6 小時

## 可直接對照的程式檔

- [spring-rest-jwt-demo/README.md](spring-rest-jwt-demo/README.md)：demo 導覽與教學路徑
- [spring-rest-jwt-demo/src/main/java/com/company/training/restjwtdemo/controller/AuthController.java](spring-rest-jwt-demo/src/main/java/com/company/training/restjwtdemo/controller/AuthController.java)：登入 API
- [spring-rest-jwt-demo/src/main/java/com/company/training/restjwtdemo/controller/InsuranceApiController.java](spring-rest-jwt-demo/src/main/java/com/company/training/restjwtdemo/controller/InsuranceApiController.java)：受保護 API
- [spring-rest-jwt-demo/src/main/java/com/company/training/restjwtdemo/dto/ApiErrorResponse.java](spring-rest-jwt-demo/src/main/java/com/company/training/restjwtdemo/dto/ApiErrorResponse.java)：統一錯誤格式
- [spring-rest-jwt-demo/src/main/java/com/company/training/restjwtdemo/config/SecurityConfig.java](spring-rest-jwt-demo/src/main/java/com/company/training/restjwtdemo/config/SecurityConfig.java)：Security 與 CORS
- [spring-rest-jwt-demo/src/main/resources/static/openapi/openapi.yaml](spring-rest-jwt-demo/src/main/resources/static/openapi/openapi.yaml)：OpenAPI 文件
- [spring-rest-jwt-demo/postman/training-rest-jwt-demo.collection.json](spring-rest-jwt-demo/postman/training-rest-jwt-demo.collection.json)：Postman 驗收流程

## 本輪補強主題

相較第一輪版本，這次特別補強幾個真實專案常缺漏的點：

- 分頁、條件查詢與 API contract drift
- requestId / traceability 與交付可追蹤性
- 401 / 403 / validation / business exception 的分流
- Postman 驗收腳本與 OpenAPI 同步管理
- CORS 與前後端分離串接
- idempotency 與 API 版本演進思路

## 建議授課主線

建議沿著以下流程帶課：

1. 先用保單查詢與理賠案件查詢設計 API。
2. 再處理錯誤格式，讓 API 在失敗時也具一致性。
3. 接著加入 Postman 與 OpenAPI，讓介面可被驗證與共享。
4. 最後把登入與 JWT 保護補上，完成最小可交付安全 API。

## 範例閉環

### Step 1：設計查詢 API

```http
GET /api/policies/{policyNo}
```

### Step 2：設計建立理賠 API

```http
POST /api/claims
Content-Type: application/json

{
	"policyNo": "PL20240001",
	"claimAmount": 3000,
	"reason": "outpatient"
}
```

### Step 3：統一成功回應與錯誤回應

```json
{
	"requestId": "REQ-20240401-001",
	"data": {
		"policyNo": "PL20240001",
		"status": "ACTIVE"
	}
}
```

```json
{
	"requestId": "REQ-20240401-002",
	"errorCode": "POLICY_NOT_FOUND",
	"message": "policy not found",
	"path": "/api/policies/PL99999999"
}
```

### Step 4：用 Postman 驗證

至少要驗證：

- 查詢成功
- 查詢不存在資料
- 未登入呼叫受保護 API
- 登入後帶 token 呼叫成功

## 常見誤區

- 路徑設計混入動詞與 action 參數，導致 API 難以理解。
- 錯誤時直接回傳 exception message，沒有統一結構。
- OpenAPI 文件與實際欄位不同步。
- JWT 只做到簽發 token，沒驗證保護路由。
- Postman 只存成功案例，沒有保存失敗案例。

## 練習與解答方向

### 練習題 1

設計保單查詢與理賠建立兩條 API，寫出 request / response DTO。

解答方向：

- 路徑要反映資源語意，不要用 `/doQueryPolicy` 這種命名。
- DTO 要明確分 request 與 response，不要直接把 entity 往外回。

### 練習題 2

為理賠建立流程加入全域例外處理與錯誤碼。

解答方向：

- 至少要區分輸入錯誤、查無資料、系統錯誤三類。
- 回應中最好保留 `requestId` 或 `traceId`。

### 練習題 3

完成登入 API、JWT 驗證與 Postman 驗收流程。

解答方向：

- 登入成功後要將 token 存進 environment 或 collection variable。
- 驗收不是只有拿到 token，還要證明 token 可以保護 API。

### 練習題 4

設計一題「Swagger 與實際 DTO 欄位不一致」的情境，說明如何定位問題並修正。

解答方向：

- 先比對 controller / dto / openapi 文件，不要先怪 Swagger UI。
- 修正後要同步驗證 Postman 與文件輸出。

### 練習題 5

設計一題 Vue 前端呼叫 API 時發生 CORS preflight 失敗的情境，說明排查順序與修正方式。

解答方向：

- 先確認是瀏覽器層的 CORS，還是 JWT / 401 問題。
- 再檢查 allowed origin、allowed methods、allowed headers 與 credentials。

### 練習題 6

設計一題同一支建立 API 被重送兩次造成重複資料的情境，說明 idempotency 的處理方向。

解答方向：

- 要把「HTTP 能打通」與「業務上可安全重送」分開看。
- 可以從 request key、商業鍵或狀態檢查設計切入。

## 與前一門課的銜接

03 決定資料是否正確與可回滾，04 決定這些資料能力能否被包裝成安全、可測、可交付的 API。這兩門課必須連著看，否則學員很容易只會寫 Controller，不知道資料一致性與驗證風險。