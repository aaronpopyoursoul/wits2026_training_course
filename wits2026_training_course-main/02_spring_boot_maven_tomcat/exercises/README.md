# Exercises — 實戰情境練習

> 這些練習的目的不是背指令，而是訓練學員在金融／壽險後端環境中分辨：問題到底來自部署、依賴、還是稽核攔截設計。

## 練習目標

完成這一組 exercises 後，學員應該能做到三件事：

1. 不把所有錯誤都歸咎於 Spring Boot 程式碼本身。
2. 能用正確順序拆解部署、打包與攔截問題。
3. 能把修正過程轉化成可交付的 Git commit 與文字說明。

完成全部情境後，學員還應該能回答：

- 這次問題是發生在 IDE、容器、打包、還是攔截設計哪一層
- 驗證證據應該長什麼樣子
- 修正後為什麼算是「可交付」而不是只是「本機暫時能跑」

## 練習索引

- scenario-01-audit-log-gap：保單與理賠請求的稽核日誌漏記
- scenario-02-war-deploy-failure：WAR 部署失敗
- scenario-03-interceptor-scope-mismatch：攔截範圍錯誤
- scenario-04-eclipse-project-import-failure：Eclipse 匯入與 JDK / Maven 辨識失敗
- scenario-05-maven-lifecycle-confusion：clean / package / install 與產物理解混亂
- scenario-06-filter-vs-interceptor-ordering：traceId、route、duration 出現順序錯亂

## 建議練習順序

建議不要亂跳題，因為六題的設計是有層次的：

1. 先做 scenario-01，建立觀測與稽核分層觀念
2. 再做 scenario-02，建立 WAR 與外部 Tomcat 部署判斷
3. 接著做 scenario-03，建立 MVC 攔截範圍設計
4. 再做 scenario-04，建立 Eclipse 匯入與環境辨識能力
5. 接著做 scenario-05，建立 Maven lifecycle 與產物因果鏈
6. 最後做 scenario-06，把 Filter / Interceptor 執行時序真正串起來

## 建議作答方式

每一題都建議學員按照以下格式回答：

1. 先用一句話描述問題發生在哪一層。
2. 再列出排查順序，不要直接跳到修改。
3. 接著說明實際修正內容。
4. 最後補上為什麼這樣改，以及如果不改會有什麼交付風險。

如果題目涉及部署或驗證，建議再補第五點：

5. 說明你如何確認修正真的生效，而不是只是碰巧通過一次。

## 建議驗收證據

每一題至少應交出以下其中幾項：

- 修正前後的設定差異
- 實際驗證結果或畫面
- Git commit message
- 對技術原因的文字說明

若情境涉及部署與打包，建議至少補其中兩項：

- `pom.xml` 或設定檔差異
- 啟動 log 或錯誤訊息摘要
- 驗證 URL 成功結果

## 評核重點

1. 是否能明確描述問題發生在哪一層。
2. 是否能提出可驗證的排查步驟。
3. 是否能用版本控制提交修正。
4. 是否能解釋修正背後的技術原因，而不是只說「這樣改就好了」。

## 題型對照表

| 題目 | 主題焦點 | 核心能力 |
|------|----------|----------|
| scenario-01 | 稽核漏記 | Filter / Interceptor / AOP 分層 |
| scenario-02 | WAR 部署失敗 | packaging / provided / initializer |
| scenario-03 | 攔截範圍錯誤 | include / exclude patterns |
| scenario-04 | Eclipse 匯入失敗 | JDK / Maven / 專案辨識 |
| scenario-05 | lifecycle 混亂 | clean / package / install 與產物理解 |
| scenario-06 | 攔截順序錯亂 | request lifecycle 與觀測欄位串接 |

## 建議評分方式

- 問題辨識：30%
- 技術修正：30%
- 說明能力：20%
- Git 交付品質：20%

## 建議作答結構範本

學員可依下列模板提交：

1. 問題定位：問題在哪一層
2. 排查順序：先看什麼、後看什麼
3. 修正內容：改了哪些設定、程式或文件
4. 驗證證據：如何證明修正有效
5. 交付說明：commit message 與風險說明

## 常見作答錯誤

- 直接貼修改結果，沒有說明排查過程。
- 看見錯誤就改程式碼，沒有先判斷是部署、容器還是攔截設計問題。
- 只交 commit，不交技術說明。
- 只交說明，不交可驗證證據。
- 把一次成功啟動誤認為真正完成驗證。
- 只談工具操作，不解釋為什麼這樣改。
