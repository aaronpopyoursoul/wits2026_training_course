# Module 03 — Postman 與 API 文件化

## 模組目標

這裡不是教工具操作而已，而是要讓學員理解：沒有文件、沒有可重跑的測試集合，就不算真正可交付的 API。

## 核心重點

- Postman collection
- environment 變數
# Module 03 — Postman 測試與 OpenAPI 文件

## 模組目標

這一模組要讓學員知道 API 不只要能寫，還要能被驗證、被展示、被交接，因此 Postman 與 OpenAPI 不是附屬工具，而是 API 交付流程的一部分。學完後，學員應能自己整理一套最小可用的 collection，並說明 Swagger UI 背後其實是在展示契約而不是只是展示頁面。

## 核心重點

- Postman collection
- 環境變數
- Swagger / OpenAPI
- 文件與實作同步

## 情境說明

API 開發最常見的問題之一，不是 Controller 寫不出來，而是「寫完以後只有作者自己知道怎麼用」。只要團隊中多一個前端、一個測試，這個問題就會立刻浮現。這一章要讓學員理解：

- Postman 是測試與重現工具
- OpenAPI 是契約說明工具
- 兩者搭配，才能讓 API 真正可交付

## Postman 能幫助什麼

- 驗證 API 行為
- 儲存測試案例
- 協助前後端協作
- 快速重現錯誤情境

### Step 1：Postman 不只是「手動按 Send」

學員很容易把 Postman 當成點擊工具，但在教學上要把它定位成：

- 快速驗證請求格式
- 保存 happy path 與 error path
- 協助團隊共用測試資料
- 讓 bug 能被穩定重現

## 建議測試集合內容

1. login
2. 查詢保單
3. 建立理賠
4. 查詢未授權情境
5. 查詢不存在資源情境

### Step 2：collection 應該有結構，不要所有 request 混在一起

例如可以按主題分資料夾：

- Auth
- Policy
- Claim
- Error Cases

這樣做的價值是後續 demo、除錯與交接都更清楚。

## 環境變數示例

- `baseUrl`
- `token`
- `policyNo`

### Step 3：環境變數是為了減少硬編碼與重複輸入

如果每個 request 都把 host、token、測試 ID 寫死，很快就會遇到：

- 換環境要全部重改
- token 過期後每條都要重填
- 團隊成員無法共用 collection

因此教材要讓學員理解 `baseUrl`、`token`、測試資料變數的用途。

## login 後保存 token 的概念

典型流程是：

1. 呼叫 login API
2. 從 response body 取出 token
3. 存進環境變數
4. 後續授權 API 自動帶入 `Authorization` header

這段流程要讓學員知道，Postman 測試不只是單次請求，而是能串成一個操作流程。

## OpenAPI 的角色

OpenAPI 不只是畫面漂亮的 Swagger UI，而是 API 契約的可視化表示。

### Step 4：OpenAPI 與 Swagger UI 要分開理解

- OpenAPI：契約規格
- Swagger UI：把規格渲染成可閱讀、可互動的頁面

若只記得 Swagger 畫面，卻不知道背後其實是在描述 path、schema、status code、security requirement，就很容易把文件維護流於形式。

## 文件應包含內容

- API path
- request body
- response schema
- 狀態碼
- 權限需求

### Step 5：什麼叫做文件與實作同步

真正的同步不是「有頁面就算完成」，而是：

- path 沒寫錯
- 欄位名稱與型別一致
- 權限需求寫清楚
- 錯誤狀態碼有列出
- request / response 範例與實作一致

## 常見錯誤

- 只寫 Controller，不維護文件
- Swagger 有列出 API，但欄位說明過於模糊
- Postman 測試資料寫死，導致團隊難以共用
- 文件與程式實作不一致
- collection 只有成功案例，沒有失敗案例
- 前端看到 Swagger 畫面，卻仍不知道何時會回 401、403、404

## 自我檢查清單

- 我知道 Postman collection 為什麼要有成功與失敗案例嗎？
- 我能說明 environment variable 的實際用途嗎？
- 我知道 OpenAPI 是契約而不只是 UI 嗎？
- 我能辨識文件與實作不同步的風險嗎？

## 練習題

1. 建立一組保單查詢與理賠建立的 Postman collection。
2. 設計 login 後自動保存 token 的測試流程。
3. 列出一份 API 文件至少要包含哪些欄位。
4. 找一個文件與實作可能脫節的案例，說明會造成什麼協作問題。

## 練習解答方向

1. collection 要有成功與失敗案例，不是只有 happy path。
2. token 保存可以透過 Postman test script 寫入環境變數。
3. 文件欄位至少要能讓前端知道怎麼送、會回什麼、何時失敗。
4. 脫節案例可從欄位名稱、狀態碼、授權需求三類切入。

## 驗收標準

- 能建立基本 Postman collection
- 能說明 environment variable 的用途
- 能理解 OpenAPI 與 Swagger UI 的關係
- 能避免文件與程式逐漸脫節
- 能把 Postman 與 OpenAPI 放回 API 交付流程中理解
			summary: 查詢保單摘要
			parameters:
				- name: policyNo
					in: path
					required: true
					schema:
						type: string
			responses:
				'200':
					description: 查詢成功
				'404':
					description: 查無保單
```

## 文件與程式碼如何保持一致

1. 每新增一條 API，就同步補 Postman 與 OpenAPI。
2. 每次欄位改名，都要檢查 collection 與 schema。
3. 把文件更新列入驗收條件，而不是「有空再補」。

## 常見錯誤

- 只有 Swagger UI 畫面，沒有標準 OpenAPI 檔案。
- Postman collection 只存請求，沒有測試腳本。
- OpenAPI 寫的是舊欄位名稱，實作已改但未同步。
- 只測 happy path，不測錯誤情境。

## 練習題

1. 建立登入與查詢保單的 Postman collection。
2. 撰寫 OpenAPI 文件，描述一條受保護 API。
3. 加入至少一個 404 與一個 401 的測試案例。

## 練習解答方向

1. environment 要讓 token 可被後續請求重用。
2. OpenAPI 不只寫 200，至少補 400、401、404 其中幾種常見回應。
3. 若文件與實作不一致，要把差異列出來，不要直接忽略。

## 驗收標準

- 能建立至少一組 Postman collection
- 能用 Swagger UI 檢視與驗證 API
- 能說明文件與程式碼不一致時，會造成什麼協作問題
- 能描述 token 取得與使用的完整驗收流程