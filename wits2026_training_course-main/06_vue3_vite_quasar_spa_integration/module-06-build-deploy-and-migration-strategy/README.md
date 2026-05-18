# Module 06 — Build、Deploy、Base URL 與 Migration Strategy

## 模組目標

這一模組要把 06 從開發模式推進到交付思維。學員要能說明本機可以跑，不代表部署可用；也要能提出 Vue SPA 與 JSP 並存時的 migration strategy，而不是只說「之後再慢慢改」。

## 情境說明

專案在 `npm run dev` 下正常，但部署到測試環境後，API base URL、router base path 或靜態資源路徑全部錯掉。另一個常見情況是 Vue 與 JSP 共存，但沒有定義導覽邊界與切換策略，導致使用者體驗混亂。

## 核心重點

- env 與 base URL
- build / dist 基本觀念
- router 路徑與部署位置
- lazy routes
- Vue / JSP migration strategy

## 教學步驟

### Step 1：先理解 dev 與 deploy 差異

本機 dev server 與實際部署的 URL、base path、proxy 條件可能完全不同。

### Step 1-1：本機可跑，常常只是代表「開發環境條件成立」

這句話要在課堂上講得很明白。學員很容易以為 `npm run dev` 正常，就代表專案已可交付，但實際上還有：

- API base URL
- router base
- 靜態資源路徑
- 重新整理子路由 fallback

這些在部署環境都可能跟本機不同。

### Step 1-2：env 與 base URL 的角色

建議用一句話定義：

- env：描述不同環境的變數差異
- base URL：前端打 API 時的根路徑

範例：

```env
VITE_API_BASE_URL=http://localhost:8082
```

這裡要讓學員理解，這不是雜項設定，而是交付成敗的關鍵之一。

### Step 2：整理 deployment checklist

- API base URL
- router base
- 靜態資源路徑
- 錯誤頁與 fallback

### Step 2-1：router base 與部署位置的關係

如果前端不是部署在網站根目錄，而是像：

- `/app/`
- `/portal/`

那 router 與 build 設定通常都要配合。否則常見結果就是：

- 首頁可進
- 子頁面重整 404
- 靜態資源抓不到

### Step 2-2：lazy routes 的角色

這一章除了部署，也應補一下 lazy routes 的意義：

- 首次載入較輕
- 大型前台頁面可分段載入

但要提醒學員：lazy loading 是效能與交付策略，不是只是語法技巧。

### Step 3：設計 migration strategy

至少要定義：

- 哪些頁面先用 Vue
- 哪些頁面暫留 JSP
- 導覽如何切換
- 驗收怎麼切分

### Step 3-1：migration strategy 需要回答的四個問題

1. 哪些頁面優先改
2. 哪些頁面暫時保留
3. 使用者如何在新舊頁面間切換
4. 每一階段怎麼驗收

### Step 3-2：不要只寫「逐步替換」

這是最常見但最沒用的答案。真正可執行的 migration strategy 至少要有：

- 頁面清單
- 路徑切分
- 風險說明
- 里程碑

## 範例：最小部署檢查清單

- `.env` 是否對應正確 API 位址
- router base 是否符合部署路徑
- 重新整理子路由時伺服器是否有 fallback 設定
- 靜態資源路徑是否正確
- 404 與未登入導向是否仍可運作

## 範例：Vue / JSP 漸進式切分

- 第一階段：登入、查詢前台改為 Vue
- 第二階段：高互動功能頁逐步遷移
- 第三階段：低互動或低變動後台頁再評估是否替換

這種切法比一次重寫更符合企業現場的風險控制。

## 常見錯誤

- 把 `.env` 當成只有開發者才需要懂的細節。
- 忘記部署環境與 dev server 的差異。
- migration strategy 只寫一句「逐步替換」。
- 沒理解 router base 對子路由與重整的影響。
- 把 lazy routes 當成與交付無關的小優化。

## 練習題

1. 設計一份前端部署檢查清單。
2. 設計一題 base URL / router 路徑錯誤的排查題。
3. 說明 lazy routes 在此專題中的使用價值。
4. 寫一份 Vue / JSP 並存的 migration 草案。
5. 設計一個部署後子路由重整 404 的排查順序。

## 解題方向

1. 部署題要從環境變數、路由與靜態資源切入。
2. base URL / router 題要先分清是 API 路徑還是前端路由問題。
3. lazy routes 題要從載入策略與頁面規模回答。
4. migration 題要說明切換順序與風險。
5. 不要把部署問題誤認成框架語法問題。

## 完整參考答案

標準答案重點：

- 可交付前端必須能在不同環境穩定解析路徑與 API
- migration strategy 需要具體切分，而不是抽象口號

## 驗收標準

- 能說明 env、base URL、router base 各自的角色
- 能提出最小部署檢查清單
- 能說明 lazy routes 與交付策略的關係
- 能寫出具體的 Vue / JSP migration 草案