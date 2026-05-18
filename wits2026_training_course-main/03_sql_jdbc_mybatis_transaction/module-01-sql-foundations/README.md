# Module 01 — SQL 基本語法與查詢思維

## 模組目標

這一模組要建立兩個核心能力：

1. 學員看得懂資料表與商業需求的對應關係。
2. 學員能把業務問題翻成可驗證的 SQL。

不要把 SQL 教成背語法，而要讓學員先理解資料表之間的商業關聯。例如：

- 保單主檔與保戶資料如何關聯
- 理賠申請與保單號如何對應
- 多筆繳費紀錄如何彙總成應收狀態

學完後，學員不只要能寫出查詢，還要能回答這條 SQL 為什麼這樣接表、為什麼這裡需要彙總、為什麼這裡應該先過濾再分組。

## 核心重點

- select、insert、update、delete
- where、order by、group by、having
- join、子查詢、彙總查詢
- 以保單、理賠、繳費紀錄做查詢建模

## 情境說明

假設你接手一個壽險後台系統，產品經理希望你提供以下資訊：

1. 指定保單號的保戶姓名、產品代碼、近三期繳費狀態。
2. 最近三十天的理賠案件數量與核准金額。
3. 保單狀態為有效，但最近一期保費逾期未繳的清單。

如果學員只會寫 `select * from policy`，就無法解這些題。這一模組的目標，就是把「看起來很抽象的商業問題」拆成資料表、欄位、條件與彙總。

## 先建立一個正確心智模型

SQL 題通常不是從語法開始，而是從需求拆解開始。教學上要讓學員養成以下順序：

1. 先辨識查詢目標是明細、彙總還是比對。
2. 再辨識主表是誰。
3. 再決定要不要接其他表。
4. 最後才是 where、group by、having、order by 的細節。

如果一開始就直接寫 SQL，通常會出現兩種問題：

- 寫得出語法，但查不到正確結果
- 勉強查得到結果，但說不出查詢邏輯

## 建議教學步驟

### Step 1：先畫出資料表關係

至少讓學員知道：

- `policy` 是保單主檔
- `policy_holder` 是保戶主檔
- `premium_payment` 記錄每期保費繳費
- `claim_case` 記錄理賠申請

這一步的教學重點，不是讓學員背表名，而是要能用自然語言說出：

- 哪張表是主資料
- 哪張表是事件資料
- 哪張表一對多掛在主表下

### Step 2：先回答查詢目標，再寫 SQL

每次下 SQL 前，先回答三件事：

1. 我要哪幾個欄位。
2. 資料來自哪幾張表。
3. 我是要明細、彙總，還是只判斷是否存在。

可以再補一個常用判斷：

4. 這題要保留沒有子資料的主資料嗎？

這個問題會直接決定 inner join 或 left join。

### Step 3：用最小查詢開始，再逐步加條件

先寫最簡單版本：

```sql
select policy_no, product_code
from policy;
```

再加上保戶資料：

```sql
select p.policy_no,
			 p.product_code,
			 h.holder_name
from policy p
join policy_holder h on h.holder_id = p.holder_id;
```

最後才加入繳費彙總：

```sql
select p.policy_no,
			 h.holder_name,
			 sum(pp.paid_amount) as total_paid_amount,
			 count(pp.id) as payment_count
from policy p
join policy_holder h on h.holder_id = p.holder_id
left join premium_payment pp on pp.policy_id = p.id
where p.status = 'ACTIVE'
group by p.policy_no, h.holder_name
having sum(pp.paid_amount) > 0;
```

這裡要明確讓學員看到「查詢是逐層長出來的」，而不是一次把所有條件硬湊上去。

### Step 4：學會先用文字描述查詢，再翻成 SQL

例如：

- 先寫文字：找出狀態為有效的保單，並顯示保戶姓名與累計已繳保費
- 再翻成資料需求：需要 policy、policy_holder、premium_payment
- 再翻成 SQL 結構：主表是 policy，保戶 inner join，繳費 left join，再 group by

這種做法能大幅降低「語法寫對但題目解錯」的情況。

## 關鍵觀念

### 1. join 不是語法題，是資料關係題

先確認一對一、一對多、多對一，再決定用 inner join 還是 left join。

- 要求一定要有關聯資料：通常用 inner join
- 要保留主表，即使子表沒有資料：通常用 left join

還要補上兩個常見誤區：

- left join 後若在 where 寫子表條件，可能又把它變相縮成 inner join
- join 重複列數時，要先判斷是一對多本來就會展開，還是真正接錯表

### 2. group by 不是拿來硬湊語法

只要查詢內同時出現聚合函數與非聚合欄位，就要明確思考分組維度。不是資料庫要求你背規則，而是你必須先知道你到底要按「保單」彙總，還是按「月份」彙總。

這裡很適合提醒學員：

- 若目標是每張保單一列，group by 就應該圍繞保單維度
- 若目標是每月一列，group by 就應該圍繞月份維度

