# Demo Script

## Demo 1：API contract 先於程式碼

1. 先畫出 login、policy summary、claim status 三條 API。
2. 定義 request / response 與錯誤格式。
3. 再對照 controller 與 DTO。

## Demo 2：Global exception handling

1. 故意送錯欄位。
2. 讓學員看錯誤格式是否一致。
3. 比對 requestId、errorCode 與 path。

## Demo 3：Postman 與 OpenAPI 同步

1. 打開 OpenAPI。
2. 跑 Postman collection。
3. 驗證文件與實作是否一致。

## Demo 4：JWT、401/403 與 CORS

1. 示範未登入呼叫。
2. 示範帶 token 呼叫。
3. 示範前端跨來源情境與 CORS 設定。