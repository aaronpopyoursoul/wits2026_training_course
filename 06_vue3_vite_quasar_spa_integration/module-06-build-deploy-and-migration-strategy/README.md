# Module 06 — Build、Deploy、Base URL 與 Migration Strategy

## 模組目標

這一模組要把學員從「本機開發成功」帶到「可交付、可部署、可規劃遷移」。

很多前端初學者會以為：

- `npm run dev` 可以跑就代表完成了
- 只要 build 成功就代表可以部署
- migration strategy 只要寫「逐步替換」就夠了

這些理解都不夠。真正交付時，還要面對：

- API base URL 與環境差異
- router base 與部署子路徑
- 靜態資源路徑
- SPA 子路由重整 fallback
- 新系統與舊系統如何分階段共存

學完後，學員應能：

1. 說明開發環境與部署環境的差異。
2. 說明 `env`、`VITE_API_BASE_URL`、router base 各自的角色。
3. 設計一份最小部署檢查清單。
4. 寫出具體的 Vue / JSP migration strategy，而不是抽象口號。

## 先備知識

- 已完成前端頁面、API 串接與登入流程
- 已理解 module-04 的 CORS 與系統共存概念
- 已理解 module-05 的資料狀態與錯誤處理

## 情境說明

在開發機上，前端透過 Vite dev server 執行，一切看起來都正常。可是部署到測試環境後，可能立刻發生：

- API 打到錯的 host
- CSS / JS 靜態資源 404
- 直接進入子路由可以，但重新整理就 404
- Vue 與 JSP 都存在，卻沒有人知道哪些路徑該交給誰

這一章的目的，就是讓學員理解：

> 前端可以畫出畫面，不代表它已經準備好被交付。

## 本章核心問題

1. 為什麼 `npm run dev` 跟部署後的行為會不同？
2. `VITE_API_BASE_URL` 與 router base 各自控制什麼？
3. 為什麼首頁正常、子頁重整卻 404？
4. migration strategy 為什麼不能只寫「逐步替換」？

## 教學地圖

1. 先區分 dev 與 deploy
2. 再整理 build 與部署檢查點
3. 最後把 migration strategy 寫成可執行方案

---

## Step 1：先理解 dev 與 deploy 的差異

### Step 1-1：本機可跑，只代表開發條件成立

`npm run dev` 正常，通常只代表：

- Vite dev server 有起來
- 本機 `.env` 有設對
- 你目前的 router 路徑在開發環境可運作

但這不保證正式環境也會一樣，因為部署後通常會改變：

- domain
- path prefix
- API host
- 靜態檔案位置
- server fallback 規則

### Step 1-2：把常見問題先列出來

部署時最常出錯的四件事：

1. API base URL 指到錯的地方
2. router base 與實際部署位置不一致
3. 靜態資源路徑不正確
4. 子路由重整時，伺服器沒有把請求導回 `index.html`

這四點要變成學員的固定檢查習慣。

---

## Step 2：理解 `env`、API base URL 與 router base

### Step 2-1：`env` 是環境差異，不是雜項設定

最小概念：

- `.env.development` 給本機開發用
- `.env.production` 給正式環境 build 用
- 變數名稱要有一致規則

在 Vite 中，前端可讀的環境變數通常以 `VITE_` 開頭。

例如：

```env
VITE_API_BASE_URL=http://localhost:8082
```

### Step 2-2：API base URL 控制的是 HTTP request 的根路徑

現有專案對照：

- `frontend-demo/src/services/http.ts`

最小寫法：

```ts
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8082'

export const http = axios.create({
	baseURL: apiBaseUrl,
})
```

教學時要點出：

- `VITE_API_BASE_URL` 控制的是打 API 的位置
- 它不控制前端頁面路由
- 它也不等於部署目錄路徑

### Step 2-3：router base 控制的是前端路由的根路徑

如果前端部署在網站根目錄，可能用 `/` 就夠了。

但如果部署在子路徑，例如：

- `/app/`
- `/portal/`

那 router 與 build 設定都要一起調整。

最小正確觀念：

```ts
history: createWebHistory(import.meta.env.BASE_URL)
```

這個概念在目前 `frontend-demo/src/router/index.ts` 還沒有完整示範，因此很適合作為教材中的「部署前必補」重點。

### Step 2-4：Vite `base` 與 router base 要對齊

如果部署在 `/app/`，常見設定會長這樣：

```ts
export default defineConfig({
	base: '/app/',
})
```

對應的 router：

```ts
createWebHistory(import.meta.env.BASE_URL)
```

這兩者不對齊時，很容易出現：

- 首頁看起來正常
- 重新整理子頁 404
- JS/CSS 檔案抓錯位置

---

## Step 3：理解 build 成功不等於部署可用

### Step 3-1：`dist` 只是輸出結果，不是保證書

當你執行 `npm run build`，Vite 會產生 `dist`。但真正要檢查的是：

