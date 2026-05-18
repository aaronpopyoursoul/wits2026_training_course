# Demo Script

## Demo 1：舊系統維運不是落後，是現實

1. 開 05 課程總覽。
2. 問學員：如果公司有 200 個 JSP 頁面，第一件事是重寫還是盤點？
3. 拉回本課重點：理解、維運、逐步替換。

## Demo 2：JSP 頁面結構

1. 打開 `frontend-demo/jsp/policy-dashboard.jsp`。
2. 指出哪些內容來自 request attribute。
3. 問學員：這種頁面哪一部分最適合後續改成 AJAX？

## Demo 3：jQuery AJAX 同源測試頁

1. 啟動 04 的 `spring-rest-jwt-demo`。
2. 開 `/legacy-demo/jquery-ajax-tester.html`。
3. 先登入，再查保單摘要。
4. 問學員：這樣的補法對舊系統有什麼價值？

## Demo 4：Axios 測試頁

1. 開 `/legacy-demo/axios-api-tester.html`。
2. 比較 jQuery 與 Axios 在 error handling 與 header 帶法上的差異。
3. 問學員：如果團隊先不重寫畫面，先補 Axios 測試頁，能解決什麼問題？

## Demo 5：專題 kickoff

1. 打開 `project-kit/project-proposal-template.md`。
2. 要求學員先寫題目、使用者、第一階段 API。
3. 不准直接跳去畫 UI，先把商業目標與 API 契約寫清楚。