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

#### Axios 建立專用 instance 與指定 token 的比較

**方式 A：每次呼叫自己加 header（舊系統測試頁常見）**

```javascript
async function queryClaim() {
  const claimNo = document.getElementById("claimNo").value;
  const token = localStorage.getItem("accessToken");

  try {
    const response = await axios.get(`/api/claims/${claimNo}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    document.getElementById("result").textContent =
      JSON.stringify(response.data, null, 2);
  } catch (error) {
    handleApiError(error);
  }
}
```

**方式 B：建立指定 instance 與 interceptor（推薦方式）**

```javascript
// apiClient.js（偀就舊系統測試頁的建立方式）
const apiClient = axios.create({
  baseURL: "/api",  // 同源時直接用相對路徑
  timeout: 10000,
  headers: { "Content-Type": "application/json" }
});

// Request interceptor：統一帶入 Bearer token
apiClient.interceptors.request.use(function (config) {
  var token = localStorage.getItem("accessToken");
  if (token) {
    config.headers["Authorization"] = "Bearer " + token;
  }
  return config;
});

// Response interceptor：統一處理 401
apiClient.interceptors.response.use(
  function (response) { return response; },
  function (error) {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem("accessToken");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

// 使用：再也不用每次手动加 header
async function queryClaim(claimNo) {
  const response = await apiClient.get(`/claims/${claimNo}`);
  return response.data;
}
```

此方式的好處：
- 每個呼叫不必手动加 header
- token 遗失只需修改 interceptor 一處
- 401 統一導回登入，不散在各頁 handler

## 同源測試頁記得放在後端同源路徑下

### 1-1：同源測試頁在舊系統維運裡的價値

這一段很重要，因為 05 和 04 是一起搞的：

- 把測試頁放在後端同源路徑下
- 可直接驗證 JWT API
- 可避開跨域干擾，先確認契約是否正確

這就是為什麼 05 不只是「前端語法課」，而是 API 消費與維運課。

#### 同源測試頁範例：api-test.jsp

建立方式：將此檔放在 Spring Boot 的 `src/main/resources/static/` 之下，或並就 JSP  webapp 目錄。

```html
<!DOCTYPE html>
<html lang="zh-TW">
<head>
  <meta charset="UTF-8">
  <title>API 測試頁</title>
  <!-- 後端同源：直接引用，不需 CDN -->
  <script src="/static/js/axios.min.js"></script>
</head>
<body>

<h2>JWT API 測試頁</h2>

<!-- 登入測試 -->
<section>
  <h3>1. 登入取得 Token</h3>
  <input id="username" placeholder="帳號" value="admin" />
  <input id="password" type="password" placeholder="密碼" value="123456" />
  <button onclick="doLogin()">登入</button>
  <pre id="loginResult"></pre>
</section>

<!-- 受保護 API 測試 -->
<section>
  <h3>2. 查詢保單（需 token）</h3>
  <input id="policyNo" placeholder="保單號碼" value="PL20240001" />
  <button onclick="queryPolicy()">查詢</button>
  <pre id="policyResult"></pre>
</section>

<p id="errorMessage" style="color:red"></p>

<script>
// 同源路徑：相對路徑即可，不需要 localhost:8082
var BASE_URL = "";

async function doLogin() {
  try {
    var res = await axios.post(BASE_URL + "/api/auth/login", {
      username: document.getElementById("username").value,
      password: document.getElementById("password").value
    });
    localStorage.setItem("accessToken", res.data.token);
    document.getElementById("loginResult").textContent =
      "Token 已取得：" + res.data.token.substring(0, 40) + "...";
  } catch (e) {
    handleApiError(e);
  }
}

async function queryPolicy() {
  var policyNo = document.getElementById("policyNo").value;
  var token = localStorage.getItem("accessToken");
  try {
    var res = await axios.get(BASE_URL + "/api/policies/" + policyNo, {
      headers: { Authorization: "Bearer " + token }
    });
    document.getElementById("policyResult").textContent =
      JSON.stringify(res.data, null, 2);
  } catch (e) {
    handleApiError(e);
  }
}

function handleApiError(error) {
  var msg = document.getElementById("errorMessage");
  if (!error.response) {
    msg.textContent = "網路錯誤：後端未回應";
  } else if (error.response.status === 401) {
    msg.textContent = "401 未授權，請先登入";
  } else if (error.response.status === 404) {
    msg.textContent = "404 查無資料";
  } else {
    msg.textContent = "HTTP " + error.response.status + "：" + JSON.stringify(error.response.data);
  }
}
</script>
</body>
</html>
```

這頁的教學重點：
- `BASE_URL = ""` + 相對路徑 = 同源，完全繞過 CORS。
- `localStorage` 暫存 token，常見於舊系統紧急測試場合。
- 這個測試頁不適合當正式前台，但對於舊系統類型 A 檔偃5莳內驗證 JWT 流程非常實用。

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

#### 分類錯誤處理範例

```javascript
function handleApiError(error) {
  var msgEl = document.getElementById("errorMessage");

  if (!error.response) {
    // 網路錯誤：請求根本沒送出（或後端未啟動）
    msgEl.textContent = "網路錯誤，請確認後端服務是否启動";
    return;
  }

  switch (error.response.status) {
    case 400:
      msgEl.textContent = "請求格式錯誤：" + (error.response.data.message || "請檢查輸入");
      break;
    case 401:
      msgEl.textContent = "未登入或 token 已過期，請重新登入";
      // 可選擇自動導回登入頁
      // window.location.href = "/login";
      break;
    case 404:
      msgEl.textContent = "查無此筆資料，請確認輸入是否正確";
      break;
    case 500:
      msgEl.textContent = "伺服器內部錯誤，請聯繫系統管理員";
      break;
    default:
      msgEl.textContent = "HTTP " + error.response.status + "：操作失敗";
  }
}
```

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