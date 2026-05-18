# Frontend Demo

這個專案是 06 課程的可執行前台骨架，目標是對接 04 的 JWT API，示範：

- Vue 3 + TypeScript + Vite 專案結構
- Quasar UI 元件
- Vue Router + Pinia + JWT route guard
- Axios service layer
- 與 Spring Boot API 的跨域對接

## 預設後端

- Base URL：`http://localhost:8082`
- Login：`POST /api/auth/login`
- Policy Summary：`GET /api/policies/{policyNo}/summary`
- Claim Status：`GET /api/claims/{claimNo}/status`

## 啟動方式

```bash
npm install
npm run dev
```

## 建議環境變數

可建立 `.env`：

```bash
VITE_API_BASE_URL=http://localhost:8082
```

若未設定，專案會預設使用 `http://localhost:8082`。

## 教材對照索引

- `src/main.ts`：Vue / Quasar 啟動入口
- `src/router/index.ts`：route guard 與導航規則
- `src/stores/auth.ts`：token、登入狀態與 local storage
- `src/services/http.ts`：Axios 基礎設定與 401 統一處理
- `src/services/auth.ts`：登入 API 包裝
- `src/services/insurance.ts`：保單與理賠查詢 API 包裝
- `src/composables/useApiState.ts`：loading / error state 管理
- `src/layouts/AppLayout.vue`：主要版面
- `src/pages/*.vue`：各頁面實作

## 建議教學順序

1. 先看 `main.ts`、router、pinia，理解前台骨架。
2. 再看 auth store 與 `bootstrap/auth.ts`，理解登入狀態如何初始化。
3. 接著看 `http.ts` 與 services，理解 API 呼叫邊界。
4. 最後看 pages 與 layout，理解 UI 如何消費這些狀態與服務。

## 建議延伸補強點

- token refresh / session timeout UX
- 表單驗證與欄位錯誤顯示
- lazy-loaded routes
- production base path 與部署設定
- 與既有 JSP 導覽列的整合策略