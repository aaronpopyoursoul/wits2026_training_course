# Module 05 — SQL 效能、索引與資料邊界情境

## 模組目標

這一模組要讓學員知道：SQL 題不只是在語法上答對，還要能在資料量增加、NULL 值、重複資料與 reconciliation 情境下維持正確與可排查。學完後，學員應能回答為什麼同一條查詢在資料量變大後突然變慢，以及為什麼金融系統常需要特別處理 duplicate 與 NULL。

還要讓學員建立一個成熟觀念：SQL 問題通常分成兩類，正確性與效能。這兩類問題經常同時出現，但修法不一定相同。

## 情境說明

某保險報表原本只查單月資料，後來改成查近一年保費與理賠摘要。SQL 還是能跑，但速度大幅下降，且部分結果因 NULL 欄位與重複資料造成金額統計錯誤。這類問題在上課時若不特別拆開，學員很容易只記得 join 語法，不理解資料邊界情境。

## 先建立一個正確心智模型

面對一條查詢時，應先分開問兩件事：

1. 這條查詢結果正不正確
2. 這條查詢在資料量變大後還撐不撐得住

很多初學者只要 SQL 有結果就算完成，但企業實務裡，慢查詢與錯查詢都會造成業務風險。

## 核心重點

- explain 與索引基本觀念
- NULL handling
- duplicate detection
- reconciliation query
- CTE 與 window functions 的使用時機

## 教學步驟

### Step 1：先分清「查不對」與「查太慢」是兩種問題

- 查不對：多半來自 join、where、group by、NULL 處理
- 查太慢：多半來自索引、查詢結構、資料量與排序方式

這裡可進一步補一句：

- 查不對要先看業務鍵、join 粒度、彙總維度
- 查太慢要先看執行計畫、過濾條件、索引與資料分布

### Step 2：理解常見 edge cases

金融與保險系統最常見：

- 欄位為 NULL 造成統計錯誤
- 同一理賠申請重複入帳
- 匯總金額與明細金額對不起來

這一步要讓學員知道 edge case 不是例外題，而是金融資料日常題。

### Step 3：NULL、duplicate、reconciliation 三種問題要分開談

- NULL：值不存在，統計與比較時容易失真
- duplicate：同一商業事件重複出現，可能來自原始資料或 join 展開
- reconciliation：兩邊資料都看似合理，但合計對不起來

這樣拆開後，學員比較不會遇到任何異常都只想加 `distinct`。

### Step 4：導入 CTE 與 window functions

```sql
with latest_payment as (
    select policy_id,
           paid_amount,
           row_number() over (partition by policy_id order by payment_date desc) as rn
    from premium_payment
)
select policy_id, paid_amount
from latest_payment
where rn = 1;
```

這裡要講清楚：

- CTE 讓查詢分段表達，提升可讀性
- window function 適合處理最新一筆、排序排名、分組內計算
- 它們不是高級炫技，而是讓報表與比對查詢更容易維護

## explain 與索引的教學切入點

本章不需要把資料庫優化講成 DBA 課，但至少要讓學員知道：

- explain 是看資料庫怎麼打算執行查詢
- 索引的目的不是為了「有索引」而是為了減少不必要掃描
- where、join、order by 常常決定索引是否有機會被利用

教學上可先建立最低限度判斷力：

- 查詢慢時，不要第一反應就怪資料庫
- 先看條件欄位是否合理
- 再看是否有支撐該查詢模式的索引

## 常見錯誤

- 看到查詢慢就先怪資料庫。
- 對 NULL 不敏感，導致 sum / count 結果誤判。
- 用 `distinct` 強行消重，卻沒有找出真正的重複來源。
- reconciliation 題只做單邊加總，沒有真的找出差異來源。
- 看見 window function 就覺得太進階，回頭用更難讀的巢狀子查詢。
- explain 看不懂就完全跳過，不建立最基本判讀能力。

## 自我檢查清單

- 我能說明 explain 與索引的基本關係嗎？
- 我知道如何辨識重複資料是來自 join 還是原始資料嗎？
- 我能說明 CTE 與 window functions 在報表查詢中的用途嗎？
- 我能分辨這題是在解正確性，還是在解效能嗎？
- 我知道 `coalesce`、`count(*)`、`count(column)` 的差異會如何影響結果嗎？

## 練習題

1. 寫一題找出重複理賠申請的查詢。
2. 寫一題找出最近一期保費紀錄的查詢。
3. 寫一題 reconciliation 查詢，比對理賠台帳與理賠主檔總額。
4. 針對一條近一年報表查詢，列出你會先檢查的 explain / 索引 / where 條件面向。

## 解題方向

1. 重複資料題要說明是以哪個商業 key 判定重複。
2. 最近一期題適合用 window function，不一定只靠子查詢。
3. reconciliation 題要強調「比對」與「找差異」，不是只做單邊總和。
4. 效能題要先建立排查順序，而不是直接宣稱要加索引。

## 完整參考答案

建議答案重點：

- 先界定商業鍵，例如 `claim_no` 或 `policy_no + payment_cycle`
- 對 NULL 做明確處理，例如 `coalesce`
- 對慢查詢先用 explain / 索引 / where 條件順序排查
- 對 duplicate 要先分辨來自原始資料還是 join 展開
- 對 reconciliation 要設計出可找差異來源的查詢，而不是只有總額比較