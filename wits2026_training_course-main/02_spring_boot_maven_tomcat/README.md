# Spring Boot / Maven / Eclipse Tomcat 課程

> 課程資料夾：02_spring_boot_maven_tomcat
> 對象：金融與壽險產業的初中階軟體工程師
> 技術基線：Java 8、Spring Boot 2.7.x、Maven 3.8+、Tomcat 9、Eclipse IDE

## 課程定位

這門課不是在教學員快速生出 API，而是讓學員理解金融與壽險系統的後端專案如何被建立、打包、部署、追蹤與交付。

金融與壽險領域的系統開發有三個現實條件：

1. 環境通常不是最新，也不一定全面容器化。
2. 部署流程與伺服器版本必須可追溯。
3. 請求紀錄、稽核資訊與問題追蹤不能靠人工補救。

因此，本週課程聚焦在四件事：

1. 建立版本控制與工程交付的基本紀律。
2. 學會下載獨立 Tomcat，並在 Eclipse 中手動建立 Server Runtime。
3. 理解 Maven 依賴管理、Spring Boot 打包流程，以及 JAR / WAR 的差異。
4. 掌握 Spring Core 進階中的 Filter、Interceptor、AOP，並用它們實作可供稽核追溯的請求日誌攔截。

如果 03 到 05 是在教「資料層、API 層、前端消費層」怎麼協作，那 01 的角色就是把整個後端專案的交付基礎打穩。這一週的重點不是功能做得多，而是讓學員理解：為什麼同一份 Spring Boot 專案，在不同 Tomcat、不同 Maven 設定、不同攔截策略下，最終交付結果會完全不同。

## 你會學到什麼

- 能解釋企業後端專案的交付物不只包含 Java 原始碼
- 能在 Eclipse 中手動建立外部 Tomcat 9 Runtime 並部署 WAR
- 能理解 `pom.xml` 中 packaging、scope 與 Spring Boot 打包行為的因果關係
- 能說明 Filter、Interceptor、AOP 在請求生命週期中的不同位置
- 能完成一個帶 traceId 與分層稽核日誌的 Spring Boot 範例專案

## 建議先備知識

- 具備 Java 類別、例外、集合的基本能力
- 看得懂 Spring Boot 基本專案結構
- 知道 HTTP request / response 的概念
- 曾用過 Git 基本指令，例如 add、commit、log

## 這門課要解決的真實問題

很多後端初學者在本機用 `main()` 啟動成功，就以為專案已經可以交付。但企業現場會追問：

1. 這個版本是打成 JAR 還是 WAR。
2. 為什麼部署到外部 Tomcat 會失敗。
3. 哪些請求有留下稽核日誌，哪些沒有。
4. 同事要接手時，是否能根據 Git、README、pom 與 Eclipse 設定完整重建環境。

本課程就是針對這些問題設計，不讓學員把環境問題、打包問題與框架問題混在一起。

## 本週學習目標

完成本週課程後，學員應能：

- 透過 Git 管理後端專案與交付內容。
- 在本地下載並設定獨立 Tomcat 9。
- 在 Eclipse 中手動建立 Tomcat Server Runtime 並掛載專案。
- 理解 Maven lifecycle、dependency scope 與 Spring Boot repackage 行為。
- 說明 JAR 與 WAR 的差異，以及什麼情況下要部署到外部 Tomcat。
- 分辨 Filter、Interceptor、AOP 的責任邊界與執行時機。
- 建立一個可在本地 Eclipse Tomcat Server 成功啟動，並能記錄保單查詢、理賠進度、核保審核請求日誌的 Spring Boot 專案。

## 交付標準

本週課程的最終交付物必須滿足以下條件：

1. 專案可被 Eclipse 匯入為 Maven Project。
2. 專案可打包為 WAR，並掛載到本地外部 Tomcat Server Runtime。
3. 啟動後可成功處理 HTTP 請求。
4. 請求會被 Filter、Interceptor、AOP 至少其中兩層正確攔截並輸出帶 traceId 的稽核日誌。
5. 專案已提交至 Git Repo，Commit Message 具可讀性。

## 建議學習節奏

### 講師授課版

