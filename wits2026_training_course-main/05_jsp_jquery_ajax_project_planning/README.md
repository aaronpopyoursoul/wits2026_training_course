# JSP / jQuery / AJAX / 專題企劃 課程

> 課程資料夾：05_jsp_jquery_ajax_project_planning
> 對象：已完成 04 課程，準備從既有舊系統維運與 API 消費角度理解前後端協作的工程師
> 技術基線：JSP、Prototype.js、jQuery、AJAX、Axios、OpenAPI 3

## 課程定位

這門課的目的不是把學員培養成前端框架工程師，而是讓學員看懂既有 JSP / jQuery 系統如何被維運、局部改造、逐步接上 API，並進一步學會在專題啟動階段定義題目、系統架構與 API 契約。

企業現場常見的不是全新前端，而是：

- 有一批 JSP 頁面還在跑
- 頁面中混著 Prototype.js、jQuery 與手寫 JavaScript
- 部分功能已經有 REST API，部分仍是 server-side render
- 團隊想改，但沒辦法一次推倒重來

這門課要教的是「如何在這種真實條件下工作」。

## 為什麼這些主題要放在同一課

以下主題其實是同一條演進路線：

- JSP 伺服器端渲染
- Prototype.js / jQuery 的 DOM 與事件操作
- 傳統 AJAX 呼叫
- Axios 現代化 HTTP 呼叫
- 專題團隊編組、題目定義、架構選型與 API 規格約定

原因是，很多企業專案不是從零開始，而是必須先維運既有 JSP 或 jQuery 頁面，再逐步過渡到分層更清楚的 API 消費模式。若學員不理解這段歷史與技術債，後面做專題時很容易只會講新技術名詞，卻不知道現場系統怎麼修、怎麼接、怎麼分階段替換。

## 你會學到什麼

- 能說明 JSP 伺服器端渲染的基本流程
- 能看懂 Prototype.js 與 jQuery 的 DOM / 事件 / AJAX 寫法
- 能比較 jQuery AJAX 與 Axios 的差異
- 能說明舊系統維運時，哪些頁面應先補 API 測試頁，哪些不適合立即重寫
- 能製作一個成功呼叫後端 API 的前端測試頁面
- 能完成一份具初步商業邏輯、技術架構與 API 契約的專題企劃書

## 本週學習目標

- 看懂舊系統頁面的伺服器端渲染與前端事件流程
- 能從既有 jQuery / Prototype.js 程式碼推回原始需求
- 能把既有頁面改造成呼叫 API 的測試頁
- 能知道何時該先補 API 契約，而不是直接改畫面
- 能以團隊啟動角度，定義題目、範圍、角色與里程碑

## 交付標準

1. 有一份專題企劃書初稿。
2. 有一份 API 規格草案。
3. 有一個能成功發送 Axios 或 AJAX 請求的前端測試頁面。
4. 能說明為什麼此專題採用該架構，而不是只列工具名稱。
5. 能說明舊系統維運的過渡策略，而不是只提出全面重寫。

## 新版模組地圖

這次把 05 拆成 6 個模組，讓學員能把 legacy 維運、局部重構、API 消費、專題啟動與治理分開理解，而不是把所有舊系統問題都歸成「jQuery 很舊」。

- module-01-jsp-server-rendering：JSP 與伺服器端渲染
- module-02-prototype-jquery-legacy-dom：Prototype.js / jQuery 舊式頁面互動
- module-03-ajax-axios-api-client：AJAX 與 Axios API 消費
- module-04-project-planning-and-api-contract：專題啟動、題目定義與 API 契約
- module-05-jstl-security-and-progressive-refactor：JSTL、XSS、表單生命週期與漸進式重構
- module-06-project-delivery-and-team-collaboration：角色分工、API 治理、里程碑與交付檢查
- exercises：情境練習與啟動題
- instructor-guide：講師教案與帶課腳本
- frontend-demo：JSP / jQuery / Axios 示範頁
- project-kit：專題企劃與 API 規格模板

