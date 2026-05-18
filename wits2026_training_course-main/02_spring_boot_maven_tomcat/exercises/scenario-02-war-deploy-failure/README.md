# Scenario 02 — WAR 部署失敗

## 練習目標

這一題要讓學員建立正確的部署排查順序，避免一看到 Tomcat 啟動失敗就直接回去改 controller 或 service。重點不是把系統救起來而已，而是知道為什麼部署失敗，以及哪一層設定決定了這次失敗。

## 背景

學員完成 `mvn package` 後，成功產出 WAR 檔，卻在 Eclipse Tomcat Server 啟動時失敗。結果是保單查詢模組完全無法掛上測試 Tomcat，Console 顯示 Spring context 初始化異常或 servlet container 類別衝突。

## 問題拆解

這類問題通常集中在三個面向：

1. 產物型態錯了，例如其實還是 jar 思維。
2. 容器依賴處理錯了，例如外部 Tomcat 又被重複打進 WAR。
3. Spring Boot 外部部署初始化設定不完整。

## 任務

1. 檢查 `pom.xml` 的 packaging 是否正確。
2. 檢查 `spring-boot-starter-tomcat` 的 scope 是否應設為 `provided`。
3. 檢查主程式是否有繼承 `SpringBootServletInitializer`。
4. 提交修正並重新部署驗證。

## 預期排查順序

1. 先看 packaging：是不是 `war`
2. 再看 tomcat 依賴 scope
3. 再看 Application 類別
4. 最後看 Eclipse Server 與 Runtime 綁定是否正確

## 建議作答步驟

### Step 1：先看 `pom.xml`

先確認：

- packaging 是否為 `war`
- 是否有不該打包進外部容器的依賴

### Step 2：檢查 Tomcat starter scope

如果目標是外部 Tomcat，`spring-boot-starter-tomcat` 通常應為 `provided`，否則就會有容器重複或類別衝突風險。

### Step 3：檢查 Application 類別

若缺少 `SpringBootServletInitializer` 相關設定，WAR 雖然能打出來，但外部容器不一定知道如何正確初始化應用。

### Step 4：重新部署並驗證

修正後要重新：

- package
- 掛載到 Eclipse Tomcat Server
- 驗證 `/training/health` 與至少一條業務 API

## 工程規範

不要只改到能啟動就結束，必須補一個說明：為什麼這次部署失敗，以及若這是上線前演練會造成什麼交付風險。

Commit message 範例：

```bash
git commit -m "fix(packaging): configure war deployment for external tomcat"
```

## 常見錯誤

- 看到 WAR 檔存在，就以為部署問題一定不是打包設定。
- 只改 `pom.xml`，沒有檢查 Application 類別。
- 沒分清 embedded Tomcat 與 external Tomcat 的責任邊界。
- 修正後沒有做實際 URL 驗證。

## 自我檢查清單

- 我能說明這次錯誤是部署層問題，不是業務功能問題嗎？
- 我是否按照 packaging、scope、initializer、runtime 的順序排查？
- 我能解釋 `provided` 的技術原因，而不只是照抄設定嗎？
- 我是否真的在外部 Tomcat 驗證成功？

## 考核點

- 是否理解 external Tomcat 與 executable jar 的差異
- 是否能用正確順序排查問題
- 是否能避免把 container 重複打進 WAR

## 解答方向

1. 正確答案通常不是改 controller，而是先回 `pom.xml` 與 Application 類別。
2. `provided` 的重點是容器責任切分，不是單純為了「讓它能跑」。
3. 真正完成此題的標準，是外部 Tomcat 成功啟動並可驗證 URL，而不是只有 package 成功。
