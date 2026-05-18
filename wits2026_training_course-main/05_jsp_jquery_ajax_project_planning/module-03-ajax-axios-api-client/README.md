# Module 03 — AJAX 與 Axios API 呼叫

## 模組目標

這一模組不是單純比較語法，而是讓學員看到三個層次：

1. 傳統頁面局部更新怎麼做
2. jQuery AJAX 如何呼叫 REST API
3. Axios 如何讓 API 呼叫結構更清楚、更容易擴充

## 核心重點

- XMLHttpRequest 與 jQuery AJAX 基本觀念
- Axios request / response 流程
- Token header 帶法
- API 錯誤處理與畫面回饋

## 情境說明

在 05 的場景裡，AJAX 與 Axios 不是單純技術選型，而是舊系統逐步接上 API 的橋梁。你可能不會立刻把整頁改成 SPA，但你很常會被要求：

- 在 JSP 頁面補一個 AJAX 查詢
- 做一個同源測試頁驗證 JWT API
- 把原本散亂的 request 呼叫改成比較可讀的 Axios 寫法

## jQuery AJAX 範例

```javascript
$("#loginButton").on("click", function () {
	$.ajax({
		url: "/api/auth/login",
		method: "POST",
		contentType: "application/json",
		data: JSON.stringify({
			username: $("#username").val(),
			password: $("#password").val()
		}),
		success: function (response) {
			localStorage.setItem("accessToken", response.accessToken);
			$("#message").text("login success");
		},
		error: function (xhr) {
			$("#message").text(xhr.responseText);
		}
	});
});
```

### Step 1：AJAX 不只是「打 API」，而是一段完整請求流程

學員要能拆出這四件事：

- request 要送什麼
- header 要不要帶 token
- success 後畫面要怎麼更新
- error 時使用者看見什麼

如果只停在「成功打到 API」，就會忽略契約、失敗回應與維運可讀性。

## Axios 範例

```javascript
async function queryClaim() {
	const claimNo = document.getElementById("claimNo").value;
	const token = localStorage.getItem("accessToken");

	try {
		const response = await axios.get(`/api/claims/${claimNo}`, {
			headers: {
				Authorization: `Bearer ${token}`
			}
		});

		document.getElementById("result").textContent = JSON.stringify(response.data, null, 2);
	} catch (error) {
		if (error.response) {
			document.getElementById("result").textContent = JSON.stringify(error.response.data, null, 2);
		} else {
			document.getElementById("result").textContent = "network error";
		}
	}
}
```

### Step 2：jQuery AJAX 與 Axios 的差別，不只是寫法比較新

- jQuery AJAX：適合舊頁面局部補強
- Axios：更容易整理 request、response、error 與共通設定

這一點要講明，否則學員很容易只把 Axios 理解成「比較潮的 AJAX」。

## 教學重點

### 1. request 與 response 是一個完整流程

不要只看「打到 API」，還要看：

- request body 怎麼組
- header 怎麼帶
- response data 怎麼顯示
- error response 怎麼處理

### 1-1：同源測試頁在舊系統維運裡的價值

這一段很重要，因為 05 和 04 是一起搭的：

- 把測試頁放在後端同源路徑下
- 可直接驗證 JWT API
- 可避開跨域干擾，先確認契約是否正確

這就是為什麼 05 不只是「前端語法課」，而是 API 消費與維運課。

### 2. 帶 token 是前後端契約的一部分

如果後端規定 `Authorization: Bearer <token>`，前端就必須穩定遵守，否則 401 是必然結果。

### 2-1：token 應放在哪裡，這是維運現實問題

在 05 的舊系統脈絡中，很多測試頁會先把 token 放在：

- localStorage
- sessionStorage
- 頁面變數

教學上要讓學員知道：

- 測試頁可以先簡化處理
- 但正式專案要有更明確的狀態管理與風險評估

這也正好銜接 06 的 Pinia / service layer。

### 3. 畫面要有失敗時的回饋

不要 API 失敗就只在 console 看錯誤。實務上至少要把錯誤訊息、錯誤碼或提示顯示出來。

### 3-1：錯誤處理不要全部顯示同一段 generic message

至少要分辨：

- 400：輸入或請求格式問題
- 401：未登入或 token 錯誤
- 404：查無資料
- network error：請求根本沒到後端

這是前端是否理解後端契約的基本表現。

### Step 3：API base path 與環境切換

在舊系統測試頁裡，很多人會把 API 路徑直接寫死。這短期能跑，但一換環境就很痛苦。

所以教材要讓學員建立觀念：

- 測試頁可以先用相對路徑
- 若要跨環境，就要有 base path 管理思維

這也是後面 06 build / deploy 的前置概念。

## jQuery AJAX 與 Axios 的差異

- jQuery AJAX 比較貼近舊系統頁面改造
- Axios 在 request / response 攔截與結構化處理上更清楚
- 舊系統維運常常兩者都會遇到，所以必須都看得懂

## 常見錯誤

- token 沒有加 `Bearer ` 前綴。
- content type 不對，後端讀不到 JSON。
- 只處理 success，不處理 error。
- 把 API base path 寫死，導致換環境困難。
- 只會看 console，不讓畫面顯示任何回饋。
- 沒分清 401 是驗證問題，404 是查無資料問題。

## 自我檢查清單

- 我能說明一次 API 呼叫從 request 到畫面顯示的完整流程嗎？
- 我知道 Bearer token 為什麼是契約的一部分嗎？
- 我能區分 400、401、404、network error 的前端呈現差異嗎？
- 我知道為什麼同源測試頁對 legacy 系統很重要嗎？

## 練習題

1. 用 jQuery AJAX 完成登入並保存 token。
2. 用 Axios 呼叫一條受保護 API。
3. 模擬 401、400、404 三種錯誤並在畫面顯示不同訊息。
4. 說明相對路徑與寫死 base URL 的差異。

## 練習解答方向

1. 登入成功後要確定 token 被保存，否則下一步無法驗證。
2. 受保護 API 題要真的加 header，不是把 token 放 query string。
3. 401、400、404 要顯示不同訊息，不能全部寫成 generic error。
4. base path 題要從環境切換與維護成本角度說明。

## 驗收標準

- 能用 AJAX 或 Axios 成功打到後端 API
- 能帶上 Bearer token 呼叫受保護 API
- 能處理 401 與 400 回應並顯示適當訊息
- 能說明為什麼同源測試頁對舊系統維運很重要
- 能比較 jQuery AJAX 與 Axios 在維運上的差異