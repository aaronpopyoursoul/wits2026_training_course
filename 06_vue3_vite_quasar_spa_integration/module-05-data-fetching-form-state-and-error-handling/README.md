# Module 05 — Data Fetching、Form State 與 Error Handling

## 模組目標

這一模組要把學員從「畫面有跑出來」帶到「非同步流程可以被維護」。

前端頁面只要開始打 API，就一定會碰到以下問題：

- 請求送出後，畫面要不要顯示 loading
- API 失敗時，要怎麼回饋給使用者
- 沒有資料時，這算錯誤還是正常狀態
- 表單送出時，怎麼避免重複點擊造成多次請求

學完後，學員應能：

1. 區分 idle、loading、success、empty、error 五種狀態。
2. 說明 service、composable、page 的責任分工。
3. 設計一個最小可重用的 API state 管理方式。
4. 根據不同 HTTP 錯誤類型，給出不同的前端回應。

## 先備知識

- 已會 Vue 3 基本 reactive / ref / event handling
- 已理解表單輸入與 router 導頁
- 已看過 module-04 的登入頁、查詢頁與 API 呼叫情境

## 情境說明

初學者常見的問題通常不是「API 完全不會串」，而是以下這些細節沒有被系統化：

- 送出按鈕點了沒反應，使用者不知道是不是壞了
- API 失敗了，但頁面沒有任何提示
- 查無資料與系統錯誤看起來一模一樣
- 重複點擊送出，導致後端收到兩次請求
- 所有頁面都自己寫一份 loading / error 邏輯，最後專案非常難維護

這一模組的目標就是把這些問題變成可教、可複用、可驗收的流程。

## 本章核心問題

1. 一個查詢頁到底有哪些畫面狀態？
2. loading、empty、error 為什麼不能混在一起？
3. service、composable、page 該怎麼分工？
4. 401、422、500 前端要不要顯示同一句話？

## 教學地圖

1. 先定義狀態，而不是先寫 API 呼叫
2. 再決定邏輯放在哪一層
3. 最後設計查詢頁與表單頁的完整流程

---

## Step 1：先定義非同步頁面的狀態

### Step 1-1：每個資料頁至少要回答五件事

一個最小可交付的資料頁，至少要能區分：

- `idle`：尚未查詢
- `loading`：請求中
- `success`：查詢成功且有資料
- `empty`：查詢成功但沒有資料
- `error`：查詢失敗

如果沒有把這五種狀態拆開，最後常見結果是：

- 畫面空白，但不知道是沒查、沒資料、還是 API 壞掉
- 錯誤訊息與空資料訊息混在一起
- 頁面判斷變成一堆互相衝突的 `v-if`

### Step 1-2：不要把「沒資料」當成「出錯」

這是初學者最容易踩的坑。

例如保單查詢：

- 使用者還沒輸入保單號，這是 `idle`
- 使用者有查，但查不到資料，這是 `empty`
- API timeout 或 server 500，這才是 `error`

這三件事情對使用者的意義完全不同，所以畫面也不應該長一樣。

### Step 1-3：用狀態流來教，而不是只用布林值

建議課堂上直接畫這個流程：

```text
idle -> loading -> success
							 -> empty
							 -> error
```

教學重點：

- `loading` 不會直接等於成功
- `error` 不是預設狀態
- `empty` 是成功的一種結果，不是壞掉

---

## Step 2：決定邏輯放在哪裡

### Step 2-1：三層分工先講清楚

可以用這個最小原則：

- `service`：負責 HTTP request / response
- `composable`：負責共通狀態與流程控制
- `page`：負責欄位輸入、事件觸發、畫面顯示

如果不先講清楚，初學者很容易把所有東西都塞進 page component，最後頁面檔案會變得很難維護。

### Step 2-2：每一層該做什麼

| 層級 | 應負責的事 | 不應負責的事 |
| --- | --- | --- |
| service | 打 API、回傳資料、丟出錯誤 | 直接操作 UI、決定畫面文字 |
| composable | 管 loading、error、共通流程 | 直接決定某頁版面結構 |
| page | 收集輸入、呼叫流程、顯示畫面 | 寫重複的 HTTP 邏輯 |

