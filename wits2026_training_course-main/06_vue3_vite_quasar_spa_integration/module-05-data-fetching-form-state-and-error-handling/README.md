# Module 05 — Data Fetching、Form State 與 Error Handling

## 模組目標

這一模組要讓學員把 Vue 專案從「畫面能顯示」提升到「非同步流程能被維護」。學完後，學員應能解釋 loading、error、empty state 為什麼是前端交付品質的一部分，以及 composables、stores、services 該怎麼分工。

## 情境說明

登入頁或查詢頁常見的問題不是 API 打不通，而是：送出後沒 loading、失敗沒有錯誤訊息、資料為空時畫面像壞掉、重複點擊送出造成多次請求。這些問題若不系統化處理，專案很快就會變成大量重複邏輯。

## 核心重點

- loading / error / empty state
- composables 與 services 分工
- 表單送出流程
- 401 / 422 / 500 前端行為
- UI 與 business logic 分離

## 教學步驟

### Step 1：定義非同步頁面的基本狀態

每個資料頁至少應能回答：

- 尚未查詢
- 查詢中
- 查無資料
- 查詢成功
- 查詢失敗

### Step 1-1：不要把「沒資料」和「壞掉了」混在一起

這是很多前端初學者最容易忽略的點。畫面空白不一定是錯誤，可能只是：

- 尚未查詢
- 查詢成功但沒有資料
- API 真的失敗

這三種狀態如果不分清楚，使用者體驗會非常差，開發者也很難排查。

### Step 1-2：用狀態機思維看畫面

建議教學時直接讓學員畫出流程：

- idle
- loading
- success
- empty
- error

這樣學員會比較知道為什麼 `v-if` 不應只寫一個布林值就想處理所有畫面狀態。

### Step 2：決定邏輯放在哪裡

- service：處理 HTTP 呼叫
- composable：整理共通狀態與操作流程
- page / component：處理畫面輸入與顯示

### Step 2-1：service、composable、page 的責任再拆細一點

- service：只管請求與回應，不處理 UI 顯示文字
- composable：包裝查詢流程、loading、error 狀態
- page：處理欄位輸入、按鈕、區塊顯示與頁面組裝

這個切分要講細，否則學員很容易把所有邏輯塞回 page。

### Step 2-2：什麼東西不該進 Pinia

這一章很適合明講：

- 單頁查詢結果
- 暫時性 loading
- 局部錯誤訊息

通常不需要放進 Pinia。Pinia 更適合跨頁共享狀態，例如 auth。

### Step 3：處理表單流程

要特別說明：

- 防重複送出
- 欄位驗證
- API 錯誤如何顯示

### Step 3-1：表單送出流程應有明確順序

建議直接教成固定順序：

1. 清空前次錯誤
2. 做前端欄位驗證
3. 設定 loading
4. 呼叫 API
5. 成功後更新結果
6. 失敗後轉成可理解訊息
7. 結束 loading

### Step 3-2：不同錯誤不應顯示成同一句話

至少應區分：

- 401：登入失效或未登入
- 422 / validation 類：輸入不合法
- 500：系統問題

這不只是 UX 問題，也是前端是否理解 API 契約的表現。

## 範例：最小 API state composable

```ts
import { ref } from 'vue'

export function useApiState() {
	const loading = ref(false)
	const errorMessage = ref('')

	function start() {
		loading.value = true
		errorMessage.value = ''
	}

	function fail(message: string) {
		loading.value = false
		errorMessage.value = message
	}

	function finish() {
		loading.value = false
	}

	return {
		loading,
		errorMessage,
		start,
		fail,
		finish,
	}
}
```

這個範例的教學目的，是讓學員理解共通狀態怎麼抽，不是要他們死背這一版實作。

## 範例：查詢頁面狀態切換

```vue
<template>
	<p v-if="loading">查詢中...</p>
	<p v-else-if="errorMessage">{{ errorMessage }}</p>
	<p v-else-if="!hasSearched">請先輸入查詢條件</p>
	<p v-else-if="items.length === 0">查無資料</p>
	<ResultTable v-else :items="items" />
</template>
```

這段範例要讓學員看到，資料抓取不是只處理成功畫面，而是要處理完整狀態流。

## 常見錯誤

- 每個頁面都自己寫一套 loading / error 邏輯。
- 將所有狀態都塞進 Pinia。
- 把 HTTP error message 直接暴露到 UI。
- 沒有區分 idle、empty、error，導致畫面判斷混亂。
- 送出按鈕沒鎖定，造成重複提交。

## 練習題

1. 設計一個可重用的 API state composable。
2. 說明登入頁與查詢頁的錯誤呈現差異。
3. 畫出一個查詢頁的狀態流轉圖。
4. 設計一題 401 發生後 UI 行為混亂的排查題。
5. 設計一段防重複送出的表單流程。

## 解題方向

1. composable 題要先界定共通流程。
2. 錯誤處理題要區分使用者可理解訊息與技術細節。
3. 狀態流題要至少區分 idle、loading、success、empty、error。
4. 401 題要說明 auth 流程與查詢頁 UI 如何協調。
5. Pinia 只放跨頁共享狀態，不要把所有東西都往 store 丟。

## 完整參考答案

標準答案重點：

- 前端穩定性來自狀態設計，不只來自框架選型
- composable、service、page 要有清楚責任界線

## 驗收標準

- 能區分 idle、loading、success、empty、error
- 能說明 service、composable、page 的責任差異
- 能設計防重複送出與基本錯誤處理流程
- 能把 401、驗證錯誤、系統錯誤做不同前端回應