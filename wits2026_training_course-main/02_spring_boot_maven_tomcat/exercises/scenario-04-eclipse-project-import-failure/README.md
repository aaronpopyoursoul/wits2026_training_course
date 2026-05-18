# Scenario 04 — Eclipse 匯入與專案辨識失敗

## 練習目標

這一題要讓學員知道：專案顯示在 Project Explorer，不代表它已經是可編譯、可部署的 Maven 專案。真正要排查的是 Eclipse 對 JDK、Maven 與專案結構的辨識是否正確。

## 背景

團隊新成員從 Git clone 專案後，在 Eclipse 以 Existing Projects 或直接匯入資料夾的方式開啟專案。結果專案雖然出現在畫面上，卻出現以下現象：

- `pom.xml` 沒被當成 Maven 專案辨識
- Java 類別大量報錯
- JRE System Library 版本不正確
- 專案無法加入 Tomcat Server

## 問題拆解

這類問題通常不是單一設定，而是三個層面沒有對齊：

1. 匯入方式錯了
2. JDK / JRE 對不上
3. Maven 專案同步沒有成功

## 任務

1. 判斷目前問題是 Eclipse 匯入方式、JDK 設定還是 Maven 辨識失敗。
2. 用正確方式重新匯入或重新同步專案。
3. 確認專案最終能被 Eclipse 辨識為 Maven 專案，且可加入外部 Tomcat Server。
4. 用 Git commit 或文字說明記錄排查過程。

## 建議排查順序

1. 先確認專案是用 Existing Maven Projects 匯入，而不是一般資料夾匯入
2. 確認 Eclipse Installed JRE 與 project build path 是否為 Java 8
3. 確認 `pom.xml` 是否成功載入依賴
4. 最後確認專案 facet 與部署型態是否可加到 Tomcat Server

## 預期解法

- 以 Existing Maven Projects 匯入專案
- 修正 JDK / JRE 對應為 Java 8
- 重新執行 Maven 更新與依賴同步
- 確認專案沒有 build path error，並可被外部 Tomcat 掛載

## 建議作答步驟

### Step 1：先區分「看得到專案」與「專案可用」不是同一件事

這一題很容易掉入操作假象。學員要先說明：

- Eclipse 顯示專案，不代表 Maven 與 Java 設定已正常
- 專案可編譯、可部署、可掛上 Server 才算真正匯入成功

### Step 2：檢查 JDK 與專案基線是否一致

至少要確認：

- Installed JRE 是否為 Java 8
- Project build path 是否正確引用 Java 8
- 專案 facet 與 Maven compiler target 是否一致

### Step 3：確認 Maven 辨識與依賴同步

重點是：

- `pom.xml` 是否被 Eclipse 視為 Maven descriptor
- 依賴是否成功下載
- 是否需要執行 Maven update 讓 Eclipse 與專案狀態同步

### Step 4：驗證部署能力

最後要驗證的不是只有紅線消失，而是：

- 專案能加入 Tomcat Server
- 啟動後至少能驗證健康檢查 URL

## 工程規範

Commit message 範例：

```bash
git commit -m "docs(setup): clarify eclipse maven import and jdk alignment"
```

## 常見錯誤

- 用一般專案匯入方式開啟 Maven 專案
- 覺得只要 `pom.xml` 在那裡，Eclipse 就一定會辨識
- Eclipse 用錯 JRE 版本，卻一直回頭改程式碼
- 專案有紅線就直接重抓程式，沒有先看環境辨識

## 自我檢查清單

- 我能說明匯入成功與可部署成功的差異嗎？
- 我知道這題優先檢查的是 Eclipse / JDK / Maven，而不是 controller 嗎？
- 我是否真的驗證過專案可加入 Tomcat Server？
- 我能把錯誤定位在環境辨識層，而不是業務功能層嗎？

## 考核點

- 是否能用正確順序檢查匯入方式、JDK、Maven
- 是否理解 Eclipse 匯入問題與業務程式碼無關
- 是否能證明修正後專案真的可部署

## 解答方向

1. 先修正工具與環境辨識，再談程式碼。
2. 這題的核心是 Eclipse / Maven / JDK 對齊，而不是 Spring Boot 功能邏輯。
3. 完整答案應包含匯入方式、JDK 檢查、Maven 同步與部署驗證四個層次。