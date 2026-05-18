# Exercises

本課練習不只驗證「會不會寫 SQL 或註解」，而是要求學員能從資料正確性、交易一致性、效能與排查思路四個面向回答問題。

## 練習索引

- scenario-01-complex-query：複雜 join 與彙總查詢結果異常
- scenario-02-jdbc-rollback：JDBC 手動交易回滾失敗
- scenario-03-transactional-failure：`@Transactional` 失效與 self-invocation
- scenario-04-checked-exception-rollback：checked exception 拋出但資料未回滾
- scenario-05-concurrent-transaction-conflict：並發更新造成交易衝突與資料不一致
- scenario-06-readonly-reporting-and-timeout：大型報表查詢、readOnly 與 timeout 排查

## 建議作答方式

1. 先用一句話描述問題發生在哪一層。
2. 再列出排查順序，不要直接跳到修改程式。
3. 接著寫出修正內容與理由。
4. 最後補上完整參考答案或驗證方式。

## 評核重點

1. 是否能明確指出 SQL、JDBC、MyBatis、交易控制各自的責任。
2. 是否能提出可驗證的排查順序。
3. 是否能說明修改背後的技術原因。
4. 是否能把答案落回資料庫狀態或測試結果，而不是只停在口頭解釋。

## 建議評分方式

- 問題辨識：25%
- 技術修正：30%
- 解題邏輯：25%
- 驗證與說明：20%

## 常見作答錯誤

- 只貼 SQL 或註解，不說明為什麼這樣設計。
- 只看例外訊息，不回資料庫確認最終資料狀態。
- 一發現 rollback 沒生效，就只盯著 `@Transactional`，忽略呼叫路徑與例外型別。
- 把效能題全部歸咎於資料庫，而不檢查索引與 SQL 結構。