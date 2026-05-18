# Scenario 06 — Filter 與 Interceptor 的時序錯亂

## 練習目標

這一題要讓學員真正理解 request lifecycle，不再只背三個名詞。重點是讓學員能回答：為什麼 traceId 應該在更早的層建立，以及 route、handler、duration 為什麼通常不適合都放在同一層處理。

## 背景

目前系統已同時使用 Filter 與 Interceptor，但 log 看起來仍然很混亂：

- 有些 log 先出現 handler，再出現 traceId
- 有些 log 有 duration，但對不起 route
- 某些請求在例外情境下沒有完整收尾紀錄

團隊直覺認為「多印幾行 log 就好」，但其實問題是時序與責任分層錯了。

## 問題拆解

這題的核心在於三件事：

1. 哪些資訊在 request 一進來就能取得
2. 哪些資訊必須等 MVC 辨識 handler 後才有
3. 哪些資訊必須在請求完成或例外後才能算出來

## 任務

1. 分析目前 log 時序為什麼混亂。
2. 重新安排 traceId、startTime、route、handler、status、duration 的建立與輸出位置。
3. 驗證正常請求與失敗請求都能輸出一致結構的觀測資訊。
4. 說明這題為什麼不應只靠 AOP 或 controller 手寫 log 來解。

## 建議排查順序

1. 先列出每個欄位最早能在哪一層取得
2. 檢查 Filter 是否已在最外層建立 traceId
3. 檢查 Interceptor 是否在 preHandle / afterCompletion 正確記錄 startTime 與收尾資訊
4. 最後確認 AOP 是否只保留方法級補充，而不是搶走 request lifecycle 的責任

## 預期解法

- Filter：建立 traceId、寫入 MDC、記錄 request 進入
- Interceptor preHandle：記錄 startTime、辨識 handler
- Interceptor afterCompletion：計算 duration、補 status、收尾記錄
- AOP：保留方法級額外觀測

## 建議作答步驟

### Step 1：先把欄位依出現時機分類

例如：

- traceId：request 一進來就要有
- route / handler：MVC 辨識後才知道
- duration：請求結束後才算得出來

### Step 2：確認欄位放錯層時會出現什麼症狀

- traceId 放太晚：前段 log 串不起來
- route 放在 Filter：拿不到完整 MVC 上下文
- duration 只在 controller 算：例外時可能漏記

### Step 3：用一條成功請求與一條失敗請求驗證

這一題不能只測 happy path。至少應驗證：

- 正常保單查詢請求
- 一條發生例外或 404 的請求

## 工程規範

Commit message 範例：

```bash
git commit -m "fix(logging): align filter and interceptor ordering for request tracing"
```

## 常見錯誤

- 覺得欄位能印出來就算完成，不檢查時序是否合理
- 把所有邏輯塞到 Interceptor 或 AOP，讓責任邊界再次混亂
- 只驗正常請求，不驗例外情境
- 沒有用同一個 traceId 串接整段請求記錄

## 自我檢查清單

- 我能說明每個欄位在哪一層最早取得嗎？
- 我知道為什麼 traceId 應該比 handler 更早出現嗎？
- 我是否驗證了正常與失敗請求的日誌一致性？
- 我能解釋這題的本質是 request lifecycle 設計，而不是多印幾行 log 嗎？

## 考核點

- 是否能依資訊出現時機安排責任層
- 是否能說明為什麼順序錯會破壞可追蹤性
- 是否能驗證失敗請求下仍保有完整觀測欄位

## 解答方向

1. 先用 request lifecycle 思考，而不是先選工具。
2. traceId、route、duration 屬於不同時機點，不應硬放同一層處理。
3. 若能清楚安排 Filter、Interceptor、AOP 的責任並驗證例外場景，就算完整解題。