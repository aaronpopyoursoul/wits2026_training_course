# Module 06 — Security Exception、401/403 與 CORS 前後端整合

## 模組目標

這一模組要讓學員分清楚三種常被混淆的問題：未登入、已登入但無權限、瀏覽器同源限制。學完後，學員應能判斷一個請求失敗到底是 JWT、權限還是 CORS 問題。

除了分辨問題層次，還要讓學員知道這三種錯誤在瀏覽器、Network 面板、Spring Security 設定與 API 回應上會呈現出不同訊號，不能用同一種修法對待。

## 情境說明

Vue 前端在 `http://localhost:5173` 呼叫後端 API，瀏覽器顯示 CORS 錯誤。團隊第一時間就去改 JWT filter，結果完全沒解決。這類問題在前後端分離導入時非常常見。

## 核心重點

- 401 vs 403
- Security exception mapping
- CORS preflight
- allowed origin / methods / headers / credentials
- 前後端分離下的 API 驗證順序

## 教學步驟

### Step 1：分清 401 與 403

- 401：通常是未登入、token 缺失、token 無效
- 403：通常是已登入但不允許

這裡要進一步補上：

- 401 偏向「你還沒被系統認出是誰」
- 403 偏向「系統知道你是誰，但你不能做這件事」

如果這一點不清楚，前端會把所有錯誤都導回登入頁，後端也會在 exception mapping 上混亂。

### Step 2：理解 CORS 是瀏覽器限制

CORS 不等於 API 壞掉，也不等於 JWT 失敗。它是瀏覽器在前端跨來源呼叫時的限制與協商。

這裡要強調一個觀念：

- Postman 可以通，不代表瀏覽器一定能通
- 瀏覽器擋掉的請求，可能根本還沒進到你的業務邏輯

### Step 3：比對 preflight 請求

學員要能檢查：

- `Origin`
- `Access-Control-Request-Method`
- `Access-Control-Request-Headers`

### Step 4：理解 preflight 為什麼會發生

當瀏覽器判斷這不是簡單請求時，會先送出 `OPTIONS` 做預檢。這時若後端沒有正確回應：

- 允許來源
- 允許方法
- 允許標頭

正式請求可能根本不會送出。

## 前後端排查順序建議

當前端回報「API 打不到」時，可要求學員依下列順序判斷：

1. 是瀏覽器 console 的 CORS 訊息，還是 API JSON 錯誤回應
2. Network 裡有沒有看到 preflight `OPTIONS`
3. request 是否真的帶了 token
4. token 是否有效
5. 若 token 有效，是否是角色 / 權限不足

這套順序能避免一開始就去改錯地方。

## Security exception mapping 重點

這一章要讓學員知道 security 相關例外不是都交給同一個錯誤處理：

- 驗證失敗通常對應 authentication entry point
- 授權失敗通常對應 access denied handler

概念上要先建立：

- 身分驗證失敗和權限不足是不同事件
- 回給前端的訊息與 status code 也應不同

## 常見錯誤

- 把所有 401 / 403 / CORS 問題混成「前端打不到 API」。
- 只開 GET，忘記 OPTIONS。
- 有 credentials 時仍用萬用 origin。
- 看到 CORS 就去改 JWT filter。
- 後端明明回 403，前端卻誤判成 token 過期。
- 只測 Postman，不測瀏覽器實際跨網域情境。

## 自我檢查清單

- 我能說明 401、403、CORS 的差異嗎？
- 我知道 preflight 何時會發生嗎？
- 我能說明為什麼某些前端錯誤其實不是後端 business bug 嗎？
- 我知道 authentication failure 與 access denied 在處理責任上有差異嗎？
- 我能描述一個合理的前後端排查順序嗎？

## 練習題

1. 設計一題 preflight 失敗的排查題。
2. 設計一題 401 與 403 混淆的排查題。
3. 說明 credentials 與 allowed origin 的搭配限制。
4. 解釋為什麼 Postman 可通但瀏覽器失敗不一定是 JWT 問題。

## 解題方向

1. 先看瀏覽器 network，再看 security config。
2. 再判斷 token 是否存在、是否有效、是否有權限。
3. CORS 題一定要說明瀏覽器與 API 伺服器各自扮演什麼角色。
4. Postman / browser 差異題要從瀏覽器安全模型切入。

## 完整參考答案

標準答案重點：

- 先分類問題層次，再進入修正
- CORS 設定要與實際前端來源一致
- 401、403、CORS 各自屬於不同失敗模型
- 前後端整合除錯不能只靠單一工具或單一層觀察