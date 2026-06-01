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

#### 完整 router/index.ts 範例

```ts
// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/pages/LoginPage.vue')
    },
    {
      // 受保護區塊：以共用 Layout 包裹所有需要登入的頁面
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('@/pages/DashboardPage.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'policies',
          name: 'policies',
          component: () => import('@/pages/PolicySummaryPage.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'claims',
          name: 'claims',
          component: () => import('@/pages/ClaimProgressPage.vue'),
          meta: { requiresAuth: true }
        }
      ]
    },
    {
      // 404 fallback
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/pages/NotFoundPage.vue')
    }
  ]
})

export default router
```

設計說明：
- 受保護頁面集中在 `/` 之下，以 `MainLayout` 包裹，避免每頁重複 nav/header。
- `meta: { requiresAuth: true }` 集中維護，後續 guard 只需讀這個欄位。
- 使用動態 import（`() => import(...)`）做程式碼分割，加快首頁載入。

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

#### 完整 useAuthStore.ts 範例

```ts
// src/stores/useAuthStore.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '@/services/authService'

export const useAuthStore = defineStore('auth', () => {
  // ── State ────────────────────────────────────────────────
  const token = ref<string>('')
  const username = ref<string>('')

  // ── Getters ──────────────────────────────────────────────
  const isAuthenticated = computed(() => !!token.value)

  // ── Actions ──────────────────────────────────────────────

  /** 頁面重新整理時，從 localStorage 補回狀態 */
  function hydrateFromStorage() {
    const savedToken = localStorage.getItem('accessToken')
    const savedUsername = localStorage.getItem('username')
    if (savedToken) {
      token.value = savedToken
      username.value = savedUsername ?? ''
    }
  }

  /** 呼叫 login API，成功後更新 store 並持久化 */
  async function login(credentials: { username: string; password: string }) {
    const { token: accessToken } = await authService.login(credentials)
    token.value = accessToken
    username.value = credentials.username
    localStorage.setItem('accessToken', accessToken)
    localStorage.setItem('username', credentials.username)
  }

  /** 清除所有登入狀態 */
  function logout() {
    token.value = ''
    username.value = ''
    localStorage.removeItem('accessToken')
    localStorage.removeItem('username')
  }

  return { token, username, isAuthenticated, hydrateFromStorage, login, logout }
})
```

注意事項：
- `token` 是 `ref`，Pinia devtools 可以追蹤每次變化；localStorage 只是持久化媒介，不能當響應式狀態用。
- `login` 把 HTTP 呼叫委派給 `authService`，store 本身不直接碰 axios，職責清楚。
- `hydrateFromStorage` 需要在頁面 mount 前執行一次（見下方 main.ts）。

#### main.ts：啟動時補回 token 狀態

```ts
// src/main.ts
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/useAuthStore'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)
app.use(router)

// 在 mount 之前先從 localStorage 補回 token
// 這樣 route guard 執行時才能正確判斷 isAuthenticated
const authStore = useAuthStore()
authStore.hydrateFromStorage()

app.mount('#app')
```

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

#### 完整 guard 安裝方式

```ts
// src/router/guards.ts
import type { Router } from 'vue-router'
import { useAuthStore } from '@/stores/useAuthStore'

export function setupRouterGuard(router: Router) {
  router.beforeEach((to) => {
    const authStore = useAuthStore()

    // 已登入者訪問 /login → 直接送到 dashboard
    if (to.name === 'login' && authStore.isAuthenticated) {
      return { name: 'dashboard' }
    }

    // 未登入者訪問受保護頁面 → 帶著 redirect 送到 /login
    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
      return {
        name: 'login',
        query: { redirect: to.fullPath }
      }
    }
  })
}
```

```ts
// src/router/index.ts：建立 router 後呼叫
import { setupRouterGuard } from './guards'

const router = createRouter({ ... })
setupRouterGuard(router)
export default router
```

登入成功後如何消耗 `redirect` 查詢參數：

```ts
// src/pages/LoginPage.vue（script setup 片段）
const route = useRoute()
const router = useRouter()

async function handleLogin() {
  await authStore.login({ username: username.value, password: password.value })
  // 若使用者是被 guard 從某個受保護頁面導過來的，登入後送回原頁
  const redirect = route.query.redirect as string | undefined
  router.push(redirect ?? { name: 'dashboard' })
}
```