- 產出的 HTML 內引用的資源路徑正不正確
- API base URL 是否是目標環境需要的值
- router base 是否符合部署位置

### Step 3-2：最小部署檢查清單

部署前至少檢查這五項：

1. `.env` 是否對應正確 API 位址
2. Vite `base` 是否符合部署目錄
3. router history base 是否與 Vite `base` 對齊
4. 子路由重整時，伺服器是否有 fallback 到 `index.html`
5. 未登入導向、404 頁面、靜態資源是否仍正常運作

### Step 3-3：最小 `vite.config.ts` 示意

```ts
export default defineConfig({
	base: '/app/',
	plugins: [vue(), quasar()],
	server: {
		port: 5173,
	},
})
```

現有專案對照：

- `frontend-demo/vite.config.ts`

### Step 3-4：為什麼子路由重整會 404

當使用者直接打開 `/app/policies` 時：

- 瀏覽器會對伺服器要 `/app/policies`
- 但伺服器如果只認得實體檔案，不知道這是 SPA 路由
- 就會回 404

所以部署 SPA 必須有 fallback，例如：

```nginx
location /app/ {
		alias /var/www/vue-spa/;
		try_files $uri $uri/ /app/index.html;
}
```

這一段要讓學生知道：

- 404 不一定是 Vue Router 寫錯
- 很多時候是伺服器沒有把前端路由導回入口頁

---

## Step 4：lazy routes 要怎麼放進部署與交付脈絡

### Step 4-1：lazy loading 不是語法展示，而是交付策略

當前台頁面越來越多時，把每個頁面都打包進同一個初始 bundle，會讓第一次載入變慢。

因此可以改成：

```ts
const PolicySummaryPage = () => import('@/pages/PolicySummaryPage.vue')
```

這個作法的真正價值在於：

- 首次載入更輕
- 不常用頁面延後下載
- 更符合實際上線後的效能考量

### Step 4-2：但不要把它教成萬靈丹

要提醒學生：

- lazy routes 不是所有部署問題的解法
- 它解的是載入策略，不是 base path 或 fallback 問題

---

## Step 5：把 migration strategy 寫成可執行方案

### Step 5-1：不能只寫「逐步替換」

這句話太空泛，無法驗收，也無法排程。

一份最小可執行的 migration strategy，至少要回答：

1. 哪些頁面先改成 Vue
2. 哪些頁面暫時保留 JSP
3. 使用者怎麼在新舊頁面間切換
4. 每個階段怎麼驗收

### Step 5-2：最小 migration 範例

可以這樣教：

| 階段 | 範圍 | 原因 | 驗收方式 |
| --- | --- | --- | --- |
| 第 1 階段 | 登入頁、查詢前台 | 使用者量大、互動高、最能體現 SPA 價值 | 新前台可獨立登入與查詢 |
| 第 2 階段 | 高互動業務頁 | 狀態管理複雜，適合 Vue | 使用者可在新前台完成主要流程 |
| 第 3 階段 | 低互動舊後台 | 變動少，暫時保留 JSP 風險較低 | 評估是否保留或再重寫 |

### Step 5-3：路徑切分也要一起寫

與 module-04 呼應，migration strategy 最少應有：

- `/app/**` 給 Vue SPA
- `/legacy/**` 給 JSP
- `/api/**` 給 Spring Boot

如果沒有這種路徑邊界，遷移計畫通常只會停留在口頭上。

---

## 對照現有專案

建議授課時搭配這些檔案：

- `frontend-demo/vite.config.ts`
- `frontend-demo/src/router/index.ts`
- `frontend-demo/src/services/http.ts`

授課順序建議：

1. 先讀 README 中的最小配置概念
2. 再看真實專案檔案，指出目前已具備與尚未補齊的地方
3. 最後回到部署檢查清單，讓學生知道哪些是上線前一定要驗的點

## 常見錯誤

- 把 `.env` 當成只有開發者才需要理解的細節
- 分不清 API base URL 與 router base
- 只看首頁可進，不測子路由重整
- 誤把部署錯誤當成 Vue 語法問題
- migration strategy 只寫一句「逐步替換」
- 把 lazy routes 當成與交付完全無關的小技巧

## 課堂練習

1. 設計一份前端部署檢查清單。
2. 說明 `VITE_API_BASE_URL`、Vite `base`、router base 的差異。
3. 設計一題「首頁正常，但子頁重整 404」的排查順序。
4. 說明 lazy routes 在這個專題中的價值。
5. 寫一份 Vue / JSP 並存的 migration 草案。

## 驗收標準

- 能說明 dev 與 deploy 的差異
- 能區分 API base URL、Vite `base`、router base 的角色
- 能提出最小部署檢查清單
- 能解釋為什麼子路由重整會 404
- 能寫出具體、可執行的 Vue / JSP migration strategy