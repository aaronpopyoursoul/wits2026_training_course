# Module 07 — Observability、驗收流程與交付就緒

## 模組目標

這一模組要讓學員把 API 做到真正可交付，而不是只有本機可跑。內容聚焦 requestId、錯誤追蹤、Postman 驗收流程、文件核對與交付檢查清單。

學完後，學員應能回答一個很實務的問題：如果測試只回報「有一支 API 有時候會失敗」，我要靠什麼資訊在最短時間內重現、定位與交接問題。

## 情境說明

測試同事回報理賠 API 有時失敗，但只貼了一張 500 截圖。若系統沒有 requestId、錯誤碼與清楚的 Postman 驗收流程，開發者很難快速定位問題。這正是 observability 與 delivery readiness 的意義。

## 核心重點

- requestId / traceability
- error code 設計
- Postman 驗收腳本
- 交付前檢查清單
- API 發布溝通

## 教學步驟

### Step 1：先定義可追蹤 API 的最小資訊

- requestId
- errorCode
- path
- timestamp

這一段的重點不是背欄位，而是讓學員理解這些欄位各自的用途：

- requestId：串接前端回報與後端 log
- errorCode：快速辨識錯誤類型
- path：定位是哪支 API
- timestamp：協助對時與排查

### Step 2：建立驗收流程

至少要有：

- health check
- login
- protected API success
- invalid request
- invalid token / expired token

這裡要明確指出，驗收不是只驗 happy path。真正可交付的 API 至少要驗證：

- 正常成功
- 輸入錯誤
- 權限錯誤
- token 錯誤
- 文件與實作是否一致

### Step 3：建立交付前檢查清單

讓學員養成發布前對照 DTO、OpenAPI、Postman 與 security config 的習慣。

### Step 4：把 observability 當成交付品質，而不是額外加分題

很多團隊在開發階段忽略 requestId、錯誤碼與結構化錯誤資訊，等到測試或上線出問題時才發現難以追蹤。這一章要建立的觀念是：

- 可追蹤性不是維運人員才需要
- 開發、測試、前端、維運都會依賴它

## 建議交付前檢查面向

- 功能 API 是否可正確操作
- 常見錯誤情境是否有穩定回應格式
- 受保護 API 是否能正確回 401 / 403
- OpenAPI 是否與實作一致
- Postman collection 是否可重複執行
- requestId / errorCode 是否可在錯誤情境中取得

## 常見錯誤

- 覺得 requestId 是額外裝飾。
- 驗收只跑 happy path。
- 發布前沒有核對文件與實作是否一致。
- 測試回報沒有 requestId，後端也沒有主動要求補足資訊。
- 錯誤碼設計過度隨意，導致同類問題無法歸類。
- 上線前沒有一套固定驗收順序，每次靠個人記憶操作。

## 自我檢查清單

- 我能說明 requestId 對排查有什麼幫助嗎？
- 我是否有一套可重複執行的 Postman 驗收流程？
- 我知道交付前應核對哪些項目嗎？
- 我能解釋 observability 為什麼也是開發品質的一部分嗎？
- 我能列出 success path 與 failure path 的最小驗收集嗎？

## 練習題

1. 設計一份 API 交付檢查清單。
2. 設計一題測試回報資訊不足的排查改善題。
3. 寫出受保護 API 的最小驗收流程。
4. 說明 errorCode 與 requestId 在交付與維運上的不同角色。

## 解題方向

1. 驗收清單要包含功能、錯誤、安全與文件。
2. 排查題要要求 requestId / errorCode / path 等資訊。
3. 驗收流程要同時覆蓋 success 與 failure path。
4. 角色題要從重現、定位、溝通三個角度回答。

## 完整參考答案

標準答案重點：

- 可交付 API 必須可測、可追、可重現
- Observability 是交付品質的一部分
- 驗收流程應可重複、可交接，而不是依賴個人經驗
- requestId、errorCode、文件與驗收腳本共同構成交付就緒基礎