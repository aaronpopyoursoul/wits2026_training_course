# Vue 3 / Vite / Quasar / SPA Integration 課程

> 課程資料夾：06_vue3_vite_quasar_spa_integration
> 對象：已完成 04 與 05，準備把 JWT API、舊系統整合與現代前端框架串成專題前台的工程師
> 技術基線：Vue 3、TypeScript、Vite 5、Quasar 2、Vue Router 4、Pinia 2、Axios

## 課程定位

這門課不是單純介紹 Vue 語法，也不是只做一個漂亮的前端畫面。它的角色，是承接 04 的 JWT API 與 05 的 JSP / jQuery 維運脈絡，把學員正式帶進「現代前端專題如何啟動、如何與既有後端整合、如何與舊架構共存」的實作場景。

如果 05 的重點是理解 legacy 頁面怎麼維護、怎麼以 AJAX / Axios 呼叫 API，那 06 的重點就是把這些零散能力收斂成一個具備登入、路由、狀態管理、UI 元件化、跨域處理與既有系統共存策略的 Vue SPA。

## 你會學到什麼

- 能用 Vite 建立 Vue 3 + TypeScript 前端專案
- 能理解 SFC、Components、props / emit / slots 與 composables 的責任分界
- 能比較 Options API 與 Composition API 的寫法與使用時機
- 能用 Vue Router 與 Pinia 實作 JWT 登入、狀態管理與 route guard
- 能用 Quasar 建立可交付的前台介面
- 能說明 CORS 為什麼發生、後端如何正式開放、前端如何對接
- 能設計 Vue SPA 與既有 JSP 系統並存的漸進式整合策略

## 建議先備知識

- 已完成 04，知道 JWT 登入流程、401 / 403 / 404 的 API 意義
- 已完成 05，知道 JSP、jQuery、Axios 與舊系統維運脈絡
- 知道 JavaScript 基本語法與非同步呼叫概念
- 能看懂 JSON、HTTP header 與 Bearer token 基本格式

## 這門課要解決的真實問題

企業現場很少讓前端從零開始純綠地開發。更常見的是：

1. 後端 API 已存在，而且 JWT 驗證流程已經上線。
2. 系統仍有部分 JSP / jQuery 頁面需要維運。
3. 團隊希望新增一個現代前台，但又不能一次推倒整套舊架構。
4. 本地開發時，前端跑在 `localhost:5173`，後端跑在 `localhost:8082`，立刻碰到 CORS。

06 的課程設計，就是為了讓學員能在這種真實條件下開工，而不是只會跑一個孤立的 Vue demo。

## 本週學習目標

完成本週課程後，學員應能：

- 建立 Vue 3 + TypeScript + Vite + Quasar 專案骨架
- 以元件化方式拆分登入、首頁、保單查詢與理賠查詢頁面
- 用 Pinia 管理登入狀態與 token
- 用 Vue Router 實作受保護頁面與 route guard
- 用 Axios service layer 統一處理 base URL、Authorization header 與 401
- 理解後端 CORS 設定與前端串接的關係
- 說明 Vue SPA 與既有 JSP 頁面如何並存

## 交付標準

1. 有一個可執行的 Vue 3 前台專案骨架。
2. 可成功呼叫 04 的 `/api/auth/login` 取得 JWT。
3. 受保護頁面會在未登入時被 route guard 擋下。
4. 前端能帶 Bearer token 呼叫保單或理賠 API。
5. 文件能清楚說明 CORS、JWT、Vue Router、Pinia 與 JSP 並存策略。

## 新版模組地圖

06 這次從 4 個模組擴成 6 個模組，目的是把「Vue 語法」、「前端架構」、「API 整合」、「UI / 狀態管理」、「部署環境」與「legacy migration」拆開講清楚。

