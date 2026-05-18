# Scenario 02 — JDBC 交易沒有回滾

## 背景

理賠申請流程需要同時更新 claim 與 payout 兩張表。第一段更新成功，第二段丟出例外，但資料庫卻只回滾一半。

## 任務

1. 檢查 autoCommit 設定。
2. 檢查是否在正確位置 commit / rollback。
3. 說明這種錯誤若進入正式環境，會造成什麼帳務風險。