# Scenario 05 — 元件責任、Slots 與 Composable 邊界混亂

## 背景

團隊把頁面查詢邏輯、UI 呈現與欄位格式化都混在同一個大型元件裡，後來又加入 slots 與共用 composable，結果責任更混亂，連 bug 發生在哪一層都看不清楚。

## 任務

1. 說明這題主要是架構問題還是語法問題。
2. 提出 page、component、composable 的責任切分方式。
3. 寫出完整參考答案，說明 slot 何時適合、何時不適合。

## 解題方向

- 重點不是 Vue 語法，而是責任切分。
- slots 應服務於可重組 UI，不該掩蓋本來應屬於 page 的業務邏輯。

## 完整參考答案

標準答案重點：

1. 先切 page / component / composable 邊界。
2. 再決定 slot 是否真的有必要。