1. 先建立「交付不是只看程式碼」的觀念。
2. 再讓學員親手完成 Eclipse / Tomcat Runtime 建置。
3. 接著拆解 Maven、WAR、provided scope 的部署邏輯。
4. 最後才進入 Filter、Interceptor、AOP 的請求追蹤設計。
5. 用 exercises 收尾，要求學員從故障排查與交付角度回答問題。

### 自學版

1. 先讀本 README，理解課程主軸與交付要求。
2. 直接跟著 [SETUP_ECLIPSE_TOMCAT.md](SETUP_ECLIPSE_TOMCAT.md) 完成環境建置。
3. 再依序閱讀四個 module README，把概念與實作綁在一起看。
4. 最後做 exercises，檢查自己是否真的能分辨部署、打包與攔截設計問題。

## 課程結構

- module-01-course-overview：版本控制基礎與工程交付意識
- module-02-eclipse-tomcat-runtime：Eclipse / Tomcat Runtime 建置
- module-03-maven-and-packaging：Maven 與 Spring Boot 打包機制
- module-04-spring-core-advanced：Filter / Interceptor / AOP 深入解析
- exercises：情境練習與驗收題
- spring-boot-demo：可匯入 Eclipse、可部署至外部 Tomcat 的保險業務範例專案骨架

## 建議學習順序

1. 先讀 module-01，理解為什麼這門課不是單純的 IDE 操作課。
2. 接著讀 module-02，實際完成 Tomcat Runtime 建置。
3. 再讀 module-03，建立對 Maven 與 WAR 打包的基本理解。
4. 讀 module-04，理解請求攔截的技術選型。
5. 最後進入 exercises，完成稽核日誌、WAR 部署與攔截範圍故障排查練習。

## 建議主線情境

本課建議以保險業後台稽核需求為核心情境：

1. 保單查詢 API 要能被部署到外部 Tomcat。
2. 理賠進度查詢請求要留下 traceId 與 handler 紀錄。
3. 核保審核請求要能被一致地追蹤與排查。

這樣安排的好處是，學員不會把技術主題視為零散知識點，而會理解它們共同支撐的是「可部署、可追蹤、可交付」的企業系統。

## 本週不納入，但下週會銜接的主題

以下主題不在本週實作範圍內，但本週建立的專案骨架會保留後續延伸空間：

- SQL
- JDBC 與 JDBC Transaction
- MyBatis / MyBatis Plus
- 交易控制
- 除錯與例外處理
- RestController 深入設計

## 為什麼這樣安排

先把專案建立、伺服器建置、打包方式與請求追蹤打穩，下一週才適合進入資料存取與交易控制。否則學員很容易把問題混在一起：到底是 SQL 錯、Tomcat 配置錯、WAR 打包錯，還是稽核欄位根本沒打出來。這種教法在企業培訓裡成本很高。

## 常見學習誤區

- 把這門課當成單純的 Spring Boot 入門課。
- 只想知道 Controller 怎麼寫，忽略部署與追蹤要求。
- 把 Maven 與 Tomcat 設定視為雜務，不當作正式交付物。
- 認為有印 log 就等於完成稽核。
- 認為 Filter、Interceptor、AOP 是三選一，而不是分工合作。

## 練習與驗收方式

### 建議練習 1

請用文字解釋「為什麼外部 Tomcat 部署專案需要 WAR，而不是直接拿 executable JAR」。

### 建議練習 2

請列出一筆保單查詢請求從進入系統到 controller 執行結束，Filter、Interceptor、AOP 分別在哪個時點介入。

### 建議練習 3

請說明如果稽核日誌缺 traceId，應優先排查哪一層，為什麼。

## 練習解答方向

- WAR 題要從容器責任、部署模式與依賴 scope 三個角度回答。
- 攔截流程題要能畫出請求生命週期，而不是只背名詞。
- traceId 題通常先檢查 Filter，再看 Interceptor 與 log 格式傳遞。

## 快速導覽

- 課程總覽：本檔案
- Eclipse / Tomcat 操作：SETUP_ECLIPSE_TOMCAT.md
- 講師教案：instructor-guide
- 範例專案：spring-boot-demo
- 練習題：exercises
