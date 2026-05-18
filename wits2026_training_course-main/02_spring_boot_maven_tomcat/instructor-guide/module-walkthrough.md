# Module Walkthrough

## Module 01

- 目標：讓學員知道版本控制範圍不只原始碼。
- 提問：如果沒有記錄 Tomcat 與 pom 設定，交接時會缺什麼？
- 現場要開的檔案：課程根 README、`.gitignore`、`pom.xml`
- 講師提醒：把 Git 從工具話題拉回交付與稽核追溯

## Module 02

- 目標：讓學員自己完成 Eclipse Tomcat Runtime 建置。
- 提問：外部 Tomcat 與內嵌 Tomcat 的責任切分在哪裡？
- 現場要開的檔案：`SETUP_ECLIPSE_TOMCAT.md`
- 講師提醒：不要只示範成功流程，也要刻意指出 JRE、Runtime、context path 三個常卡點

## Module 03

- 目標：理解 WAR、provided scope、Spring Boot repackage。
- 提問：部署失敗時先看 pom 還是先看 controller？為什麼？
- 現場要開的檔案：`pom.xml`、Application 啟動類別
- 講師提醒：要求學員用部署目標反推 packaging，而不是反過來背設定

## Module 04

- 目標：建立請求攔截的分層觀念。
- 提問：哪一層最適合建立 traceId，哪一層最適合記錄 handler，為什麼？
- 現場要開的檔案：RequestLoggingFilter、RequestLoggingInterceptor、RequestLoggingAspect
- 講師提醒：這一段要持續提醒學員，三者不是三選一，而是看資訊出現時機做分層

## 課後連結建議

- Module 01 → 02：沒有可交付清單，就無法穩定重建環境
- Module 02 → 03：環境建好後，要理解產物為什麼能部署
- Module 03 → 04：系統能啟動後，還要能追到每筆請求做了什麼