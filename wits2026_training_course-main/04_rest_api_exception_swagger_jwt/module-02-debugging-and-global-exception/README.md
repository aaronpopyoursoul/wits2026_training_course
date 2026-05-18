# Module 02 — 偵錯流程與全域例外處理

## 模組目標

這一模組要讓學員理解 API 失敗時，不是只看 console 報錯，而是要能分辨錯誤發生在哪一層，並透過全域例外處理把錯誤整理成穩定的回應格式。學完後，學員應能描述一個請求從進入 Controller 到例外被轉成 JSON 回應的完整路徑。

## 核心重點

- 偵錯順序
- 例外分類
- `@ControllerAdvice`
- 統一錯誤格式

## 情境說明

假設前端呼叫保單查詢 API，結果收到 500。初學者常見反應是直接看最後一行 stack trace，但真正有效的做法是先回答三個問題：

1. 請求有沒有正確進到系統
2. 例外是驗證錯、業務錯還是系統錯
3. 最後回給前端的錯誤格式是否一致

## 為什麼不能把 Exception 直接往外丟

如果直接讓 Exception 原樣回到前端，常見問題有：

- 訊息不穩定
- 暴露內部實作細節
- 前端難以一致處理
- 測試案例難以穩定驗證

這一段要讓學員知道，例外處理不是為了把錯誤藏起來，而是把錯誤整理成穩定契約。

## 建議錯誤回應格式

```json
{
  "code": "POLICY_NOT_FOUND",
  "message": "查無保單資料",
  "requestId": "REQ-20240101-0001"
}
```

### Step 1：先把錯誤格式固定下來

若每個 Controller 各自回不同格式，前端會很難處理。例如有些 API 回：

- `message`
- `error`
- `detail`

有些又直接回 HTML 錯誤頁。這會讓整體 API 品質快速失控。教材要讓學員先固定一個統一格式，再來討論每種例外怎麼映射。

## 例外分類示例

- 驗證錯誤：欄位缺漏、格式錯誤
- 業務錯誤：保單不存在、狀態不允許
- 系統錯誤：資料庫連線失敗、未知例外

### Step 2：例外分類是為了回對狀態碼，不是為了分類而分類

學員常會把所有錯誤都包成同一類，最後統一回 `500`。這在 API 設計上是失敗的，因為：

- 驗證錯誤代表使用者輸入不對，應偏向 `400`
- 業務錯誤代表請求合理但條件不成立，可能是 `404`、`409` 或 `422`
- 系統錯誤才是 `500`

## 全域例外處理範例

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(PolicyNotFoundException.class)
	public ResponseEntity<ErrorResponse> handlePolicyNotFound(PolicyNotFoundException ex) {
		ErrorResponse response = new ErrorResponse("POLICY_NOT_FOUND", ex.getMessage(), RequestIdHolder.get());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		ErrorResponse response = new ErrorResponse("INTERNAL_ERROR", "系統忙碌中，請稍後再試", RequestIdHolder.get());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
}
```

## 這段程式要教什麼

- `@RestControllerAdvice` 代表集中處理 API 例外
- `@ExceptionHandler` 用來指定某一類例外如何映射
- 外層 `ErrorResponse` 用來穩定前後端契約
- 未知例外不應原樣曝光給前端

### Step 3：requestId 的用途要講清楚

很多人以為 requestId 只是多一個欄位，其實它是把前端回報與後端 log 連起來的索引。

當使用者說「剛剛查保單失敗」，若畫面能帶出 requestId，後端就能從 log 裡快速定位是哪一筆請求失敗。這也是為什麼錯誤回應裡保留 requestId 很有價值。

## 偵錯時要先看什麼

1. API 路徑是否正確
2. Request body 是否符合格式
3. Controller 是否收到請求
4. Service / Repository 哪裡拋出例外
5. Global exception handler 是否正確轉換

### Step 4：偵錯順序要從外到內，不要直接跳到最後一層

合理的排查順序通常是：

1. 先確認 request 是否真的打到正確 URL
2. 再看 header、body、path variable 是否正確
3. 確認 Controller 是否有命中
4. 再往 service、repository 追
5. 最後檢查 exception handler 的映射與回應格式

若一開始就只盯著資料庫或 service，常常會浪費很多時間。

## 常見錯誤

- 只靠 try-catch 包住所有程式碼
- 把所有錯誤都回成 500
- 前端收到錯誤時只看到 stack trace 或 HTML 頁面
- 沒有 requestId，無法對應 log
- 把系統內部例外訊息直接暴露給前端
- 明明是驗證錯誤，卻回成 `200` + 錯誤訊息字串

## 自我檢查清單

- 我能分辨驗證錯誤、業務錯誤、系統錯誤嗎？
- 我知道為什麼全域例外處理比每個 Controller 各自 try-catch 更穩定嗎？
- 我知道 requestId 在除錯流程中的用途嗎？
- 我能描述從 exception 到標準 JSON 錯誤回應的流程嗎？

## 練習題

1. 設計一個 `PolicyNotFoundException` 的錯誤回應。
2. 設計驗證錯誤與系統錯誤的回應格式。
3. 針對一個 API 失敗案例，寫出你的偵錯順序。
4. 找出一個「表面上能跑，但錯誤處理不合理」的 API 寫法並說明原因。

## 練習解答方向

1. `PolicyNotFoundException` 應對應 `404 Not Found`。
2. 驗證錯誤與系統錯誤可以共用外層格式，但 code / message / status 要明確。
3. 偵錯題要從 request 進入點一路追到 handler，而不是只看最後例外訊息。
4. 反例題可從錯誤狀態碼、訊息暴露、格式不一致三個角度拆解。

## 驗收標準

- 能說明為什麼需要統一錯誤格式
- 能區分驗證、業務、系統錯誤
- 能寫出基本的 `@RestControllerAdvice`
- 能描述 API 出錯時合理的偵錯順序
- 能說明 requestId 如何協助前後端與維運協作