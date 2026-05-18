# Scenario 02 — CORS / Preflight 失敗

## 背景

Vue 前台跑在 `http://localhost:5173`，Spring Boot API 跑在 `http://localhost:8082`。使用者從登入頁送出請求時，瀏覽器在 preflight 階段就失敗，畫面顯示 network error 或 CORS blocked。

## 任務

1. 解釋這個錯誤為什麼不是單純 API 500。
2. 說明同源政策與 preflight request 的角色。
3. 提出後端正式開放 CORS 的設定方案。

## 預期方向

- 先確認前後端是否不同源
- 再確認後端是否允許對應 origin、method、headers
- 不要用全面開放代替精準設計

## 解答方向

1. 這題的本質是瀏覽器安全政策，不是 Vue 或 Axios 特有問題。
2. 若前端送出自訂 Authorization header，通常更容易觸發 preflight。
3. 對本課來說，主體解法是 Spring Boot 正式允許 `http://localhost:5173` 呼叫 `/api/**`。