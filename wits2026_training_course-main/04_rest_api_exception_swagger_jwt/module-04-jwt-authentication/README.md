# Module 04 — JWT 驗證與登入流程

## 模組目標

這一模組要讓學員理解 JWT 驗證不是只把 token 發出去，而是要知道登入、帶 token、驗證 token、建立安全上下文這整條流程怎麼串起來。學完後，學員應能清楚分辨登入成功、token 驗證成功、授權成功其實是三個不同層次。

## 核心重點

- login API
- token 生成
- request header 驗證
- security context

## 情境說明

很多初學者以為 JWT 的重點只是「登入成功回一個 token」。但真正的教學重點應該是整條請求鏈：

1. 使用者怎麼登入
2. 後端怎麼產生 token
3. 前端後續怎麼帶 token
4. 後端怎麼驗證 token
5. 驗證通過後怎麼建立使用者身分
6. 有身分後還要怎麼做授權判斷

## JWT 流程概觀

1. 使用者登入
2. 後端驗證帳密
3. 建立 JWT token
4. 前端之後帶著 token 呼叫 API
5. 後端驗證 token 並建立登入身分

### Step 1：先分清登入、驗證、授權三件事

- 登入：使用者送帳密，後端確認你是誰
- 驗證：後續請求帶 token，後端確認 token 是否可信
- 授權：即使你是合法使用者，也不代表你能做所有操作

這一段不講清楚，學員很容易把 `401` 和 `403` 完全混在一起。

## 常見 header

```http
Authorization: Bearer eyJhbGciOi...
```

### Step 2：Bearer token 是放在 header，不是放在 query string

這裡要建立正確慣例：

- 通常放在 `Authorization` header
- 前綴是 `Bearer `
- 不應該把 token 隨意放在 URL 參數

## login response 示意

```json
{
	"token": "<JWT>",
	"userId": "A12345",
	"role": "USER"
}
```

### Step 3：JWT response 要回什麼，不要回什麼

通常可以回：

- token
- 使用者識別資訊
- 前端啟動畫面所需的最小角色資訊

不應該回：

- 過多敏感欄位
- 不必要的內部安全資訊
- 讓前端自行決定授權邏輯所需的全部細節

## Filter 的角色

Filter 會在 request 進到 controller 前先檢查 header 是否有 token，若有就驗證並建立使用者資訊。

### Step 4：Filter 在整條鏈上的位置要講清楚

JWT Filter 的責任通常是：

- 讀取 header
- 解析 token
- 驗證 token 是否有效
- 建立 `Authentication`
- 放進 Security Context

它不是用來：

- 處理業務邏輯
- 直接決定所有錯誤訊息格式
- 取代 Controller 或 Service 的責任

可用下列方法示意 token 解析後建立身分：

```java
public Authentication parseAuthentication(String token) {
	Claims claims = Jwts.parserBuilder()
		.setSigningKey(secretKey)
		.build()
		.parseClaimsJws(token)
		.getBody();

	String username = claims.getSubject();
	return new UsernamePasswordAuthenticationToken(username, token, Collections.emptyList());
}
```

## 教學重點

- token 不代表已授權所有資源
- 驗證成功後，後端仍需看角色或權限
- 401 與 403 不應混用

### Step 5：401 與 403 要在這一章先建立正確觀念

- `401 Unauthorized`：通常代表沒有合法身分，例如沒帶 token、token 無效、token 過期
- `403 Forbidden`：通常代表已通過身分驗證，但角色或權限不足

這個觀念會直接影響後續 security exception 與前端攔截處理。

## JWT 結構理解重點

教學上不必把數學與加密細節講得過深，但至少要讓學員知道：

- JWT 不是亂碼字串而已
- 它有 header、payload、signature 三個概念
- payload 可以放 claims，但不代表什麼都該放
- signature 是讓後端判斷 token 是否被竄改的重要依據

## 常見驗證失敗情境

- 沒有 Authorization header
- header 不是 `Bearer ` 開頭
- token 過期
- token 簽章錯誤
- token 結構損壞

## 回應設計建議

驗證失敗時，不要直接把底層堆疊拋給前端。建議至少有：

```json
{
	"errorCode": "UNAUTHORIZED",
	"message": "invalid or expired token",
	"path": "/api/claims/CLM-20240001"
}
```

## 常見錯誤

- 把 JWT 當 session 使用觀念不清
- token 解析失敗時直接回傳原始例外
- 登入成功但後續 API 沒有帶 token
- 以為有 token 就等於有所有權限
- 在 token 裡放過多敏感資料
- 只測登入成功，不測 token 過期或無效場景

## 自我檢查清單

- 我能畫出登入到受保護 API 的完整流程嗎？
- 我能說明 Filter 在 request 鏈上的位置嗎？
- 我能分辨 401 與 403 的差異嗎？
- 我知道 token 驗證成功不等於授權成功嗎？

## 練習題

1. 畫出登入到受保護 API 的請求流程。
2. 說明 401 與 403 的差異。
3. 列出 JWT 驗證失敗時可能發生的三種情境。
4. 說明 Filter、Controller、權限判斷各自的責任。
5. 實作一條受保護的保單或理賠 API，並驗證不帶 token、帶錯 token、過期 token 三種情況。

## 練習解答方向

1. 流程要包含 login、發 token、帶 token、驗證 token、建立 authentication。
2. 401 偏向未登入 / token 無效，403 偏向已登入但權限不足。
3. 例如 token 過期、簽章錯誤、header 缺失。
4. 角色題要回答誰負責驗證身分、誰負責業務處理、誰負責授權判斷。
5. 受保護 API 題要實際驗證 header，而不是只在 controller 手動判斷字串。

## 驗收標準

- 能口頭說明 JWT 驗證流程
- 能知道 Filter 在整個流程中的位置
- 能分辨登入失敗與權限不足
- 能理解 token 與角色授權是兩個層次
- 能說明登入、驗證、授權三者的差異
- 能用 Postman 展示完整登入與帶 token 驗收流程