### Step 2-3：什麼不該進 Pinia

這一段一定要講，因為很多初學者會把「所有 state 都丟進 store」當成架構升級。

通常不需要進 Pinia 的狀態：

- 單頁查詢結果
- 頁面自己的 loading
- 一次性的錯誤訊息
- 表單當下輸入中的內容

比較適合進 Pinia 的狀態：

- auth 使用者登入狀態
- access token
- 跨頁共用的使用者資訊

現有專案對照：

- `frontend-demo/src/stores/auth.ts` 只放跨頁共享的 auth 狀態

---

## Step 3：把 API state 抽成最小 composable

### 最小 `useApiState` 範例

```ts
import { ref } from 'vue'

export function useApiState() {
	const isLoading = ref(false)
	const errorMessage = ref('')

	function startLoading() {
		isLoading.value = true
		errorMessage.value = ''
	}

	function finishLoading() {
		isLoading.value = false
	}

	function setError(message: string) {
		errorMessage.value = message
	}

	return {
		isLoading,
		errorMessage,
		startLoading,
		finishLoading,
		setError,
	}
}
```

這個範例對照現有專案：

- `frontend-demo/src/composables/useApiState.ts`

教學時要說清楚：

- 它不是萬能解法
- 它只是把最常重複的 loading / error 狀態抽出來
- 對初學者來說，這已經是很好的一步

### 為什麼先從小版本開始

因為一開始就做太完整，學生會分不清楚：

- 哪些是框架必要知識
- 哪些是專案演進後的優化

這一版只先處理：

- 開始 loading
- 結束 loading
- 記錄錯誤訊息

就已經足夠支撐登入頁與查詢頁。

---

## Step 4：查詢頁的完整狀態流

### 最小查詢頁範例

```vue
<template>
	<q-banner v-if="errorMessage" class="bg-red-1 text-negative q-mb-md">
		{{ errorMessage }}
	</q-banner>

	<p v-if="isLoading">查詢中...</p>
	<p v-else-if="!hasSearched">請先輸入查詢條件</p>
	<p v-else-if="items.length === 0">查無資料</p>
	<ResultTable v-else :items="items" />
</template>
```

這裡要特別帶學生看：

- `!hasSearched` 是 `idle`
- `items.length === 0` 是 `empty`
- `errorMessage` 是 `error`

很多頁面畫面不穩定，不是因為框架不行，而是因為這三種狀態沒有被拆開。

### 對照現有查詢頁

現有專案的 `frontend-demo/src/pages/PolicySummaryPage.vue` 可以拿來示範這個流程：

1. 使用者輸入 `policyNo`
2. 按下查詢按鈕
3. 開始 loading
4. 清掉舊資料
5. 呼叫 service
6. 成功時更新結果
7. 失敗時顯示錯誤訊息
8. 最後結束 loading

### 查詢頁最小 script 範例

```ts
const policyNo = ref('POL20260001')
const policySummary = ref(null)
const hasSearched = ref(false)
const { errorMessage, finishLoading, isLoading, setError, startLoading } = useApiState()

async function loadPolicy() {
	hasSearched.value = true
	startLoading()
	policySummary.value = null

	try {
		policySummary.value = await fetchPolicySummary(policyNo.value)
	} catch {
		setError('查詢失敗，請稍後再試')
	} finally {
		finishLoading()
	}
}
```

這段範例的教學價值在於：

- 先把流程順序固定下來
- 再去追求更完整的錯誤分類

---

## Step 5：表單送出流程要有固定順序

### Step 5-1：推薦的七步驟

表單送出流程建議固定成：

1. 清空前一次錯誤
2. 進行欄位驗證
3. 鎖住送出按鈕
4. 呼叫 API
5. 成功時更新畫面或導頁
6. 失敗時轉成使用者看得懂的訊息
7. 結束 loading，解除按鈕鎖定

這樣的好處是：

