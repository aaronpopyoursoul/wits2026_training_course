# spring-boot-demo

> 可匯入 Eclipse、可打包為 WAR、可部署到外部 Tomcat 的金融／壽險後端示範專案骨架。

## 專案目標

這個專案刻意不做資料庫與交易控制，而是把注意力放在本週最重要的三件事：

1. Maven 與 WAR 打包可被團隊重建
2. Eclipse + 外部 Tomcat 9 可成功部署
3. 保單查詢、理賠進度、核保審核等請求具備可追溯的稽核日誌

## 目前包含的結構

- `TrainingAuditDemoApplication`：Spring Boot 啟動入口，支援 WAR 部署
- `HomeController`：保單查詢、理賠進度、核保審核與 health check 端點
- `RequestLoggingFilter`：建立 traceId、記錄 clientIp / userAgent、回寫 `X-Trace-Id`
- `RequestLoggingInterceptor`：記錄 route pattern、handler、status、duration
- `RequestLoggingAspect`：記錄 controller method 執行與錯誤
- `WebMvcConfig`：只攔截需稽核的業務路徑，排除健康檢查與靜態資源
- `.project` / `.classpath` / `.settings/*`：Eclipse 匯入與 Tomcat 部署所需最小設定

## 匯入方式

1. Eclipse > Import > Existing Maven Projects
2. 指向本專案根目錄
3. 讓 Maven 完成依賴解析
4. 若 WTP 未自動辨識，確認已使用本專案隨附的 `.project` 與 `.settings/*`
5. 配合上層 `SETUP_ECLIPSE_TOMCAT.md` 建立 Server Runtime

## 驗證路徑

- `GET /training/`
- `GET /training/health`
- `GET /training/policies/POL20260001/summary`
- `GET /training/claims/CLM20260511001/status`
- `GET /training/underwriting/applications/UW20260511001/review`

## 預期日誌欄位

每筆需要被稽核的請求，至少應能在 console 中看到：

- `traceId`
- `method`
- `uri`
- `route`
- `handler`
- `status`
- `durationMs`

這些欄位是後續做問題追蹤、人工稽核與跨團隊交接的最小基線。
