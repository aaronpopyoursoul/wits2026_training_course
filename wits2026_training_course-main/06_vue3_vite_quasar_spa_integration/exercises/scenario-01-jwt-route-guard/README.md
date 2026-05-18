# Scenario 01 — JWT 登入成功但受保護頁仍無法進入

## 背景

學員已經能呼叫 `/api/auth/login` 並拿到 access token，但登入後進入 `/dashboard` 仍被導回 `/login`，或刷新頁面後立即失去登入狀態。

## 任務

1. 分析問題是出在 Router、Pinia 還是 token 保存策略。
2. 補上正確的 route guard 與 auth store 邏輯。
3. 驗證登入、刷新頁面與 logout 三個流程。

## 預期方向

- Router：負責根據登入狀態決定是否放行
- Pinia：負責保存 access token 與登入狀態
- Local storage / session storage：僅作持久化輔助，不應成為唯一狀態來源

## 解答方向

1. 問題通常不是 API 沒回 token，而是前端狀態沒有被正確管理。
2. 正確做法是讓 Router 讀取 store 狀態，而不是在每個頁面各自判斷。
3. 若刷新後丟失登入狀態，通常代表 store 初始化時沒有從持久化來源還原。