- module-01-vue3-vite-typescript：Vue 3、Vite、TypeScript、SFC 與響應式基礎
- module-02-options-vs-composition-and-components：Options API / Composition API 與元件化設計
- module-03-router-pinia-auth-guard：Vue Router、Pinia、JWT 登入與路由守衛
- module-04-quasar-cors-and-jsp-integration：Quasar UI、CORS 與 Vue + JSP 漸進式整合
- module-05-data-fetching-form-state-and-error-handling：資料抓取、loading / error、表單流程與 composables
- module-06-build-deploy-and-migration-strategy：env / base URL、build / deploy、lazy routes 與 migration strategy
- exercises：情境練習與驗收題
- instructor-guide：講師教案與帶課腳本
- frontend-demo：Vite + Vue 3 + Quasar 可執行前台骨架

## 預估學習時數

- 主教材閱讀與授課：8-10 小時
- exercises 與故障排查：4-6 小時
- frontend-demo 對照與延伸練習：4-5 小時

## 可直接對照的程式檔

- [frontend-demo/README.md](frontend-demo/README.md)：前台骨架導覽
- [frontend-demo/src/router/index.ts](frontend-demo/src/router/index.ts)：route guard 與 page 結構
- [frontend-demo/src/stores/auth.ts](frontend-demo/src/stores/auth.ts)：Pinia auth store
- [frontend-demo/src/services/http.ts](frontend-demo/src/services/http.ts)：Axios instance 與 unauthorized handler
- [frontend-demo/src/bootstrap/auth.ts](frontend-demo/src/bootstrap/auth.ts)：登入狀態初始化
- [frontend-demo/src/layouts/AppLayout.vue](frontend-demo/src/layouts/AppLayout.vue)：Quasar layout
- [frontend-demo/src/pages/LoginPage.vue](frontend-demo/src/pages/LoginPage.vue)：登入頁
- [frontend-demo/src/pages/PolicySummaryPage.vue](frontend-demo/src/pages/PolicySummaryPage.vue)：保單摘要頁
- [frontend-demo/src/pages/ClaimStatusPage.vue](frontend-demo/src/pages/ClaimStatusPage.vue)：理賠狀態頁

## 本輪補強主題

相較前一版，這次補強幾個實務上必須會但常被忽略的主題：

- loading / error / empty state 設計
- composables 與 service layer 分工
- token 失效處理與前端導回流程
- env、base URL、build 與 deploy 基本觀念
- lazy routes、migration strategy 與與 JSP 共存的演進順序

## 建議授課節奏

### 講師授課版

1. 先對照 05 的 Axios 測試頁，說明為什麼現在要升級成 SPA。
2. 示範 Vite + Vue 3 + TypeScript 專案結構與 SFC 寫法。
3. 再帶學員理解 Options API 與 Composition API 的差異。
4. 接著導入 Router、Pinia、JWT 與 route guard。
5. 最後示範 Quasar UI、CORS 處理與 Vue / JSP 並存策略。

### 自學版

1. 先讀本 README，理解 06 與 04、05 的關係。
2. 再依序閱讀四個 module README，把 Vue 基礎、架構與整合問題連起來看。
3. 最後跑 frontend-demo，完成登入、查詢與 CORS 串接驗證。
4. 再做 exercises，練習排查 JWT、跨域與舊系統共存問題。

## 建議主線情境

本課建議以「保險理賠與保單查詢前台」為主線：

1. 使用者先登入取得 JWT。
2. 進入 Dashboard 查看快速導覽。
3. 查詢保單摘要與理賠狀態。
4. 遇到 token 失效時被導回登入頁。
5. 在系統整體架構上，Vue 前台與既有 JSP 後台頁面並存。

## 這份大綱額外補入的 Vue 必要主題

你原始大綱已包含主體技術，但若要完成實際專題，還必須補上以下內容：

- SFC 單檔元件結構
- props / emit / slots
- computed / watch
- composables
- axios service layer
- env 與 API base URL 設定
- 表單驗證與 loading / error 狀態
- build / dist 部署觀念

這些內容都會被正式放進四個 module，而不是只口頭帶過。

## 與前兩門課的銜接

- 04 提供 JWT API、OpenAPI 與錯誤格式
- 05 提供 JSP / jQuery / Axios 與舊系統維運脈絡
- 06 則把兩者結合，正式建立 Vue SPA 專題前台雛形

## 快速導覽

- 課程總覽：本檔案
- Modules：六個 module README
- 練習題：exercises
- 講師教案：instructor-guide
- 前端骨架：frontend-demo