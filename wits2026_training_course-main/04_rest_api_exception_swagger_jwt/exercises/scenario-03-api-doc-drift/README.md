# Scenario 03 — API 文件與實作漂移

## 背景

前端依 Swagger 文件呼叫核保 API，但實際欄位名稱與文件不一致，造成測試延誤。團隊口頭說明已更新，卻沒有同步到 OpenAPI 文件。

## 任務

1. 找出文件與實作不一致之處。
2. 修正 Swagger / OpenAPI 設定或註解。
3. 用 Postman 重新驗證文件是否可用。