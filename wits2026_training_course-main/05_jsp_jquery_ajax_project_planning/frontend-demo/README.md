# frontend-demo

這個資料夾提供 3 種示範：

1. JSP 頁面如何承接伺服器端資料並做局部互動
2. jQuery AJAX 如何登入並呼叫受保護 API
3. Axios 如何以較清楚的方式管理 token 與 API 請求

## 對接後端

預設對接 04 課程中的範例 API：

- base URL：`http://localhost:8082`
- login：`POST /api/auth/login`
- policy summary：`GET /api/policies/{policyNo}/summary`
- claim status：`GET /api/claims/{claimNo}/status`

## 建議使用方式

若要直接執行且避免瀏覽器跨來源限制，請優先使用已整合到 04 專案中的同源示範頁：

- `/legacy-demo/jquery-ajax-tester.html`
- `/legacy-demo/axios-api-tester.html`

這兩個頁面會和 04 的 API 一起由同一個 Spring Boot 應用提供。

JSP 範例則是給課堂講解傳統頁面結構使用，不要求直接在本資料夾中獨立執行。

## 教學對照重點

- JSP 範例：用來說明 request attribute、伺服器端渲染與頁面初始狀態
- jQuery 範例：用來說明事件綁定、AJAX 與 legacy DOM 操作
- Axios 範例：用來說明較清楚的 HTTP client 寫法與 token 管理

## 建議授課順序

1. 先看 JSP 範例，理解頁面資料從哪裡來。
2. 再看 jQuery AJAX 範例，理解舊頁面如何開始消費 API。
3. 最後對照 Axios 範例，說明程式可讀性與維護性的提升。

## 可延伸補強題

- 動態 DOM 與事件委派
- 表單驗證與錯誤訊息顯示
- XSS 避免方式
- 與 OpenAPI 契約同步的 API 測試頁