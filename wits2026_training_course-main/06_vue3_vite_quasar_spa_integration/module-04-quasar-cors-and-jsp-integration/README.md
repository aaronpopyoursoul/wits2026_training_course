# Module 04 — Quasar / CORS / Vue + JSP 漸進式整合

## 模組目標

這一模組要讓學員知道：前端現代化不只是把畫面做漂亮，更包含跨域串接、UI 元件一致性，以及如何與既有 JSP 系統共存。學完這一章後，學員應能說明為什麼本地開發會遇到 CORS，後端應如何正式開放，還有為什麼企業常選擇 Vue SPA 與 JSP 並存，而不是一次全面重寫。

## 情境說明

Vue 前台跑在 `http://localhost:5173`，Spring Boot API 跑在 `http://localhost:8082`。登入 API、保單 API、理賠 API 都存在，但瀏覽器直接呼叫時被 CORS 擋下。另一方面，企業內部仍有 JSP 後台頁面不能立即下線，新前台必須與舊頁面共存。

## 核心重點

- Quasar UI 元件基礎
- QLayout、QPage、QForm、QInput、QBtn、QTable
- CORS 原理與後端設定
- 前後端本地開發分離
- Vue SPA 與 JSP 並存策略

## 教學步驟

### Step 1：用 Quasar 建立一致的 UI 結構

本課建議至少示範：

- Login Page：`QForm`、`QInput`、`QBtn`
- Dashboard：`QCard`、`QBanner`
- 查詢頁：`QTable`

### Step 1-1：Quasar 不只是元件清單，而是頁面骨架系統

很多學員看到 Quasar 時，會把它理解成「多一套 UI 元件」。這個理解太淺。對 06 來說，Quasar 真正重要的是它能幫你快速建立：

- 版型一致性
- 表單互動一致性
- 頁面骨架與導覽結構

### Step 1-2：先教 layout，再教單一元件

建議教學順序不要一開始就只講 `QBtn`、`QInput`。更好的順序是：

1. `QLayout`
2. `QHeader` / `QDrawer` / `QPageContainer`
3. `QPage`
4. 再進入表單與卡片元件

這樣學員比較能理解 Quasar 對專題架構的價值。

### Step 1-3：登入頁與查詢頁的 Quasar 用法差異

- 登入頁：重點在表單、驗證、提交狀態
- 查詢頁：重點在結果展示、空狀態、列表元件

這可以順便連到 module-05 的 loading / error / empty state。

### Step 2：理解為什麼會有 CORS

這不是後端 API 壞掉，而是瀏覽器同源政策在保護前端。只要前端與後端的 protocol / host / port 其中之一不同，就不是同源。

### Step 2-1：先講什麼是 origin

origin 由三件事組成：

- protocol
- host
- port

例如：

- `http://localhost:5173`
- `http://localhost:8082`

只要其中一項不同，就不是同源。

### Step 2-2：為什麼 Postman 可以，瀏覽器不行

這是 CORS 教學裡最重要的一個分辨點：

- Postman 不受瀏覽器同源政策限制
- 瀏覽器會先檢查跨來源請求是否被允許

所以「Postman 成功、前端失敗」常常不是 API 功能壞掉，而是 CORS 設定或 preflight 流程問題。

### Step 3：理解後端正式開放 CORS

對本課來說，主體解法是讓 Spring Boot 正式允許 `http://localhost:5173` 呼叫 `/api/**`。這比只靠開發代理更符合學員理解正式部署責任邊界。

### Step 3-1：allowed origins、methods、headers 各自代表什麼

- `allowedOrigins`：哪些來源能呼叫
- `allowedMethods`：哪些 HTTP 方法能用
- `allowedHeaders`：哪些請求標頭被允許
- `allowCredentials`：是否允許帶 cookie 或授權資訊

這裡要讓學員知道，CORS 不是開或不開而已，而是明確定義跨域規則。

### Step 3-2：為什麼這門課不主打只靠 dev proxy

因為本課要讓學員理解正式系統的責任邊界：

- 前端應知道自己從哪個來源發請求
- 後端應正式定義允許哪些來源

dev proxy 可以用，但不應掩蓋學員對 CORS 真正成因的理解。

### Step 4：理解 Vue 與 JSP 並存策略

主模式：

- Vue SPA 作為新前台入口
- JSP 保留既有後台或舊管理頁面

這種模式比較符合企業漸進式現代化，而不是高風險全面重寫。

### Step 4-1：哪些頁面適合優先改成 Vue

通常優先改：

- 新功能前台
- 流程較複雜、互動較多的畫面
- 需要更佳狀態管理的查詢與操作頁

通常先保留 JSP：

- 穩定運作中的舊後台頁
- 低變動、低互動的管理畫面
- 牽涉太多既有依賴的頁面

### Step 4-2：並存不是混亂共存，而是有邊界的共存

要讓學員知道，並存策略應至少定義：

- 哪些 URL 由 Vue 管
- 哪些 URL 留給 JSP
- 導覽如何切換
- 驗收如何切分

## 對照範例

可以用一個真實路徑策略說明：

- `/app/*`：Vue SPA
- `/legacy/*`：JSP 舊頁面

這樣學員會比較理解 migration 不只是技術選型，而是路徑與責任切分。

## CORS 範例

```java
registry.addMapping("/api/**")
    .allowedOrigins("http://localhost:5173")
    .allowedMethods("GET", "POST", "PUT", "DELETE")
    .allowedHeaders("*")
    .allowCredentials(true);
```

## 常見錯誤

- 把 CORS 問題誤認成 JWT 或 API 路徑錯誤。
- 用 `*` 粗暴開放所有來源，卻沒理解安全影響。
- 以為 Quasar 只是換皮，不理解它是 UI 元件與版型系統。
- 覺得新系統上線就一定要立刻淘汰 JSP。
- 把 JSP 並存策略講成抽象口號，沒有路徑與頁面邊界。
- 看到瀏覽器報錯就直接改後端，不先確認實際 HTTP 流程。

## 自我檢查清單

- 我能說明同源與跨源的差異嗎？
- 我知道為什麼本課主軸把 CORS 放在後端正式開放嗎？
- 我能說明 Vue SPA 與 JSP 並存的商業理由嗎？
- 我能指出 Quasar 在這門課的角色是 UI 建設，而不是框架替代品嗎？

## 練習題

1. 用 Quasar 做出登入頁與簡單 Dashboard。
2. 說明本地 5173 呼叫 8082 為什麼會被瀏覽器擋下。
3. 解釋 `allowedOrigins`、`allowedMethods`、`allowedHeaders` 的差異。
4. 為企業場景寫出一段「為什麼 Vue 與 JSP 先並存，而不是全面重寫」的技術說明。
5. 畫出一個 Vue 與 JSP 並存時的路徑切分草圖。

## 練習解答方向

1. Quasar 題要重點在元件結構與表單互動，而不是追求視覺複雜度。
2. CORS 題要講同源政策，不要只寫「因為瀏覽器限制」。
3. CORS 設定題要明確說明每個欄位的責任。
4. JSP 並存題要從風險、成本、交付節奏三個角度說明。
5. 路徑草圖題要有清楚邊界，而不是只說「新舊並存」。

## 驗收標準

- 能用 Quasar 建立最小可用 UI
- 能解釋並處理本地開發的 CORS 問題
- 能說明 Vue SPA 與 JSP 並存策略
- 能說明 Quasar、CORS、migration 三者在專題中的不同角色
- 能把 UI、JWT、CORS 與舊系統整合放進同一個專題脈絡