# Module 03 — Vue Router / Pinia / JWT / Route Guard

## 模組目標

這一模組要把 04 的 JWT API 正式接到 Vue 前台，讓學員能完成登入、保存 token、受保護路由與 401 重導的最小閉環。學完後，學員應能說明為什麼單靠 localStorage 不足以支撐前台架構，以及 Router 與 Pinia 分別解決什麼問題。

## 情境說明

使用者登入後要進入 Dashboard，未登入則不能查看保單摘要與理賠進度頁。如果 token 過期或不存在，前端應自動導回登入頁，而不是等 API 失敗後讓使用者自己猜發生什麼事。

## 核心重點

- Vue Router 路由結構
- Pinia 狀態管理
- JWT token 保存
- route guard
- 401 處理
- axios instance 與 Authorization header

## 教學步驟

### Step 1：定義路由結構

建議最小路由：

- `/login`
- `/dashboard`
- `/policies`
- `/claims`
- `/:pathMatch(.*)*`

### Step 1-1：先讓學員理解「頁面切換」與「資料切換」不同

Vue Router 處理的是前端頁面導航，不是 API 資料狀態本身。這一點若不先講清楚，學員很容易把 route、token、查詢結果都混在一起。

建議直接教學員把 router 想成：

- 哪些 URL 對應哪些頁面
- 哪些頁面需要保護
- 找不到頁面時該去哪裡

### Step 1-2：路由表為什麼要配合 meta

在本課中，`requiresAuth` 不是裝飾，而是告訴 route guard 哪些頁面應先檢查登入狀態。

範例：

```ts
{
  path: '/policies',
  component: PolicySummaryPage,
  meta: { requiresAuth: true }
}
```

這樣的好處是，權限規則與路由定義可以放在一起看。

### Step 2：建立 auth store

Pinia store 的責任應包含：

- 保存 access token
- 保存登入狀態
- 提供 login / logout 行為
- 在頁面刷新時還原必要狀態

### Step 2-1：Pinia 為什麼不是「只是全域變數」

很多學員第一次接觸 Pinia，會把它理解成比較漂亮的 global variable。這樣不夠。Pinia 真正的價值在於：

- 統一狀態來源
- 統一修改入口
- 讓 router、service、layout、page 都能共享同一份 auth 狀態

### Step 2-2：auth store 應包含哪些資料

最小狀態通常至少要有：

- access token
- 是否已登入
- 使用者顯示名稱或基本資訊（若課程需要）

最小行為通常至少要有：

- `login`
- `logout`
- `hydrateFromStorage`

### Step 2-3：為什麼不能只靠 localStorage

`localStorage` 的問題不是不能存，而是：

- 它不是響應式狀態
- 頁面各處若直接讀寫，會讓登入流程失控
- 401 發生時很難統一清理

所以本課要讓學員建立觀念：

- localStorage 是持久化媒介
- store 才是前端狀態的正式入口

### Step 3：建立 route guard

guard 要做的不是驗證 JWT 內容本身，而是根據目前登入狀態與路由 meta 決定是否允許進入。

### Step 3-1：route guard 解的是「前端導航保護」

這一點一定要跟後端 JWT 驗證拆開講：

- route guard：避免未登入使用者直接進入受保護頁面
- 後端 JWT 驗證：真正保護 API 不被未授權請求存取

兩者是不同層次，不可互相取代。

### Step 3-2：最小 guard 範例

```ts
router.beforeEach((to) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
})
```

這一段要帶出的重點：

- guard 要簡潔
- redirect 資訊可保留使用者原本想去的頁面
- 不要把過多業務邏輯塞進 router

### Step 4：建立 axios service layer

最小範例：

```ts
import axios from 'axios'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
})
```

並在 request interceptor 中注入 Bearer token。

### Step 4-1：request interceptor 為什麼重要

如果每個頁面都自己加 Authorization header，很快就會出現：

- 有些頁面忘記加
- token 格式不一致
- 401 行為散在各處

所以這門課要刻意把 header 注入集中在 service layer。

### Step 4-2：response interceptor 與 401 統一處理

response interceptor 適合做：

- token 失效時清除登入狀態
- 導回登入頁
- 讓前端各頁不必各自寫一套 401 處理

### Step 4-3：router、store、service 三者怎麼合作

建議直接用一個流程說明：

1. 使用者在 login page 送出帳密
2. auth store 呼叫 auth service 取得 token
3. auth store 更新狀態並持久化
4. router 允許進入受保護頁
5. service 之後的 API 請求統一帶 token
6. 若遇 401，service 通知清理狀態並導回登入

## 完整閉環範例

```ts
// store
const token = ref('')
const isAuthenticated = computed(() => !!token.value)

function logout() {
  token.value = ''
  localStorage.removeItem('accessToken')
}
```

這段教學的重點不是只讓學員背 API，而是能說出這個閉環裡每層的責任。

## 常見錯誤

- 把 Router 當成狀態管理工具。
- 把所有登入狀態只放 localStorage，不透過 store 統一管理。
- guard 寫得過重，所有驗證細節都塞在 router。
- 401 發生後沒有統一處理，導致每個頁面各自重導。
- 把 route guard 當成真正的安全防線，忽略後端仍必須驗證 JWT。
- 讓 page 直接碰 axios instance 太多細節，導致架構鬆散。

## 自我檢查清單

- 我能說明 Router 與 Pinia 的責任邊界嗎？
- 我知道 token 應如何安全地在前端狀態中使用嗎？
- 我能解釋 route guard 與後端 JWT 驗證是不同層次的保護嗎？
- 我知道 401 應在 service 層統一處理嗎？

## 練習題

1. 建立 `auth` store，保存 access token 並提供 logout。
2. 為 `/dashboard`、`/policies`、`/claims` 加上 `requiresAuth` 路由 meta。
3. 實作一個 route guard，未登入時導回 `/login`。
4. 在 axios interceptor 中統一帶入 Authorization header。
5. 設計一條 401 發生後的前端處理流程圖。

## 練習解答方向

1. store 題要避免直接讓頁面四處操作 localStorage。
2. route guard 題要清楚說明未登入時的導向邏輯。
3. guard 題要分清導頁保護與後端驗證兩層。
4. interceptor 題要指出為什麼 service 層比頁面層更適合做 header 注入。
5. 401 題要說明清 token、更新 store、導頁三步之間的關係。

## 驗收標準

- 能完成 JWT 登入與 token 保存
- 能用 route guard 保護受保護頁面
- 能用 Pinia 管理登入狀態
- 能說明 router、store、service 的責任邊界
- 能統一處理 Authorization header 與 401 錯誤