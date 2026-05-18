# Scenario 02 — JWT 過期與未授權處理

## 背景

理賠查詢 API 已加上 JWT，但 token 過期時系統直接丟出未處理例外，造成前端只看到 generic 500。

## 任務

1. 區分未登入、token 過期、token 無效三種情境。
2. 定義一致回應格式。
3. 用 Postman 驗證受保護 API 的成功與失敗流程。