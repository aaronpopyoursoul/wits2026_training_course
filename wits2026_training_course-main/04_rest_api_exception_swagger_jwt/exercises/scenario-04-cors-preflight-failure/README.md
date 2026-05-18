# Scenario 04 — CORS Preflight Failure

## 背景

Vue 前端從 `http://localhost:5173` 呼叫後端受保護 API，瀏覽器顯示 preflight 失敗，畫面上看起來像是「API 壞了」，但 Postman 呼叫卻正常。

## 任務

1. 說明為什麼 Postman 正常不代表瀏覽器正常。
2. 提出排查順序：network、response headers、security config、allowed origins。
3. 寫出完整參考答案與修正方式。

## 解題方向

- 先辨識這是瀏覽器跨來源限制，不是純 JWT 問題。
- 檢查 OPTIONS 是否允許，allowed headers 是否包含實際送出的內容。

## 完整參考答案

關鍵答案：

1. CORS 是瀏覽器限制，Postman 不會做同樣檢查。
2. 修正要對準 origin、methods、headers、credentials。