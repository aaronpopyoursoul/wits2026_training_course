# Scenario 06 — 大型報表查詢、readOnly 與 timeout

## 背景

一條保費與理賠綜合報表查詢在資料量增加後耗時明顯上升，甚至拖慢整個應用服務。團隊只想直接把 timeout 調大，但沒有先搞清楚瓶頸。

## 任務

1. 說明這題應該從 SQL、索引、readOnly、timeout 哪個順序排查。
2. 解釋 `readOnly = true` 的語意與限制。
3. 給出完整參考答案，說明為什麼不能只調大 timeout。

## 解題方向

- 先看查詢結構與索引，再看 transaction 設定。
- readOnly 不是效能萬靈丹，但在語意與資源管理上有價值。
- timeout 應當最後調整，而不是第一反應。

## 完整參考答案

標準答案重點：

1. 先查 explain / 索引 / sorting / join。
2. 再決定是否標示為只讀交易。
3. 最後才看 timeout 值是否合理。