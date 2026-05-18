# Module 02 — Eclipse / Tomcat Server Runtime 建置

## 模組目標

這一模組要讓學員真的能自己把專案掛上外部 Tomcat，而不是停留在「知道有這件事」。學完這一章後，學員應該能完成 Eclipse Runtime 建置、Server 掛載、WAR 部署與基本故障排查。

更進一步地，學員要能分清楚 IDE、Runtime、外部容器與應用本身是不同層級，這樣遇到部署失敗時才不會亂改程式碼。

## 開發痛點

很多學員只會在 IDE 裡按 Run，卻不知道程式到底是由誰啟動、部署在哪、為什麼同樣的保單查詢模組換到外部 Tomcat 就失敗。

在企業環境裡，外部 Tomcat 仍然很常見。原因包括：

- 維運團隊統一管理 servlet container
- 應用系統需遵守既有部署流程
- 啟動參數與資源限制由容器控管

如果學員不理解 Server Runtime 與應用程式的關係，後面所有的 Maven 打包與 WAR 部署都會失去上下文。

## 先建立一個正確心智模型

這一章最重要的不是按鈕位置，而是分清四個層次：

- Eclipse：開發工具
- Runtime：IDE 對 Tomcat 的描述
- Tomcat：真正承載應用的 servlet container
- Spring Boot 應用：被部署進容器的程式

很多部署錯誤之所以難排，是因為學員把這四層混成同一件事。

## 情境說明

假設團隊規定測試環境與正式環境都使用外部 Tomcat 9，所有 Spring Boot 模組都要以 WAR 形式部署。這時如果學員只會在 IDE 直接執行 main class，就無法完成真正的部署驗證，也無法分辨是程式沒起來、容器沒配置好，還是 context path 錯誤。

## 技術解法

### Tomcat 與 Eclipse Runtime 的關係

- Tomcat 是外部 servlet container
- Eclipse Server Runtime 是 IDE 中對該容器的配置描述
- 專案本身只是被部署到 Tomcat 的應用

### Step 1：先確認失敗發生在哪一層

教學上可以要求學員先判斷：

- 是 Runtime 沒設好
- 是 Server 沒掛到專案
- 是 Tomcat 沒啟動
- 還是應用已啟動但 URL 驗證失敗

這個判斷順序會直接決定排錯效率。

### 手動建立 Runtime 的必要性

手動建立 Runtime 的目的是讓學員知道：

1. Tomcat 並不是 Spring Boot 自動附帶的一切
2. 容器版本錯了，應用就可能無法啟動
3. JDK 與容器版本也必須一起考慮

這裡要讓學員知道：手動建 Runtime 的價值，在於理解相依關係，而不是為了增加操作步驟。

### 建置步驟摘要

1. 下載 Tomcat 9 並解壓
2. 在 Eclipse Preferences 中建立 Runtime
3. 將 `spring-boot-demo` 匯入為 Existing Maven Project
4. 建立 Tomcat Server
5. 把專案加到 Configured Resources
6. 啟動並驗證 `/training/health` 與 `/training/policies/POL20260001/summary`

### Step 2：驗證不是只看 Server 視圖變成 Started

真正完成部署驗證，至少要包含：

- 容器啟動成功
- 應用 context 初始化成功
- context path 正確
- 功能 URL 可以回應

只看到綠燈，不代表應用真的對外可用。

## 逐步操作

### Step 1：下載並解壓 Tomcat 9

先確認你使用的是 Tomcat 9，而不是 Tomcat 10 或其他版本。這不只是版本號問題，還涉及相容性與部署預期。

檢查重點：

- 是否存在 `bin/`、`conf/`、`lib/`、`webapps/`
- 路徑是否為本機可讀寫位置

也要提醒：Tomcat 路徑通常屬於本機環境資訊，應寫進文件，不應硬編碼進程式或提交成個人絕對路徑。

### Step 2：在 Eclipse 中建立 Runtime

實作時應確認：

1. Runtime type 是否為 Tomcat 9
2. JRE 是否對應 Java 8
3. Runtime 建立後是否可在 Server 視圖被選到

這裡可再補一個檢查點：若 Runtime 類型、JRE 與實際 Tomcat 安裝目錄對不起來，就算建立成功也可能在啟動時出問題。

### Step 3：匯入 Maven 專案

不要只看專案有沒有出現在 Project Explorer，要同時確認：

- `pom.xml` 是否被 Eclipse 辨識
- 依賴是否成功下載
- 專案沒有明顯的 build path error