不是把 select 裡所有欄位全塞進 group by 就算完成。

### 3. where 與 having 的角色不同

- `where`：先過濾資料列
- `having`：彙總後再過濾分組結果

這裡可以補一個判斷口訣：

- 在分組前就能判斷的條件，多半放 where
- 必須等 sum、count、avg 算完後才知道的條件，才放 having

### 4. 查詢閱讀順序要教清楚

很多學員會從 select 開始一路往下讀，但一遇到複雜查詢就亂掉。可以教成：

1. 先看 from / join，知道資料從哪裡來
2. 再看 where，知道先被過濾了什麼
3. 再看 group by / having，知道如何彙總
4. 最後看 select / order by，知道輸出與排序

這個閱讀順序對後面看 mapper XML 與 explain 也很有幫助。

## 逐步範例

### 範例題 1：查指定保單的保戶與累計保費

```sql
select p.policy_no,
			 p.product_code,
			 h.holder_name,
			 coalesce(sum(pp.paid_amount), 0) as total_paid_amount
from policy p
join policy_holder h on h.holder_id = p.holder_id
left join premium_payment pp on pp.policy_id = p.id
where p.policy_no = 'PL20240001'
group by p.policy_no, p.product_code, h.holder_name;
```

解題說明：

- `policy` 與 `policy_holder` 用 inner join，因為保單必須有保戶。
- `premium_payment` 用 left join，因為某些保單可能尚未繳款。
- `coalesce` 避免沒有繳費資料時得到 `null`。

這裡也要順手讓學員理解：

- `sum` 遇到沒有資料時可能得到 `null`
- 報表與金額查詢通常不希望把 `null` 直接往外拋
- 所以 `coalesce` 是資料語意處理，不只是語法技巧

### 範例題 2：查最近 30 天核准理賠金額

```sql
select date(approved_at) as approve_date,
			 count(*) as approved_case_count,
			 sum(approved_amount) as total_approved_amount
from claim_case
where status = 'APPROVED'
	and approved_at >= current_date - interval 30 day
group by date(approved_at)
order by approve_date desc;
```

### 範例題 3：查最近一期保費未繳的有效保單

```sql
select p.policy_no,
			 h.holder_name,
			 pp.due_date,
			 pp.payment_status
from policy p
join policy_holder h on h.holder_id = p.holder_id
join premium_payment pp on pp.policy_id = p.id
where p.status = 'ACTIVE'
	and pp.installment_no = (
			select max(sub_pp.installment_no)
			from premium_payment sub_pp
			where sub_pp.policy_id = p.id
	)
	and pp.payment_status = 'OVERDUE';
```

	這題非常適合教「先找最新一期，再判斷狀態」，而不是直接拿所有繳費紀錄做 where。因為需求關注的是最近一期，不是任一筆歷史紀錄。

## 常見錯誤

- 把 left join 寫成 inner join，導致本該保留的主資料被過濾掉。
- 看到 group by 就把所有欄位都丟進去，結果查不出真正想看的彙總結果。
- 先寫 SQL 再想需求，導致查詢欄位與業務問題不匹配。
- 不驗證測試資料，結果查詢寫對了但資料本身不支持驗證。
	- 子查詢能跑就收工，沒有再思考是否有更清楚的拆法。
	- 用 `select *` 當成探索與正式查詢的同一種寫法。
	- 看見結果筆數不對，卻不先懷疑 join 粒度與資料展開。

## 自我檢查清單

- 我能清楚說明每一張表代表什麼商業意義嗎？
- 我知道這個查詢是明細查詢還是彙總查詢嗎？
- 我能解釋為什麼這裡要用 left join 而不是 inner join 嗎？
- 我能用自然語言描述 where 條件在篩選什麼嗎？
	- 我能說明這題的主表是哪一張嗎？
	- 我能辨識這題是否需要先處理 NULL 或最新一筆資料嗎？

## 練習題

1. 寫出「某產品代碼下，各月份新增保單數」查詢。
2. 寫出「理賠案件已送審但超過 3 天未核准」查詢。
3. 寫出「同一保戶名下保單數超過 2 張」查詢。
	4. 寫出「有效保單中，尚未有任何繳費紀錄」查詢，並說明為什麼不能用 inner join。

## 練習解答方向

1. 先決定分組維度是月份，不是保單號。
2. 先確認時間欄位是 `submitted_at`、`created_at` 還是 `approved_at`。
3. 保戶名下保單數題要先找出關聯鍵是 `holder_id`，不是直接拿姓名比對。
	4. 沒有繳費紀錄題要刻意保留主表資料，因此通常要從 left join + 子表為 null 的方向思考。

## 驗收標準

- 能寫出至少 3 種查詢
- 能解釋 join 為什麼這樣接
- 能看懂一段複雜 SQL 對應到哪個商業需求
- 能用文字說明每段 SQL 解決的是哪個業務問題
	- 能把商業問題先拆成資料需求，再翻成 SQL