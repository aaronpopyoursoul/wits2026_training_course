# Scenario 04 — Checked Exception 拋出但資料未回滾

## 背景

服務方法在第二段更新後拋出 checked exception，開發者以為交易會自動 rollback，結果資料仍部分寫入。

## 任務

1. 判斷問題是否來自例外型別。
2. 說明為什麼 `@Transactional` 預設不一定處理 checked exception。
3. 提出修正方案並驗證資料是否回滾。

## 排查步驟

1. 先確認拋出的例外型別。
2. 再檢查 `@Transactional` 是否有 `rollbackFor`。
3. 最後回資料表確認最終狀態。

## 解題方向

- 這題不能只看有沒有報錯，必須看資料是否真的被回滾。
- 若是 checked exception，通常要補 `rollbackFor = Exception.class` 或更明確的例外類型。

## 完整參考答案

關鍵答案：

1. 問題在例外型別與 rollback 規則不匹配。
2. 修正不只是加註解，還要補測試驗證資料表狀態。