這一段要讓學員知道：專案出現在 Project Explorer 不代表它已經可部署。匯入成功與可部署成功是兩回事。

### Step 4：建立 Server 並掛載專案

這一步最常出錯的地方有三個：

1. Server Runtime 綁錯版本
2. 專案未被加入 Configured Resources
3. 專案 packaging 或 facet 不符，無法正確部署

這裡建議補一個觀念：若專案加不到 Server，先想部署型態與 faceting，而不是先懷疑 controller 或商業邏輯。

### Step 5：啟動並驗證 URL

至少驗證以下路徑：

- `/training/`
- `/training/health`
- `/training/policies/POL20260001/summary`
- `/training/claims/CLM20260511001/status`

這裡可以再教一個驗證順序：

1. 先打健康檢查
2. 再打首頁或根路徑
3. 最後打業務 API

這樣能比較快區分是容器沒起來，還是業務功能有問題。

## 驗證範例

若啟動成功，應該至少看見：

1. Tomcat Server 正常啟動。
2. Spring Boot context 初始化完成。
3. 健康檢查或業務路徑能成功回應。
4. Console 中能看到與請求相關的 trace 或攔截日誌。

這一段要讓學員知道，部署驗證不只是功能可回應，也包含觀測資訊是否正常出現。

## 工程規範

- Runtime 路徑是本機環境資訊，不要硬寫進 Git 內的程式碼
- 文件中需明確標示 Tomcat major version
- 專案若預期部署到外部 Tomcat，必須清楚說明 context path、WAR 名稱與對外驗證路徑
- 排錯時先區分：是 IDE 配置問題、Tomcat 問題，還是應用程式問題

這裡可以再補充：文件應明確告知 context path、測試 URL 與 Tomcat major version，否則團隊很容易在驗證階段浪費時間。

## 常見錯誤對應表

## 常見錯誤對應表

| 問題 | 可能原因 | 優先檢查 |
|------|----------|----------|
| Tomcat 無法啟動 | JRE 版本不對 | Eclipse Installed JREs |
| 專案加不到 Server | 專案 facet 或 packaging 不符 | Maven / Project Facets |
| 啟動後 404 | context path 錯誤 | application.properties |
| 啟動時拋 NoClassDefFoundError | 依賴 scope 錯 | pom.xml |

## 故障排查順序

1. 先確認 Server Runtime 與 JRE 是否正確。
2. 再確認專案是否真的以可部署形式掛上 Server。
3. 再看 context path 與驗證 URL 是否一致。
4. 最後才回頭檢查程式本身與依賴衝突。

很多學員一看到 404 就直接改 controller，這通常不是正確順序。

這裡要刻意建立一個排錯原則：部署問題先看部署鏈，業務問題再看程式碼，不要一開始就跳到 controller。

## 自我檢查清單

- 我知道 Eclipse Runtime 與專案本身是不同層級的概念嗎？
- 我能解釋為什麼這裡是部署到外部 Tomcat，而不是 embedded Tomcat 嗎？
- 我能分辨 404、NoClassDefFoundError、JRE 不相容分別該先看哪裡嗎？
- 我是否真的驗證過 context path，而不是只點 Run？
- 我能把 Eclipse、Runtime、Tomcat、Spring Boot 應用四層概念分開嗎？
- 我知道啟動成功與業務 URL 可驗證是兩個不同層次嗎？

## 練習題

1. 手動建立一個 Tomcat 9 Runtime 並掛上 `spring-boot-demo`。
2. 故意把 JRE 換錯版本，觀察啟動失敗訊息並記錄排查過程。
3. 故意用錯 context path 呼叫 URL，說明為什麼會得到 404。
4. 解釋如果專案加不到 Server，應優先從哪些設定面向檢查。

## 練習解答方向

1. Runtime 題要交代實際操作步驟與驗證路徑，不是只說「我建好了」。
2. JRE 題要記錄錯誤訊息與你如何反推問題所在。
3. context path 題要指出設定值與實際呼叫 URL 的對應關係。
4. 加不到 Server 題要優先從 packaging、project facets、runtime 綁定去看。

## 本模組驗收

- 能說明什麼是 Server Runtime
- 能完成外部 Tomcat 掛載
- 能指出 Tomcat 啟動失敗時先檢查哪三個地方
- 能用正確順序排查部署與 URL 驗證問題
- 能清楚區分 IDE、Runtime、容器與應用四層責任
