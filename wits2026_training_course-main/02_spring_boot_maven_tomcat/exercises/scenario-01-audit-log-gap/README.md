# Scenario 01 — 稽核日誌漏記

## 練習目標

這一題要讓學員理解：AOP 雖然能記錄方法執行，但不足以滿足企業稽核要求。真正完整的請求追蹤，通常需要 Filter、Interceptor、AOP 分工合作。

## 背景

壽險系統的保單查詢與理賠進度入口已經上線，但資訊安全與稽核團隊發現：部分請求沒有留下完整追蹤紀錄，尤其缺少 traceId、route 與請求耗時。

團隊口頭說明是「有加 AOP 日誌了」，但實際檢查後發現只記到了 controller method，沒有記到最外層請求資訊，也無法從日誌分辨是保單查詢還是理賠進度查詢。

## 問題拆解

這個情境至少包含三個缺口：

1. 沒有 traceId，代表最外層請求缺乏統一識別。
2. 沒有 route / handler，代表 MVC 層資訊缺失。
3. 只有方法層紀錄，代表 HTTP request 生命週期沒有被完整觀測。

## 任務

1. 分析目前只用 AOP 記錄時，為什麼會出現稽核缺口。
2. 補上 Filter 與 Interceptor 的分工。
3. 讓每一筆請求至少具備：
   - traceId
   - method
   - URI
   - route
   - handler
   - status
   - duration
4. 用 Git 提交修正。

## 建議排查順序

1. 先確認目前日誌是在哪一層產生。
2. 檢查是否有 Filter 建立 traceId。
3. 檢查是否有 Interceptor 記錄 route、handler、status、duration。
4. 最後再確認 AOP 是否保留作為方法層補充紀錄。

## 預期解法

- Filter：建立 traceId 並寫入 request attribute 或 MDC
- Interceptor：記錄 route、handler 與 controller 前後資訊
- AOP：保留 method 級別的補充觀測

## 建議作答步驟

### Step 1：先回答為什麼只靠 AOP 不足

回答時至少要提到：

- AOP 主要觀測的是 Spring bean 方法
- 它不是最外層 servlet request 入口
- 有些 request metadata 在 AOP 層取得不自然或不完整

### Step 2：設計三層責任分工

建議答案：

- Filter：traceId、client metadata、最外層 request 進入紀錄
- Interceptor：route、handler、status、duration
- AOP：controller / service method 執行補充觀測

### Step 3：定義一致日誌欄位

至少包含：

- traceId
- method
- uri
- route
- handler
- status
- duration

### Step 4：驗證一條請求是否真的完整記錄

建議選一條保單查詢或理賠查詢請求，實際比對三層日誌是否能串成同一筆追蹤紀錄。

## 工程規範

Commit message 範例：

```bash
git commit -m "feat(logging): add request trace and interceptor audit logs"
```

## 常見錯誤

- 把所有欄位都塞進 AOP，導致設計邏輯混亂。
- 有 traceId 但沒有往下傳遞，造成日誌無法串接。
- Interceptor 有註冊但沒有記錄完成時間，導致缺 duration。
- 只看有沒有印 log，不檢查欄位是否一致。

## 自我檢查清單

- 我能說明為什麼 AOP 不能單獨解決這題嗎？
- 我能清楚區分三層責任嗎？
- 我是否真的驗證過 traceId 在多條日誌中一致？
- 我能指出這題的核心問題是稽核設計，而不是功能 API 錯誤嗎？

## 考核點

- 能否說明為什麼只靠 AOP 不足
- 能否清楚區分 Filter / Interceptor / AOP 的責任
- 是否把攔截資訊整理成一致格式

## 解答方向

1. 問題不在 controller 功能，而在觀測層次不完整。
2. 若只用 AOP，通常會缺最外層 request metadata 與 MVC route 資訊。
3. 正確方向是三層分工，而不是在單一層越加越多邏輯。
