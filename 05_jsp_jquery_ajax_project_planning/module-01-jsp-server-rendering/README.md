# Module 01 — JSP 與伺服器端渲染

## 模組目標

這一模組要讓學員理解，JSP 不只是「舊技術」，而是很多企業系統仍在使用的交付形式。學員至少要能看懂：後端把資料塞進 request attribute，JSP 再把內容渲染成 HTML 的流程。

## 核心重點

- JSP 請求生命週期
- Servlet 容器與 JSP 編譯概念
- request / session / application scope
- 伺服器端渲染頁面的優缺點

## 情境說明

假設你接手一個保單查詢後台頁面，使用者反映畫面欄位顯示錯誤。你打開 JSP 後只看到 `${policy.policyNo}`、`${policy.holderName}`，如果不知道這些資料是誰先放進來、放在哪個 scope、頁面在請求流程中的哪個位置，就很難開始維護。

## JSP 為什麼還要學

因為在大量企業專案裡，JSP 不是歷史遺跡，而是仍在線上服務中的核心頁面。維運工程師常見的工作不是「重寫」，而是：

- 找出頁面資料從哪裡來
- 修正畫面欄位顯示錯誤
- 補上查詢條件
- 接一條新的 API 給現有頁面使用

很多企業專案不會讓你一進場就重寫成 Vue 或 React。更常見的工作是：

- 找出資料從 controller 怎麼進 JSP
- 判斷某欄位應不應該進 session
- 修正頁面邏輯與標籤
- 逐步把部分互動抽成 AJAX 或 API 消費

## 請求流程拆解

1. 瀏覽器送出 HTTP request。
2. Controller 或 Servlet 接住請求。
3. 後端把資料放進 request 或 session。
4. JSP 根據 attribute 與標籤語法輸出 HTML。
5. 瀏覽器收到完成後的 HTML。

### Step 1：先分清誰負責準備資料，誰負責顯示資料

JSP 本身不是資料來源，它是顯示層。這一點要先講清楚：

- controller / servlet / service：準備資料
- request / model / session：承接資料
- JSP：輸出 HTML

如果學員一開始就把 JSP 當成業務邏輯執行點，後面就會一直把不該放在頁面的判斷塞進去。

#### 完整請求流程與各層對應

```
瀏覽器
  │  GET /policy-dashboard?policyNo=PL20240001
  ▼
DispatcherServlet（Spring MVC 前端控制器）
  │  比對 @GetMapping
  ▼
PolicyController.policyDashboard()
  │  呼叫 service 取資料
  │  model.addAttribute("policy", policySummary)
  │  return "policy-dashboard"（邏輯視圖名稱）
  ▼
ViewResolver → /WEB-INF/views/policy-dashboard.jsp
  │  JSP 讀取 request attribute 產生 HTML
  ▼
瀏覽器收到完整 HTML（伺服器端渲染完成）
```

### Step 2：理解 JSP 在容器中的角色

JSP 不是單純 HTML 檔。它在執行時會由 servlet container 轉譯成 servlet 類型後執行，這也是為什麼它屬於伺服器端渲染。

教學上不需要把 Jasper 細節講到很深，但至少要讓學員知道：

- JSP 是伺服器端處理
- 瀏覽器拿到的是已完成的 HTML
- 所以頁面初始內容通常在回應送出前就決定好了

## 範例

### Controller 準備資料

```java
@GetMapping("/policy-dashboard")
public String policyDashboard(Model model) {
	model.addAttribute("policy", policyFacade.getPolicySummary("PL20240001"));
	return "policy-dashboard";
}
```

### JSP 顯示資料

```jsp
<h2>保單摘要</h2>
<p>保單號：${policy.policyNo}</p>
<p>保戶姓名：${policy.holderName}</p>
<p>狀態：${policy.status}</p>
```

### 加上 JSTL 的完整 JSP 頁範例

以下是一個更接近真實現場的 `policy-dashboard.jsp`，包含 JSTL 條件判斷、迴圈與 EL 表達式：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>保單儀表板</title>
</head>
<body>

<h2>保戶：${sessionScope.loginUser.displayName}，您好</h2>

<!-- 條件顯示警告 -->
<c:if test="${policy.status == 'EXPIRED'}">
  <div class="alert alert-warning">此保單已到期，請聯繫客服。</div>
</c:if>

<!-- 保單基本資料 -->
<table border="1">
  <tr><th>保單號碼</th><td>${policy.policyNo}</td></tr>
  <tr><th>保戶姓名</th><td>${policy.holderName}</td></tr>
  <tr><th>險種</th>    <td>${policy.productName}</td></tr>
  <tr><th>狀態</th>    <td>${policy.status}</td></tr>
</table>

<!-- 理賠紀錄列表 -->
<h3>理賠紀錄 (共 ${fn:length(policy.claims)} 筆)</h3>
<c:choose>
  <c:when test="${empty policy.claims}">
    <p>目前無理賠紀錄。</p>
  </c:when>
  <c:otherwise>
    <ul>
      <c:forEach var="claim" items="${policy.claims}">
        <li>${claim.claimNo} — ${claim.status} — ${claim.submittedAt}</li>
      </c:forEach>
    </ul>
  </c:otherwise>
</c:choose>

