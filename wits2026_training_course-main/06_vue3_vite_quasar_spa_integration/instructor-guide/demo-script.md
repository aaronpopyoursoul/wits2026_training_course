# Demo Script

## Demo 1：從 Axios 測試頁過渡到 Vue SPA

1. 先開 05 的 Axios 測試頁。
2. 問學員：如果現在要做 Dashboard、多頁切換與登入狀態管理，這種頁面還撐得住嗎？
3. 引出 Router、Pinia 與 Components 的必要性。

## Demo 2：建立 Vue 3 + Vite + TypeScript 專案骨架

1. 示範專案目錄結構。
2. 示範 `App.vue`、pages、components、stores、services 的分工。
3. 問學員：哪些檔案屬於畫面，哪些屬於架構。

## Demo 3：JWT 登入與 Route Guard

1. 先展示登入 API。
2. 再展示 Pinia auth store。
3. 最後示範未登入進入 `/dashboard` 被導回 `/login`。

## Demo 4：CORS 與後端正式開放

1. 先故意讓 5173 呼叫 8082 失敗。
2. 再示範後端如何允許 `http://localhost:5173`。
3. 問學員：為什麼這題不應只靠前端繞過。

## Demo 5：Vue 與 JSP 並存

1. 對照 05 的 JSP 頁面與 06 的 Vue 頁面。
2. 討論哪些頁面保留舊系統、哪些先現代化。
3. 讓學員提出專題前後台分工建議。