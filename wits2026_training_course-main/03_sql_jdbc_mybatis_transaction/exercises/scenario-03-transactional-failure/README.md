# Scenario 03 — @Transactional 失效

## 背景

學員已加上 @Transactional，但核保流程中第二段資料寫入失敗時，第一段更新仍然進資料庫。排查後懷疑是 self-invocation 或例外型別處理不當。

## 任務

1. 分析為什麼 @Transactional 沒有生效。
2. 提出修正方案。
3. 用簡單案例驗證 rollback 是否恢復正常。