## 預估學習時數

- 主教材閱讀與授課：7-9 小時
- exercises 與專題討論：4-5 小時
- demo 對照與練習：3-4 小時

## 可直接對照的教材資產

- [frontend-demo/README.md](frontend-demo/README.md)：示範頁與教學路徑
- [project-kit](project-kit)：專題企劃與 API 規格模板
- [exercises/README.md](exercises/README.md)：情境題與評核方式

## 本輪補強主題

這一輪特別補入過去容易被略過，但對舊系統維運非常重要的內容：

- JSTL 與 server-side rendering 的資料流
- XSS 與表單生命週期
- 事件委派與 legacy 頁面插件化思維
- 舊系統 API 契約治理與 migration planning
- 專題啟動時的角色分工、里程碑與風險定義

## 建議授課方式

### 第一段：先看懂舊系統

不要一開始就批評技術舊。先讓學員能閱讀：

- request attribute 從哪裡來
- JSP 如何渲染資料
- jQuery 點擊事件如何帶動畫面互動

### 第二段：再做局部現代化

不要先談 SPA 或框架遷移，先做：

- 補一個 jQuery AJAX 測試頁
- 再補一個 Axios 測試頁
- 讓學員體會「同一 API 可被不同頁面型態消費」

### 第三段：最後拉回專題規劃

帶學員回答：

- 這個專題解決什麼問題
- 保留哪些舊系統元件
- API 先定義哪些欄位
- 前後端如何分工與驗收

## 範例學習路徑

### Step 1：看懂 JSP 頁面資料來源

```jsp
<h2>保單摘要</h2>
<p>保單號：${policy.policyNo}</p>
<p>保戶：${policy.holderName}</p>
```

### Step 2：看懂 jQuery 事件與 AJAX

```javascript
$("#queryPolicyButton").on("click", function () {
	var policyNo = $("#policyNo").val();

	$.ajax({
		url: "/api/policies/" + policyNo,
		method: "GET",
		success: function (response) {
			$("#result").text(JSON.stringify(response, null, 2));
		},
		error: function (xhr) {
			$("#result").text(xhr.responseText);
		}
	});
});
```

### Step 3：改成 Axios 版本

```javascript
async function queryPolicy() {
	const policyNo = document.getElementById("policyNo").value;
	const response = await axios.get(`/api/policies/${policyNo}`);
	document.getElementById("result").textContent = JSON.stringify(response.data, null, 2);
}
```

### Step 4：把這些能力收斂成專題提案

最後要求學員回答：

- 哪些頁面保留 JSP
- 哪些功能先改成 API 呼叫
- 哪些欄位要先定義成 OpenAPI

## 與前一門課的銜接

04 建好 REST API、JWT 與 OpenAPI 之後，05 就從「API 的消費端」切入。學員在這一週會同時看到：

1. 傳統 JSP 頁面如何依賴伺服器端渲染
2. 舊系統如何用 jQuery AJAX 與 API 串接
3. 新頁面如何改用 Axios 管理非同步請求
4. 專題在啟動時如何先談 API 契約與系統架構，再談實作

## 同源 demo

若要直接跑 05 的前端測試頁，現在可透過 04 課程專案內建的同源示範頁：

- `/legacy-demo/jquery-ajax-tester.html`
- `/legacy-demo/axios-api-tester.html`

這些頁面已放入 04 的 Spring Boot 靜態資源中，可直接用相對路徑呼叫 JWT API，避免瀏覽器跨來源限制。

## 補充練習方向

### 練習題 4

請設計一題 JSP 頁面輸出使用者輸入，卻沒有適當 escaping 的情境，說明 XSS 風險與修正方式。

### 練習題 5

請設計一題 legacy 頁面使用動態新增 DOM，導致原本綁定的 click 事件失效的情境，說明事件委派如何處理。

### 練習題 6

請設計一題專題啟動會議，要求學員寫出「哪些頁面保留 JSP、哪些功能先 API 化、哪些風險要先曝光」的簡版方案。