# Module 01 — RestController 與 API 設計

## 模組目標

這一模組要讓學員知道 REST API 不是把 service 方法直接暴露出去，而是把業務能力整理成前後端、測試與文件都能理解的介面契約。學完後，學員應能說明什麼叫資源語意、為什麼 method 不該亂用，以及 request / response DTO 為什麼不能直接等於 entity。

## 核心重點

- 路徑命名
- HTTP method 選擇
- request / response DTO
- 狀態碼語意

## 情境說明

假設你要為保險系統設計三條 API：保單查詢、建立理賠案件、核准理賠。若你只是把 service 方法名稱直接變成路徑，短期雖然能打通，但前端、測試與文件使用者很快就會看不懂這些 API 到底在代表什麼資源與行為。

## 先回答一個問題：什麼叫做 API 設計合理

不是「能從 Postman 打通」就合理，而是：

1. 路徑看得出資源語意。
2. method 與行為一致。
3. request / response 結構清楚。
4. 成功與失敗的狀態碼有意義。

### Step 1：先分清資源、動作與畫面按鈕不是同一件事

很多初學者設計 API 時，會直接把前端畫面的按鈕名稱搬進路徑，例如：

- `/doQueryPolicy`
- `/createClaimAction`
- `/approveButtonClick`

這樣的 API 反映的是畫面操作，不是資源模型。教材要讓學員知道 REST 設計先問的是：

- 我在操作哪個資源
- 這個操作是查詢、建立、更新還是刪除

### Step 2：路徑設計要讓人一眼看出語意

例如：

- `GET /api/policies/{policyNo}`：查一筆保單
- `POST /api/claims`：建立一筆理賠
- `PUT /api/claims/{claimNo}/approval`：更新核准狀態

這樣的設計比 action 型路徑更適合：

- 文件閱讀
- 前後端協作
- 後續擴充

## 不好的寫法示例

```http
POST /api/doPolicyAction?action=query
POST /api/doPolicyAction?action=createClaim
```

問題：

- 路徑混雜多種用途
- method 都是 POST，失去語意
- 前後端讀 API 文件時難以直覺理解

## 建議寫法

```http
GET /api/policies/{policyNo}
POST /api/claims
PUT /api/claims/{claimNo}/approval
```

### Step 3：HTTP method 的選擇要能對應操作意圖

這一段要讓學員不只背 method 名稱，而是知道它們在 API 設計裡各自代表什麼：

- `GET`：查資料
- `POST`：建立新資源或觸發建立型流程
- `PUT`：更新整體狀態或完整替換
- `PATCH`：部分更新
- `DELETE`：刪除或停用

如果查詢、建立、更新全部都用 `POST`，短期雖可運作，但契約可讀性會很差。

## DTO 設計範例

```java
public class CreateClaimRequest {
	private String policyNo;
	private BigDecimal claimAmount;
	private String reason;
}
```

```java
public class ClaimResponse {
	private String claimNo;
	private String policyNo;
	private String status;
	private BigDecimal claimAmount;
}
```

### Step 4：DTO 為什麼要與 entity 分開

這是 04 很重要的 API 基礎。若把 entity 直接往外回傳，容易出現：

- 曝露不該給前端的欄位
- response 欄位被資料庫結構綁死
- 文件與契約很難穩定演進

所以教材要讓學員養成：

- request DTO：代表前端送進來的資料
- response DTO：代表 API 願意公開的契約

而不是把資料表欄位原封不動暴露出去。

## Controller 範例

```java
@RestController
@RequestMapping("/api/claims")
public class ClaimController {

	private final ClaimApplicationService claimApplicationService;

	public ClaimController(ClaimApplicationService claimApplicationService) {
		this.claimApplicationService = claimApplicationService;
	}

	@PostMapping
	public ResponseEntity<ClaimResponse> createClaim(@RequestBody CreateClaimRequest request) {
		ClaimResponse response = claimApplicationService.createClaim(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
```

## 這段程式要教什麼

- `@RequestMapping` 先定義資源集合
- `@PostMapping` 表示建立資源
- `201 Created` 比 `200 OK` 更符合建立語意
- Controller 不應該直接處理資料庫邏輯

### Step 5：Controller 的責任要講清楚

Controller 應主要負責：

- 接收 request
- 驗證輸入格式
- 呼叫 application / service 層
- 回傳適當 response 與 status

不應該負責：

- 直接寫 SQL
- 直接組裝 repository 查詢邏輯
- 直接處理複雜交易流程

這是為了讓 API 層維持可讀、可測、可演進。

## 狀態碼怎麼選

- `200 OK`：一般查詢或更新成功
- `201 Created`：成功建立資源
- `400 Bad Request`：輸入格式或必要欄位不符
- `401 Unauthorized`：未登入或 token 無效
- `404 Not Found`：查無指定資源
- `500 Internal Server Error`：系統端未預期錯誤

### Step 6：HTTP 狀態碼與業務訊息要分開講

學員要能理解：

- 狀態碼回答的是 HTTP 層語意
- 錯誤碼與訊息回答的是業務或系統層語意

例如：

- `404` + `POLICY_NOT_FOUND`
- `400` + `CLAIM_AMOUNT_INVALID`

這樣前端、測試與維運才知道到底是哪一類問題。

## 常見錯誤

- 把 entity 直接當 response 往外回。
- 更新、查詢、刪除全部都用 POST。
- API 命名反映的是畫面按鈕，不是資源語意。
- 一次回傳太多無關欄位，讓契約失焦。
- 看到功能能跑就停止思考，沒有考慮文件、測試與可演進性。
- path 看似合理，但 method 與行為其實不一致。

## 自我檢查清單

- 我能指出這條 API 的主資源是什麼嗎？
- 我知道為什麼這裡用 GET、POST、PUT 或 PATCH 嗎？
- 我能解釋 request DTO 與 response DTO 為什麼不應直接等於 entity 嗎？
- 我能把 status code 與業務錯誤碼分開說明嗎？

## 練習題

1. 設計保單查詢 API 與回應 DTO。
2. 設計理賠建立 API，並決定應該回 200 還是 201。
3. 設計理賠核准 API，說明為什麼用 PUT 或 PATCH。
4. 設計一條錯誤示範 API，並指出它為什麼不合理。

## 練習解答方向

1. 查詢題要先辨識主資源是 policy，不是 query。
2. 建立題若有新增資源，通常回 201 更合理。
3. 更新題要說明是整體替換還是部分變更，這會影響 PUT / PATCH 的選擇。
4. 反例題要從 path、method、DTO、status code 四個角度挑出問題。

## 驗收標準

- 能設計出查詢、建立、更新三種 API
- 能說明為什麼某個路徑或 method 這樣命名
- 能避免把所有 API 都做成單一路徑加 action 參數
- 能把 response DTO 與 entity 做清楚分離
- 能從資源語意與交付角度說明 API 設計合理性