### Step 4：建立 axios service layer

最小範例：

```ts
import axios from 'axios'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
})
```

並在 request interceptor 中注入 Bearer token。

#### src/services/http.ts ——— 基礎 axios 實例

```ts
// src/services/http.ts
import axios from 'axios'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})
```

> ⚠️ interceptor 不要直接在這個檔案裡 import Pinia store，因為 Pinia 必須等 `createApp()` 之後才能使用。  
> 請把 interceptor 邏輯抽到 `setupInterceptors.ts`，在 `main.ts` 的 `app.mount()` 之前呼叫。

### Step 4-1：request interceptor 為什麼重要

如果每個頁面都自己加 Authorization header，很快就會出現：

- 有些頁面忘記加
- token 格式不一致
- 401 行為散在各處

所以這門課要刻意把 header 注入集中在 service layer。

#### 完整 setupInterceptors.ts 範例

```ts
// src/services/setupInterceptors.ts
import { http } from './http'
import { useAuthStore } from '@/stores/useAuthStore'
import router from '@/router'

export function setupInterceptors() {
  // ────────────────────────────────────────────────────────
  // Request interceptor：統一帶入 Bearer token
  // ────────────────────────────────────────────────────────
  http.interceptors.request.use(
    (config) => {
      const authStore = useAuthStore()
      if (authStore.token) {
        config.headers.Authorization = `Bearer ${authStore.token}`
      }
      return config
    },
    (error) => Promise.reject(error)
  )

  // ────────────────────────────────────────────────────────
  // Response interceptor：統一處理 401
  // ────────────────────────────────────────────────────────
  http.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401) {
        const authStore = useAuthStore()
        authStore.logout()             // 清 store 狀態與 localStorage
        router.push({ name: 'login' }) // 導回登入頁
      }
      return Promise.reject(error)
    }
  )
}
```

```ts
// src/main.ts（補充）
import { setupInterceptors } from './services/setupInterceptors'

// 在 Pinia use() 之後、app.mount() 之前呼叫
setupInterceptors()
app.mount('#app')
```

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

以下用實際的 `LoginPage.vue` 說明每一層如何分工合作：

```vue
<!-- src/pages/LoginPage.vue -->
<template>
  <div class="login-wrapper">
    <h2>登入</h2>
    <form @submit.prevent="handleLogin">
      <input v-model="username" placeholder="帳號" required />
      <input v-model="password" type="password" placeholder="密碼" required />
      <p v-if="error" class="error-msg">{{ error }}</p>
      <button type="submit" :disabled="loading">
        {{ loading ? '登入中...' : '登入' }}
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/useAuthStore'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function handleLogin() {
  loading.value = true
  error.value = ''
  try {
    // 1. page 拿到表單資料，交給 store 處理 login 邏輯
    await authStore.login({ username: username.value, password: password.value })
    // 2. login 成功後，根據 redirect 查詢參數決定去哪
    const redirect = route.query.redirect as string | undefined
    router.push(redirect ?? { name: 'dashboard' })
  } catch {
    error.value = '帳號或密碼錯誤'
  } finally {
    loading.value = false
  }
}
</script>
```

這個範例裡每層各司其職：

| 層級 | 責任 |
|---|---|
| `LoginPage`（page） | 收集表單、觸發 login action、顯示錯誤訊息 |
| `useAuthStore`（store） | 呼叫 authService、更新狀態、持久化 token |
| `authService`（service） | 送出 HTTP 請求（axios） |
| `route guard`（router） | 判斷是否允許進入 `/login` 以外的頁面 |
| `http interceptor`（service） | 統一帶 Bearer token、統一處理 401 導頁 |

> authService 範例（供參考）：
>
> ```ts
> // src/services/authService.ts
> import { http } from './http'
>
> export const authService = {
>   login(credentials: { username: string; password: string }) {
>     return http
>       .post<{ token: string }>('/api/auth/login', credentials)
>       .then((res) => res.data)
>   }
> }
> ```

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