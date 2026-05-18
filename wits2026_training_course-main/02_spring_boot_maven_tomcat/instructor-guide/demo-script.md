# Demo Script

## Demo 1：為什麼這不是單純的 Spring Boot 入門課

1. 開啟課程根目錄與 README。
2. 指出這門課的交付標準不只包含 src，也包含 pom、Tomcat、文件與 Git。
3. 問學員：如果保單查詢發生稽核爭議，只有 controller 程式碼夠不夠追？

講師提醒：這一段不要急著進程式碼，先把「交付與追溯」主軸立起來。

## Demo 2：Eclipse + 外部 Tomcat 9

1. 示範匯入 Maven Project。
2. 示範建立 Server Runtime。
3. 示範把 WAR 部署到 Server。
4. 問學員：如果今天是 executable jar，這一套流程哪裡會不同？

講師提醒：這段示範完後，要要求學員自己說出 Eclipse、Runtime、Tomcat、應用四層差別。

## Demo 3：Maven 與 WAR 打包

1. 打開 pom.xml。
2. 說明為什麼要用 war。
3. 說明為什麼 spring-boot-starter-tomcat 要設成 provided。
4. 問學員：如果把 Tomcat 重複打進 WAR，部署風險是什麼？

可加一個對比提問：`package` 成功是否代表已可部署？為什麼不行？

## Demo 4：Filter / Interceptor / AOP 分層

1. 先開 RequestLoggingFilter。
2. 再開 RequestLoggingInterceptor。
3. 最後開 RequestLoggingAspect。
4. 用一條請求帶學員理解三層的時機差異。

建議現場要求學員先猜：哪一層最早能產出 traceId，哪一層最自然能拿到 handler。

## Demo 5：練習題帶做方式

1. 先帶 scenario-01，要求學員自己說出稽核缺口。
2. 再帶 scenario-02，要求學員用排查順序說明 WAR 部署問題。
3. 最後帶 scenario-03，要求學員說明為什麼攔截範圍本身就是設計。

若時間足夠，可加碼：

4. 用 scenario-04 問學員紅線究竟是匯入問題還是程式問題。
5. 用 scenario-05 問學員 `clean`、`package`、`install` 各自證明了什麼。
6. 用 scenario-06 問學員 traceId、route、duration 應該各在哪一層建立。