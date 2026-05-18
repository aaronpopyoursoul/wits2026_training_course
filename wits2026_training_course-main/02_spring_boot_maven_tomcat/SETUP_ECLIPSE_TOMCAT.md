# Eclipse / Tomcat 建置操作說明

> 目標：讓學員能在 Eclipse 中手動建立外部 Tomcat 9 Runtime，並成功部署 spring-boot-demo。

## 開發痛點

很多學員會把「專案能啟動」理解成只要 `main()` 跑起來就算完成。但企業環境常常不是這樣：系統要掛在外部 Tomcat、由維運控管啟動參數、由既有機房環境統一管理。

如果學員不理解 Eclipse Runtime、WAR 部署與外部 Tomcat 的關係，後面進 JDBC、交易控制或例外處理時，會把環境問題誤判成程式問題。

## 技術解法

### 1. 下載 Tomcat 9

1. 前往 Apache Tomcat 官方站下載 Tomcat 9.x zip。
2. 解壓縮到固定目錄，例如：
   - macOS: `/Users/<your-name>/servers/apache-tomcat-9`
   - Windows: `C:\servers\apache-tomcat-9`
3. 確認目錄下存在：
   - `bin/`
   - `conf/`
   - `lib/`
   - `webapps/`

### 2. 建立 Eclipse Server Runtime

1. 開啟 Eclipse。
2. 進入 `Window > Preferences > Server > Runtime Environments`。
3. 點選 `Add...`。
4. 選擇 `Apache > Apache Tomcat v9.0`。
5. 指向剛剛解壓的 Tomcat 目錄。
6. 選擇對應的 JRE 版本，建議使用 Java 8。
7. 完成後確認 Runtime 可見。

### 3. 匯入 Maven 專案

1. 進入 `File > Import > Maven > Existing Maven Projects`。
2. Root Directory 指到 `spring-boot-demo/`。
3. 完成匯入後，確認 Eclipse 已識別 `pom.xml`。

### 4. 建立 Server 並部署專案

1. 打開 `Servers` 視窗。
2. `New > Server`。
3. 選擇 `Tomcat v9.0 Server`。
4. Runtime Environment 選剛建立的外部 Tomcat。
5. 把 `spring-boot-demo` 加進 Configured Resources。
6. 啟動 Server。

### 5. 驗證啟動與攔截日誌

啟動成功後，透過瀏覽器或工具請求：

- `http://localhost:8080/training/`
- `http://localhost:8080/training/health`
- `http://localhost:8080/training/policies/POL20260001/summary`
- `http://localhost:8080/training/claims/CLM20260511001/status`
- `http://localhost:8080/training/underwriting/applications/UW20260511001/review`

預期應看到：

1. 頁面成功回應。
2. Console 或 server log 中出現：
   - Filter 進入紀錄（含 traceId、clientIp、userAgent）
   - Interceptor 前後處理紀錄（含 route、handler、duration）
   - AOP 對 controller method 的執行紀錄

## 工程規範

- Tomcat 目錄不應提交到 Git Repo。
- Eclipse 的個人 workspace 設定不應作為正式交付物。
- 交付時應提交：
  - `pom.xml`
  - `src/`
   - 最小 Eclipse 專案檔：`.project`、`.classpath`、`.settings/org.eclipse.*`
- README 或 SETUP 文件必須足夠讓他人重建環境。

## 常見錯誤

### 錯誤一：JRE 不相容

症狀：Server 啟動時拋出 class version 錯誤。

原因：Eclipse 使用的 JRE 與 Maven 編譯 target 不一致。

處理方式：
- 檢查 `pom.xml` 中的 Java version
- 檢查 Eclipse Installed JREs
- 檢查 Server Runtime 綁定的 JRE

### 錯誤二：404 或 Context Path 不對

症狀：Tomcat 啟動成功，但打 `http://localhost:8080/` 找不到頁面。

原因：應用實際 context path 不是根目錄。

處理方式：
- 檢查 `application.properties`
- 確認是否設定 `server.servlet.context-path=/training`

### 錯誤三：WAR 可打包但無法部署

症狀：Tomcat 啟動時找不到 servlet classes 或 Spring Boot 初始化失敗。

原因：
- 沒有繼承 `SpringBootServletInitializer`
- 依賴 scope 設錯
- 專案仍以 executable jar 思維配置

處理方式：
- 檢查 Application 類別
- 檢查 `spring-boot-starter-tomcat` 是否為 `provided`

## 驗收標準

- Eclipse 可成功匯入專案
- Tomcat Runtime 建立成功
- Server 成功啟動
- `GET /training/health` 可回傳 JSON 狀態資訊
- 保單／理賠／核保任一 API 可正常回應
- 日誌中可看見帶 `traceId` 的攔截紀錄