- 每個表單頁都能用相似節奏實作
- 學生看到問題時比較知道是哪一步出錯

### 對照現有登入頁

`frontend-demo/src/pages/LoginPage.vue` 是很好的教材，因為它同時包含：

- 表單輸入
- `@submit.prevent`
- loading button
- error banner
- 成功後導頁

### 最小表單範例

```ts
async function submitLogin() {
	startLoading()

	try {
		await authStore.login(form)
		await router.push('/dashboard')
	} catch (error) {
		setError('帳號或密碼錯誤')
	} finally {
		finishLoading()
	}
}
```

### Step 5-2：防重複送出不是小細節

如果送出期間按鈕還能一直點，常見結果是：

- 送出兩次登入請求
- 建立兩筆重複資料
- 錯誤訊息互相覆蓋

最小做法就是：

- `:loading="isLoading"`
- 必要時搭配 `:disable="isLoading"`

這種處理對初學者已經很夠用。

---

## Step 6：不同錯誤要有不同前端回應

### Step 6-1：至少先區分 401、422、500

這不是為了做得很複雜，而是為了避免所有問題都顯示同一句話。

| 狀態碼 | 常見意義 | 前端行為 |
| --- | --- | --- |
| 401 | 未登入或 token 失效 | 清理 auth，導回登入頁 |
| 422 | 欄位驗證失敗 | 把錯誤顯示在表單附近 |
| 500 | 系統錯誤 | 顯示一般性錯誤訊息，請使用者稍後再試 |

### Step 6-2：401 的教學重點

現有專案中的 `frontend-demo/src/services/http.ts` 有 response interceptor，可用來示範：

- 收到 401 時，不一定在每個 page 自己處理
- 可以在較靠近 HTTP 層的位置統一觸發未授權處理

最小概念：

```ts
http.interceptors.response.use(
	(response) => response,
	async (error) => {
		if (error.response?.status === 401 && unauthorizedHandler) {
			unauthorizedHandler()
		}

		return Promise.reject(error)
	},
)
```

### Step 6-3：422 欄位驗證怎麼講

即使目前 demo 沒有完整欄位錯誤對應，也建議 README 先教觀念。

假設 API 回傳：

```json
{
	"message": "validation failed",
	"errors": {
		"username": "帳號不可空白",
		"password": "密碼長度不足"
	}
}
```

前端不應該只顯示 `validation failed`，而是要把欄位錯誤放回對應欄位附近。

這一版課程就算不完整實作，也至少要讓學員知道：

- 欄位錯誤通常不等於系統錯誤
- 422 是 API 契約的一部分

---

## 對照現有專案

建議授課時直接搭配下列檔案：

- `frontend-demo/src/composables/useApiState.ts`
- `frontend-demo/src/pages/LoginPage.vue`
- `frontend-demo/src/pages/PolicySummaryPage.vue`
- `frontend-demo/src/services/http.ts`
- `frontend-demo/src/stores/auth.ts`

對照順序建議：

1. 先講 README 的最小流程
2. 再打開實際程式碼指出對應位置
3. 最後說明哪些是簡化版，哪些是專案版

## 常見錯誤

- 只有成功畫面，沒有 loading / error / empty state
- 每個頁面都手寫一套相似的流程
- 把單頁狀態全部塞進 Pinia
- 把後端原始錯誤直接顯示給使用者
- 沒鎖送出按鈕，導致重複提交
- 沒有把 `idle` 與 `empty` 分開

## 課堂練習

1. 畫出一個查詢頁的狀態流轉圖。
2. 寫一個最小版 `useApiState`。
3. 說明 `service`、`composable`、`page` 的責任差異。
4. 設計一個防重複送出的登入流程。
5. 說明 401、422、500 為什麼不應顯示同一句訊息。

## 驗收標準

- 能區分 idle、loading、success、empty、error
- 能說明 service、composable、page 的責任差異
- 能設計一個最小可重用的 API state 流程
- 能說明表單送出時為什麼要防重複提交
- 能把 401、422、500 做出不同前端回應