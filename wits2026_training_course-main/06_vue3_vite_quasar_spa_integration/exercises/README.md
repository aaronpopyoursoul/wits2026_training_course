# Exercises — Vue SPA / JWT / CORS / Legacy Integration

> 這些練習的目標不是背 Vue 語法，而是訓練學員在現代前端專題中分辨：問題到底來自路由、狀態、JWT、跨域，還是與既有 JSP 系統的整合策略。

## 練習索引

- scenario-01-jwt-route-guard：登入成功但受保護頁仍無法進入
- scenario-02-cors-preflight-failure：Vite 前台呼叫 Spring Boot API 被跨域擋下
- scenario-03-vue-jsp-coexistence：Vue 前台與既有 JSP 導覽並存混亂
- scenario-04-token-refresh-and-401-loop：token 失效後前端重導流程混亂
- scenario-05-component-boundary-and-slot-misuse：元件責任、slots 與 composables 邊界混亂
- scenario-06-build-env-and-deploy-mismatch：本機可跑但部署後 base URL / router 路徑失效

## 評核重點

1. 是否能明確描述問題發生在哪一層。
2. 是否能提出可驗證的排查順序。
3. 是否能說明修正背後的技術原因。
4. 是否能把修正轉化為可交付的前端專案設計。

## 建議評分方式

- 問題辨識：30%
- 技術修正：30%
- 說明能力：20%
- 架構與交付思考：20%

## 補充作答要求

1. 每題都要先判斷問題層次，不要直接改程式碼。
2. 至少提出一條可驗證的排查路徑。
3. 答案要同時包含解題方向與完整參考答案。