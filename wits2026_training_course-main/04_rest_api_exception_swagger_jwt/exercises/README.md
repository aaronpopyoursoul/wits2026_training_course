# Exercises

本課 exercises 以「可交付 API」為標準，不只驗證 endpoint 能不能回資料，而是要求學員能處理契約一致性、例外回應、JWT、CORS 與交付驗證。

## 練習索引

- scenario-01-validation-error：輸入驗證錯誤與錯誤格式不一致
- scenario-02-jwt-expired：JWT 過期或無效時的回應與排查
- scenario-03-api-doc-drift：OpenAPI / Swagger 文件與實作漂移
- scenario-04-cors-preflight-failure：前端串接時的 preflight 失敗
- scenario-05-forbidden-vs-unauthorized：401 / 403 邊界與 security exception mapping
- scenario-06-duplicate-create-request：重複建立請求與 idempotency 設計

## 建議作答方式

1. 先界定問題落在 HTTP、Controller、Security、文件還是前端整合層。
2. 再列出排查順序。
3. 接著寫出修正策略與理由。
4. 最後提供完整參考答案與驗證方式。

## 評核重點

1. 是否能分辨 400 / 401 / 403 / 404 / 500 的責任邊界。
2. 是否能把 Postman、OpenAPI 與實際程式互相比對。
3. 是否能解釋 JWT 與 CORS 是不同層次的問題。
4. 是否能提出可執行的驗收方式。

## 建議評分方式

- 問題辨識：25%
- 修正方案：30%
- 契約與安全理解：25%
- 驗證與說明：20%

## 常見作答錯誤

- 把所有安全錯誤都回答成 JWT 失敗。
- 只改 Swagger UI 顯示，沒有同步修正 OpenAPI 或 DTO。
- 看到瀏覽器錯誤就直接改 CORS，沒有先確認實際 HTTP status。
- 認為 API 可以成功新增一次就不需要考慮重送或重複建立。