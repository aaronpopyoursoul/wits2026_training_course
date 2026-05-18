# Module 04 — Spring Core 進階：Filter / Interceptor / AOP

## 模組目標

這一模組要讓學員不只知道三個名詞，而是能用請求生命週期解釋它們的分工。學完後，學員應該能回答：traceId 最適合在哪一層建立、handler 資訊在哪一層最容易取得、controller method 的執行耗時應該在哪一層觀測。

還要讓學員知道，三者不是替代方案比較，而是不同層級的橫切能力。只有把責任切乾淨，請求稽核與排錯資料才會穩定。

## 開發痛點

很多人第一次做請求日誌攔截時，會把所有事情都塞進 controller。這種做法短期可用，長期會讓保單查詢、理賠進度與核保審核的日誌規格失控。

在金融與壽險系統裡，請求紀錄、稽核欄位、追蹤 ID 與例行監控通常屬於橫切關注點，不應散落在每個 controller method 裡。否則一旦理賠申請沒有留下 traceId，就很難追到是哪個環節漏記。

## 先建立一個正確心智模型

這一章最重要的不是記住三個名詞，而是把請求生命週期切成三層觀察：

- 最外層 HTTP / Servlet 入口
- MVC 路由與 handler 執行前後
- Spring bean 方法切面

只有先有這個分層，學員才不會把 traceId、handler、duration 全塞進同一處。

## 情境說明

假設壽險系統已經提供三種路徑：

- 保單查詢
- 理賠進度查詢
- 核保審核

資訊安全與稽核團隊要求：每筆請求都必須能追到 traceId、route、handler、status 與 duration。如果你只在 controller 裡手寫 log，很快就會遇到欄位不一致、漏記、難維護等問題。

## 技術解法

### 三者在請求流程中的位置

#### Filter

- 屬於 Servlet 規格層
- 比 Spring MVC 更前面
- 適合做最外層的請求包裝、編碼處理、追蹤 ID 注入

這裡要補一句關鍵話：Filter 看到的是 request 最早期的入口，因此最適合做最通用、最外層的資訊初始化。

#### Interceptor

- 屬於 Spring MVC 層
- 可攔截 controller 之前與之後的流程
- 適合做登入驗證、請求計時、操作紀錄、特定路徑規則

這一層的價值在於：它開始知道 handler 與 MVC 對應關係，這是 Filter 拿不到的上下文。

#### AOP

- 屬於 Spring Bean 方法切面
- 著重方法執行點，而不是原始 HTTP 請求本身
- 適合做 service / controller method 的執行時間、輸入輸出記錄、橫切監控

這裡要提醒學員：AOP 看到的是方法，不一定等於完整 HTTP 生命週期，所以不能把所有請求稽核責任都丟給它。

## 一條請求的完整思考方式

以 `GET /training/policies/POL20260001/summary` 為例，可以這樣理解三層介入：

1. Filter：請求剛進入容器，建立 traceId，記錄最外層來源資訊。
2. Interceptor：Spring MVC 已辨識到對應 handler，可記錄 route、handler、前後處理時間。
3. AOP：controller 或 service 方法被執行時，可記錄方法名稱、參數與耗時。

這三者不是彼此替代，而是觀測粒度不同。

### Step 1：先回答「這個資訊在哪一層最早拿得到」

例如：

- traceId：越早越好，通常放 Filter
- handler / route：通常放 Interceptor
- method execution：通常放 AOP

這個判斷規則比死背三者定義更重要。

### 如何選型

| 技術 | 最適合處理 | 不適合處理 |
|------|------------|------------|
| Filter | 最外層 request/response 包裝、trace id、client metadata | 業務方法細節 |
| Interceptor | MVC 路由層級的進出紀錄、權限檢查、稽核欄位 | Spring bean 以外邏輯 |
| AOP | 方法切面、執行時間、橫切記錄 | 原始 servlet request 生命週期控制 |

這張表可以進一步轉成一個教學原則：先看你要的資訊屬於哪個層級，再選技術，不要先選技術再硬塞需求。

### 本課程採用策略

本範例專案會同時示範三者，但責任分工如下：

- Filter：為每個保單／理賠／核保請求建立 traceId，並記錄最外層進入資訊
- Interceptor：記錄 URI、route、handler、開始時間與完成時間
- AOP：記錄 controller method 執行與耗時

這樣設計的目的是讓學員看出三者不是替代關係，而是分層合作。

### Step 2：分層合作比單點全包更重要

如果所有資訊都只在 controller 手寫，會發生：

- 欄位格式不一致
- 某些路徑漏記
- 無法統一排除 `/health` 或靜態資源
- controller 被迫承擔非業務責任

## 範例設計

### Filter 範例重點

```java
String traceId = UUID.randomUUID().toString();
request.setAttribute("traceId", traceId);
MDC.put("traceId", traceId);
```

這一層要帶出的重點是：

