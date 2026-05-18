# Exercises

本課 exercises 的目標，是讓學員能從真實企業場景回答問題：怎麼維運舊頁面、怎麼局部改造、怎麼避免安全與契約風險、怎麼把專題真的啟動起來。

## 練習索引

- scenario-01-jsp-ajax-bridge：舊式 JSP 頁面如何逐步接上 API
- scenario-02-axios-contract-drift：前端測試頁與 API 契約漂移
- scenario-03-project-architecture-kickoff：專題啟動時的架構與範圍定義
- scenario-04-jsp-xss-and-form-lifecycle：JSP 輸出與表單生命週期造成的 XSS / state 問題
- scenario-05-legacy-event-delegation：動態 DOM、事件失效與 jQuery 事件委派
- scenario-06-project-scope-and-api-governance：專題範圍失控與 API 治理失序

## 建議作答方式

1. 先說明問題出在頁面渲染、事件處理、HTTP 串接還是專題治理。
2. 再列出排查或規劃順序。
3. 最後提出修正方案與完整參考答案。

## 評核重點

1. 是否能理解 legacy 系統的限制，而不是只提出全面重寫。
2. 是否能把頁面問題、API 契約問題與專題治理問題分開處理。
3. 是否能提出可落地的漸進式重構方案。
4. 是否能說明風險與驗收方式。

## 建議評分方式

- 問題辨識：25%
- 技術修正：25%
- 漸進式規劃：25%
- 說明與交付思考：25%