# Scenario 05 — Maven Lifecycle 與產物理解混亂

## 練習目標

這一題要讓學員不再把 `mvn clean package` 當成黑盒子，而是理解不同 lifecycle 指令在交付流程中分別代表什麼，以及為什麼「打包成功」不等於「部署成功」。

## 背景

專案成員在排查部署問題時，習慣反覆執行：

- `mvn clean`
- `mvn package`
- `mvn install`

但對每個指令的效果沒有清楚理解。結果常見情況是：

- package 成功卻仍無法部署到 Tomcat
- install 成功卻不知道實際產物在哪
- clean 後以為所有環境問題都被重置

## 問題拆解

這題的本質不是指令背誦，而是要回答：

1. 每個 phase 對交付流程影響什麼
2. 哪個階段能證明可編譯、可測、可打包、可安裝
3. 為什麼這些都仍然不能取代外部部署驗證

## 任務

1. 說明 `clean`、`package`、`install` 的差異。
2. 指出這門課的外部 Tomcat 部署情境下，哪些指令只能證明部分完成。
3. 以一條因果鏈說明從 `pom.xml` 到 WAR 產物再到 Tomcat 驗證的過程。
4. 提供一條適合此課程情境的標準驗證順序。

## 建議排查順序

1. 先確認 `pom.xml` 中 packaging 與依賴 scope 是否合理
2. 再執行 `mvn clean package` 產生 WAR
3. 若需要本機模組引用，再考慮 `install`
4. 最後一定要做外部 Tomcat 掛載與 URL 驗證

## 預期解法

- `clean`：清除舊產物
- `package`：產生 WAR 或 JAR
- `install`：把產物安裝到本機 Maven repository

但真正交付驗證仍需包含：

- 掛上外部 Tomcat
- 啟動成功
- 驗證健康檢查與業務 URL

## 建議作答步驟

### Step 1：先回答每個 phase 解決的是哪一種問題

- `clean` 解決舊輸出殘留
- `package` 解決產物生成
- `install` 解決本機相依重用

### Step 2：再回答哪些事情它們無法保證

例如：

- `package` 無法保證外部 Tomcat 一定能部署成功
- `install` 無法保證 context path 與 URL 驗證正確
- `clean` 無法解決 JDK 或 Runtime 配置錯誤

### Step 3：建立本課標準驗證鏈

可接受答案至少要包含：

1. 看 `pom.xml`
2. 跑 `mvn clean package`
3. 檢查 WAR 產物
4. 掛到外部 Tomcat
5. 打 `/training/health` 驗證

## 工程規範

Commit message 範例：

```bash
git commit -m "docs(maven): clarify lifecycle and external tomcat verification flow"
```

## 常見錯誤

- 把 `install` 當成比 `package` 更完整的部署驗證
- 覺得只要 package 成功，就代表 WAR 一定可部署
- 不知道 clean 只處理輸出，不處理環境設定
- 看見錯誤就重跑指令，沒有重新思考因果鏈

## 自我檢查清單

- 我能說明 `clean`、`package`、`install` 各自的作用嗎？
- 我知道哪個環節是在驗證產物，哪個環節是在驗證部署嗎？
- 我能清楚說出為什麼 package 成功不代表外部 Tomcat 一定能跑嗎？
- 我能用部署目標反推驗證順序嗎？

## 考核點

- 是否能把 lifecycle 與交付流程對上
- 是否能避免把指令當黑盒子
- 是否知道最終仍要回到外部容器驗證

## 解答方向

1. 正確答案不是背 phase 名稱，而是能說出它們各自證明了什麼。
2. 本課程的真正完成標準仍是外部 Tomcat 可部署、可驗證。
3. 若學員能把 `pom.xml`、WAR 產物、Tomcat 驗證串成因果鏈，就算掌握重點。