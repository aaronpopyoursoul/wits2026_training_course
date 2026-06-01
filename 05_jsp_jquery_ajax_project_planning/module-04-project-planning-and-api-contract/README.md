# Module 04 — 專題啟動、題目定義與 API 契約

## 模組目標

這一模組的關鍵，是讓學員知道專題不是先寫首頁，而是先回答這幾個問題：

- 做的是哪個商業問題
- 前後端如何分工
- API 由誰定義與驗收
- 是否保留 JSP、是否導入前後端分離、是否需要 JWT

## 核心重點

- 專題題目定義
- 團隊分工
- 系統架構選型
- API 契約先行
- 里程碑與風險盤點

## 情境說明

很多學員一聽到專題，就先想首頁長什麼樣、用哪個框架、要不要做動畫。但企業專案真正先失控的地方，通常不是畫面，而是：

- 題目太大
- 角色不清
- API 規格沒人定
- 驗收標準不存在

所以這一模組的目的，是把專題從「想做什麼」拉回「怎麼交付」。

## 專題啟動時要先問的問題

1. 這個題目解決哪個實際流程問題。
2. 使用者是誰，角色有哪些。
3. 現有系統有哪些可以沿用，哪些一定要重做。
4. API 契約要先定哪些核心欄位。
5. 驗收標準由誰決定。

### Step 1：題目定義先於技術選型

這一段要明講：

- 不是先決定 Vue / jQuery / JSP
- 而是先決定要解決哪個流程問題

例如保險場景可先問：

- 是查詢流程太慢？
- 是補件流程太亂？
- 是理賠進度資訊不透明？

當問題定清楚後，才知道哪些頁面保留、哪些功能 API 化。

## 題目定義示例

### 題目

理賠進度查詢與文件補件平台

### 商業問題

- 客戶無法即時知道理賠進度
- 內勤需重複接聽相同查詢電話
- 補件流程缺乏標準化欄位與追蹤機制

### 主要角色

- 保戶
- 理賠內勤
- 理賠主管

### Step 2：角色與流程不清，API 契約就會漂移

若不先定角色，很容易出現：

- 保戶看的欄位和內勤看的欄位混在一起
- 前端與後端對同一 API 的用途理解不同
- 驗收時才發現畫面與流程根本不匹配

## 架構選型討論方式

不要只列出「Spring Boot + Vue + MySQL」這種工具清單，而是要回答：

- 為什麼查詢頁先保留 JSP
- 為什麼補件上傳功能要先做 API
- 為什麼某些維運頁仍用 jQuery 比全面重寫成本低

### Step 3：架構選型要能解釋「為什麼現在先這樣做」

這是 05 很核心的觀念。舊系統專案不是永遠追求最現代，而是要能回答：

- 目前最划算的切法是什麼
- 風險最低的過渡路徑是什麼
- 哪些功能值得先 API 化

這樣自然會連到 06 的 Vue + JSP 漸進式整合。

## API 契約草案示例

以下是一份接近實際交付的 OpenAPI 3.0 YAML，包含 request body、response schema 和防御表示範例：

```yaml
openapi: "3.0.3"
info:
  title: 保全整合平台 API
  version: "1.0.0"
servers:
  - url: http://localhost:8082

paths:
  /api/auth/login:
    post:
      summary: 使用者登入
      tags: [Auth]
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required: [username, password]
              properties:
                username:
                  type: string
                  example: admin
                password:
                  type: string
                  example: "123456"
      responses:
        "200":
          description: 登入成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  token:
                    type: string
                    example: eyJhbGciOiJIUzI1NiJ9...
        "401":
          description: 帳號或密碼錯誤

  /api/claims/{claimNo}:
    get:
      summary: 查詢理賠案件狀態
      tags: [Claims]
      security:
        - bearerAuth: []
      parameters:
        - name: claimNo
          in: path
          required: true
          schema:
            type: string
          example: CLM20240001
      responses:
        "200":
          description: 成功
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/ClaimDetail"
        "401":
          description: 未登入
        "404":
          description: 查無此理賠案件

  /api/claims/{claimNo}/documents:
    post:
      summary: 新增補件資訊
      tags: [Claims]
      security:
        - bearerAuth: []
      parameters:
        - name: claimNo
          in: path
          required: true
          schema:
            type: string
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required: [docType, docUrl]
              properties:
                docType:
                  type: string
                  enum: [ID_CARD, MEDICAL_CERTIFICATE, DIAGNOSIS_REPORT]
                docUrl:
                  type: string
                  example: https://storage.example.com/doc/abc.pdf
                remark:
                  type: string
      responses:
        "201":
          description: 補件建立成功
        "400":
          description: 請求格式錯誤
        "404":
          description: 查無此理賠案件

components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

  schemas:
    ClaimDetail:
      type: object
      properties:
        claimNo:
          type: string
        status:
          type: string
          enum: [SUBMITTED, REVIEWING, APPROVED, REJECTED]
        submittedAt:
          type: string
          format: date-time
        documents:
          type: array
          items:
            type: object
            properties:
              docType:
                type: string
              docUrl:
                type: string
              uploadedAt:
                type: string
                format: date-time
```

