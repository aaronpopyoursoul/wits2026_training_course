# Module 07 — Transaction Propagation、readOnly、timeout 與觀測

## 模組目標

這一模組要補足交易控制在真實專案中的進階部分：propagation、readOnly、timeout 與觀測。學完後，學員應能解釋為什麼某些交易要切成 `REQUIRES_NEW`，什麼查詢適合 `readOnly`，以及為什麼 transaction 問題不能只靠印例外訊息排查。

同時也要讓學員知道，進階交易設定不是越多越好，而是要和業務意圖、效能風險與排查需求一起設計。

## 情境說明

理賠核准流程中，主交易失敗時，審計紀錄是否應保留？大型報表查詢是否應加入 readOnly？慢交易是否應設定 timeout？這些都是 03 第一輪教材還沒拆開說清楚，但在企業實作裡非常常見的問題。

## 先建立一個正確心智模型

這一章不是在背 annotation 屬性，而是在回答三個實務問題：

1. 交易邊界要不要切開
2. 只讀查詢要不要與寫入流程分離
3. 發生慢交易或異常時，要靠哪些觀測資訊定位問題

## 核心重點

- propagation 基本模式
- `readOnly = true`
- `timeout`
- 交易觀測與日誌
- cross-service transaction 排查

## 教學步驟

### Step 1：先理解 propagation 常見模式

- `REQUIRED`：預設，外面有交易就加入，沒有就新開
- `REQUIRES_NEW`：總是新開一個
- `NOT_SUPPORTED`：暫停交易，適合某些不需 transaction 的查詢

這裡可以再補一個教學原則：

- 先用 `REQUIRED` 思考完整業務動作
- 只有當你明確要切開命運共同體時，才考慮 `REQUIRES_NEW`

### Step 2：理解 readOnly 與 timeout

readOnly 適合大型查詢、報表與只讀流程；timeout 則是為了避免某些交易卡太久，拖垮整體服務。

要特別提醒學員：

- `readOnly = true` 不是效能魔法按鈕，而是語意與優化提示
- timeout 也不是先設大就好，而是應先找出慢點在哪裡

### Step 3：理解觀測為什麼重要

當交易異常時，你至少需要知道：

- 哪個 service method 開了交易
- 哪個 repository 執行了更新
- 哪段交易耗時過長
- 例外是否被吞掉

### Step 4：將 propagation 與業務情境綁在一起理解

例如：

- 主理賠流程失敗是否要讓審計紀錄保留
- 通知紀錄是否要獨立提交
- 大型報表是否不應加入寫入交易

這樣學員才不會把 propagation 當背誦題。

## 交易觀測至少要看到什麼

這一章可以明確建立最小觀測欄位：

- requestId 或 traceId
- service method 名稱
- transaction 邊界開始與結束
- SQL 執行時間
- 例外類型與是否 rollback

如果沒有這些資訊，很多 transaction 問題都只能靠猜。

## 常見錯誤

- 所有交易都只用預設 propagation，不思考審計與主流程要不要切開。
- 將大型報表查詢也包進一般寫入交易。
- 遇到 timeout 只調大數值，不先找慢點。
- 把 `readOnly = true` 當成裝飾，卻沒有從查詢語意與風險來理解。
- 設了 `REQUIRES_NEW` 卻沒意識到外層回滾時內層可能已永久提交。
- 交易問題發生時只看例外訊息，不看 method 邊界、耗時與呼叫鏈。

## 自我檢查清單

- 我能解釋 `REQUIRED` 與 `REQUIRES_NEW` 的差異嗎？
- 我知道 readOnly 是優化與語意，而不是裝飾性設定嗎？
- 我能提出 transaction 問題的最小觀測欄位嗎？
- 我能說明什麼情境下應該把交易切開嗎？
- 我知道 timeout 排查應先看 SQL、索引、外部呼叫，再回頭調整設定嗎？

## 練習題

1. 設計一個主交易失敗但審計紀錄保留的情境。
2. 設計一個大型報表查詢流程，說明為什麼適合 readOnly。
3. 說明 transaction timeout 題的排查順序。
4. 設計一份最小交易觀測欄位清單，說明每個欄位的用途。

## 解題方向

1. 審計題通常會用 `REQUIRES_NEW`，但要說明風險與意圖。
2. readOnly 題要明確說明查詢性質與交易語意。
3. timeout 題要先看 SQL、索引、外部呼叫，再考慮 timeout 值。
4. 觀測題要能把 service、repository、SQL 與例外串成可追蹤路徑。

## 完整參考答案

標準答案重點：

- 不是所有交易都應綁在同一個邊界裡
- 交易觀測需要與 service / repository / SQL 排查結合
- propagation、readOnly、timeout 都應回到業務意圖與風險控制來理解
- 可觀測性是交易排查與交付品質的一部分