- traceId 越早建立越好
- 最外層請求資訊適合在這裡統一注入

也可以補一個實務提醒：若 traceId 建太晚，前面發生的錯誤與進入資訊就可能無法被串起來。

### Interceptor 範例重點

```java
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
	request.setAttribute("startTime", System.currentTimeMillis());
	return true;
}

public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
	long startTime = (long) request.getAttribute("startTime");
	long duration = System.currentTimeMillis() - startTime;
}
```

這一層要帶出的重點是：

- 可以拿到 handler 與路徑資訊
- 適合做 MVC 層的進出紀錄與耗時統計

這裡可進一步說明：Interceptor 很適合記錄「誰被打到了」，而不是只知道「有請求進來」。

### AOP 範例重點

```java
@Around("within(@org.springframework.web.bind.annotation.RestController *)")
public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
	long start = System.currentTimeMillis();
	try {
		return joinPoint.proceed();
	} finally {
		long duration = System.currentTimeMillis() - start;
	}
}
```

這一層要帶出的重點是：

- 關注的是方法切面，不是最外層 HTTP 生命週期
- 適合補強 controller / service method 層級的觀測

這一步要讓學員知道，AOP 特別適合「方法層橫切規則一致化」，但不該被誤用成所有 request 追蹤的唯一來源。

## 排查與設計判斷順序

當學員不知道某個欄位該記在哪裡時，可以要求他依序回答：

1. 這個資訊是 request 一進來就有，還是必須等 MVC handler 決定後才有
2. 這個資訊是 HTTP 層資訊，還是方法執行資訊
3. 這個需求是稽核、除錯還是效能觀測

透過這三步，通常就能較穩定地決定要放 Filter、Interceptor 還是 AOP。

## 工程規範

- 不要把所有攔截邏輯都塞進同一層
- 日誌內容要有一致格式，至少包含 traceId、path、method、route、handler、status、duration
- 稽核需求與 debug 日誌要區分等級，不要全部用同一種 log level
- 攔截器不得影響正常業務流程，若要中斷請求，必須有明確條件

也建議在文件或團隊規範中明確定義：哪些路徑納入稽核、哪些只做基礎觀測、哪些應排除，以免日誌噪音過高。

## 常見錯誤

- 只用 AOP，就以為已經有完整稽核能力。
- 把 traceId 建在太晚的地方，導致最外層請求資訊缺失。
- 把所有 URI 都攔截，造成健康檢查與靜態資源噪音過高。
- 在 controller 中散落手寫 log，導致欄位格式不一致。
- 以為 Interceptor 與 AOP 只能二選一，忽略它們其實在不同層級協作。
- 不區分稽核需求與 debug log，導致資訊很多但難以使用。
- pointcut 或 include/exclude pattern 沒設計好，造成觀測缺口或噪音失控。

## 故障排查順序

1. 如果缺 traceId，先看 Filter 是否有建立與傳遞。
2. 如果缺 route 或 handler，先看 Interceptor 是否正確註冊與生效。
3. 如果缺方法耗時或 method 資訊，再看 AOP pointcut 是否命中。
4. 如果日誌很多但沒有價值，檢查 Interceptor include / exclude patterns。

這套順序很重要，因為它把排查從「哪裡沒印 log」變成「哪一層沒命中」。

## 自我檢查清單

- 我能說明三者各自在請求生命週期中的位置嗎？
- 我知道什麼資訊適合在 Filter、Interceptor、AOP 中記錄嗎？
- 我能解釋為什麼金融系統不應只在 controller 內手寫日誌嗎？
- 我知道攔截範圍本身也是設計的一部分嗎？
- 我能依據資訊層級判斷該用 Filter、Interceptor 還是 AOP 嗎？
- 我能區分稽核欄位、debug 欄位與效能觀測欄位嗎？

## 練習題

1. 用一條保單查詢請求說明三層攔截的時機與責任。
2. 解釋如果只靠 AOP，為什麼會出現稽核缺口。
3. 說明為什麼 `/health`、`/css/**` 這類路徑通常不應被業務稽核攔截。
4. 給定 traceId、route、handler、status、duration 五個欄位，說明它們最合理的分層紀錄位置。

## 練習解答方向

1. 請求流程題要從 request 進來開始，而不是從 controller 開始。
2. AOP 題要指出它看的是方法，不是原始 servlet request 邊界。
3. 攔截範圍題要平衡稽核需求與日誌噪音，而不是全部放行或全部攔截。
4. 分層題要從資訊出現時機與觀測目的兩個角度回答。

## 驗收標準

- 能說明三者執行位置不同
- 能畫出從 HTTP 進入到 controller method 的基本流程
- 能解釋為什麼金融系統不能只在 controller 內手寫日誌
- 能提出 traceId、route、handler、status、duration 的合理分層紀錄方案
- 能用請求生命週期與觀測目的判斷三者分工