此 YAML 可直接貼入 Swagger Editor（swagger.io/editor）預覽或交給 Spring Boot（springdoc-openapi）讀取。

#### 里程碑與走期清單範例

專題啟動文件应包含一張可操作的里程碑表，不能只有日期：

| 里程碑 | 日期 | 交付物 | 狀態 |
|---|---|---|---|
| M0 Kickoff | Week 1 | 啟動文件、視圖名稱對照表 | 待辦 |
| M1 API 契約凍結 | Week 2 | OpenAPI YAML + Postman collection | 待辦 |
| M2 核心配件 | Week 3–4 | 前後端基礎整合可通 | 待辦 |
| M3 功能完成 | Week 5–6 | 主要流程可展示 | 待辦 |
| M4 驗收交付 | Week 7 | 進行骗收測試、評分 | 待辦 |

### Step 4：API 契約至少要先定四件事

- 路徑與 method
- request 欄位
- response 欄位
- 錯誤格式

如果只寫 `200 success`，其實對前後端幫助很小。

### Step 5：里程碑不是排日期而已，而是排風險釋放順序

建議教學員把 milestone 想成：

- 先把不確定性高的東西確認
- 先把 API 契約定住
- 再做頁面與互動

這比一開始就全面鋪開開發更穩。

## 里程碑建議

### Milestone 1

- 完成題目定義
- 完成角色與流程圖
- 完成 API 清單初版

### Milestone 2

- 完成後端核心 API
- 完成 Postman 驗證
- 完成基本 JWT 保護

### Milestone 3

- 完成 JSP / jQuery / Axios 消費頁面
- 完成示範流程
- 完成驗收文件

## 常見錯誤

- 一開始只討論畫面，不先定義流程與 API。
- 題目太大，沒有切出 MVP。
- 團隊分工只分前端後端，沒有定義接口責任人。
- API 規格晚於程式開發，導致前後端反覆重工。
- 題目描述只有功能清單，沒有商業問題與成功條件。
- 架構選型只列工具名稱，沒有說明原因。

## 自我檢查清單

- 我能用一句話說清楚專題在解什麼問題嗎？
- 我知道誰是使用者、誰是 API owner、誰負責驗收嗎？
- 我能說明為什麼某功能先保留 JSP，某功能先做 API 嗎？
- 我能交出至少一版可討論的 API 契約草案嗎？

## 練習題

1. 為一個保險維運題目寫出商業場景、角色與範圍。
2. 畫出初步系統架構與資料流。
3. 先寫 API 契約，再說明畫面要怎麼接。
4. 定義一個三階段 milestone 與每階段驗收標準。

## 練習解答方向

1. 題目要能在課程時間內完成，不宜過大。
2. 架構圖至少要標出前端頁面、後端 API、資料庫三塊。
3. API 契約要先定核心欄位與錯誤格式，否則後續容易失焦。
4. milestone 題要先排契約與風險，不要只排畫面進度。

## 驗收標準

- 能寫出簡要商業場景與角色
- 能提出初步系統架構圖
- 能交出一份 API 規格草案
- 能說明為什麼此題目適合這套技術組合
- 能提出舊系統維運到新 API 消費的過渡策略
- 能用 MVP、風險與責任分工角度說明專題啟動決策