</body>
</html>
```

**說明**：
- `<%@ taglib %>` 宣告必須在頁面頂端，否則 JSTL 標籤無效。
- `${sessionScope.loginUser.displayName}` 讀取 session scope 中的登入使用者，而非 request scope。
- `${policy.claims}` 是 controller 透過 `model.addAttribute` 放進 request 的 List。
- `<c:if>`、`<c:choose>`、`<c:forEach>` 是 JSTL Core 標籤，負責條件與迴圈，讓 JSP 避免直接使用 Java scriptlet（`<% %>`）。

### Step 3：追資料來源時應該怎麼找

建議教學員固定按這個順序排查：

1. 先找 JSP 中用到的 EL 表達式，例如 `${policy.policyNo}`
2. 再回 controller 或 servlet 找 `model.addAttribute("policy", ...)`
3. 再往下追 service 或 facade 的資料來源

這比直接從 JSP 一路硬讀到底有效得多。

#### 維運排查實例

情境：`${policy.holderName}` 畫面顯示空白。

```
排查步驟：
1. JSP 找到 ${policy.holderName}
2. 回 controller 找 model.addAttribute("policy", ...)
   → 發現是 policyFacade.getPolicySummary(policyNo) 的回傳值
3. 進 PolicyFacade → PolicyService → PolicyMapper
   → 發現 SQL 查此欄位用的是 holder_name，但 Java DTO 中欄位叫 name（對不上）
4. 修正 DTO 欄位名稱或加 @JsonProperty / MyBatis resultMap 對應
```

> 重點：「畫面空白」不等於 JSP 壞了，很多時候是 Java 物件欄位名稱對不上 SQL 欄位。

## scope 差異

- request scope：一次請求有效，適合單頁顯示資料
- session scope：跨多次請求有效，適合登入資訊或短期會話資料
- application scope：整個應用程式共用，通常不用來放使用者專屬資訊

#### scope 存取對照

```java
// Controller 放資料的方式

// 1. request scope（放進 Model）
model.addAttribute("policy", policySummary);     // JSP 用 ${policy.xxx}

// 2. session scope（放進 HttpSession）
HttpSession session = request.getSession();
session.setAttribute("loginUser", currentUser);  // JSP 用 ${sessionScope.loginUser.xxx}

// 3. session scope 用 Spring @SessionAttributes（Spring MVC 風格）
@SessionAttributes("loginUser")
@Controller
public class AuthController { ... }
```

```jsp
<%-- JSP 內讀取各種 scope --%>

<%-- request scope（最常用，model.addAttribute 的資料） --%>
<p>${requestScope.policy.policyNo}</p>
<%-- 簡寫同效果（EL 會自動往 request → session → application 尋找） --%>
<p>${policy.policyNo}</p>

<%-- session scope（跨頁保留的值，例如登入使用者） --%>
<p>歡迎，${sessionScope.loginUser.displayName}</p>

<%-- 判斷是否已登入 --%>
<c:if test="${empty sessionScope.loginUser}">
  <a href="/login">請先登入</a>
</c:if>
```

### Step 4：scope 不是名詞題，而是資料生命週期設計

這一段要讓學員知道，不同 scope 對應的是資料該活多久：

- request：單次查詢結果、一次頁面顯示
- session：登入使用者資訊、短期會話選項
- application：系統共用常數或全域設定

常見誤用：

- 把查詢結果丟進 session，導致舊資料殘留
- 把使用者資料放 application scope，造成安全風險

### Step 5：server-side rendering 的優點與限制要連回專案情境

這一段不要只講抽象概念，應回到 05 的主題：

- 優點：對舊系統維運與後端快速交付友善
- 限制：互動複雜時，很難靠 JSP 一路撐到現代前台需求

這樣才能自然銜接後面的 jQuery、AJAX、Axios 與 06 的 Vue。

## 優點與限制

### 優點

- 頁面一開始就有完整 HTML
- 對舊系統與伺服器端流程整合容易
- 後端工程師可以快速交付簡單頁面

### 限制

- 畫面與後端耦合高
- 頁面互動一複雜就容易混亂
- 重用性與模組化通常比不上現代前端框架

## 常見錯誤

- 不知道 `${...}` 的資料從哪裡來。
- 把太多業務邏輯直接寫進 JSP。
- 混用 session 與 request，導致資料生命週期混亂。
- 覺得 JSP 等於不能接 API，其實只是使用方式不同。
- 看到畫面錯誤就直接改 JSP，沒有先回頭確認後端有沒有準備資料。
- 把顯示判斷、驗證與資料組裝全部塞進 JSP scriptlet 或標籤條件裡。

## 自我檢查清單

- 我能指出 JSP 中某個欄位的資料來源嗎？
- 我能解釋 request、session、application scope 的差異嗎？
- 我知道 JSP 是伺服器端渲染，而不是前端動態框架嗎？
- 我能判斷哪段邏輯該留在後端，哪段只該負責顯示嗎？

## 練習題

1. 找出一個 JSP 頁面顯示欄位的資料來源。
2. 解釋某個欄位為什麼適合放 request scope，不適合放 session scope。
3. 畫出一個 JSP 頁面的完整請求流程。
4. 修改頁面，多顯示一個保單產品代碼欄位。

## 練習解答方向

1. 先從 controller 的 model 或 request attribute 找起。
2. 若資料只跟單次查詢有關，通常不應進 session。
3. 流程題要包含 controller、scope、JSP 輸出三段。
4. 新增欄位題要先確認後端是否已準備資料，不是只改 JSP。

## 驗收標準

- 能解釋 JSP 為什麼屬於伺服器端渲染
- 能說出 request scope 與 session scope 的差異
- 能指出 JSP 頁面中哪些內容是後端先準備好的
- 能用文字描述一個 JSP 頁面的完整請求流程
- 能說明 JSP 在舊系統維運中的實際角色