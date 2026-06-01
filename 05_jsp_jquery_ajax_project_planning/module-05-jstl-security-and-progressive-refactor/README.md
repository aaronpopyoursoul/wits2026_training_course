# Module 05 — JSTL、XSS、表單生命週期與漸進式重構

## 模組目標

這一模組要補足 05 第一輪較弱的區塊：JSTL、輸出安全、表單提交生命週期與漸進式重構。學員要能理解舊系統最大的風險往往不是「語法舊」，而是輸出安全、隱含狀態與頁面流程不清楚。

## 情境說明

某 JSP 頁面會把查詢條件與使用者輸入回填到畫面，團隊直接把字串輸出到 HTML。功能表面可用，但只要輸入惡意內容，就可能出現 XSS 風險。另一類常見問題是送出表單後畫面狀態不一致，開發者卻不知道資料在哪一層被覆寫。

## 核心重點

- JSTL 與 EL 的用途
- escaping 與 XSS 風險
- 表單提交流程與回填
- 漸進式重構思路

## 教學步驟

### Step 1：先看 JSP 的輸出點

要讓學員知道哪些值是安全常數，哪些來自使用者輸入或外部資料。

### Step 1-1：JSTL / EL 是幫你讀值，不是幫你自動處理所有安全問題

這是很多初學者容易誤會的地方。看到 `${...}` 或 JSTL 標籤，不代表輸出一定安全。教材應明講：

- JSTL / EL 主要是方便輸出與條件判斷
- 安全性仍要看你輸出的資料來源與 escaping 策略

### Step 1-2：先分類資料來源

教學時可直接分三類：

- 常數或系統固定字串
- 後端查回的業務資料
- 使用者輸入或外部來源資料

第三類最需要警覺 XSS 風險。
#### JSTL 常用標籤對照範例

```jsp
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- 1. 條件判斷：c:if --%>
<c:if test="${policy.status == 'EXPIRED'}">
  <div class="alert">此保單已到期</div>
</c:if>

<%-- 2. 排他條件：c:choose / c:when / c:otherwise --%>
<c:choose>
  <c:when test="${policy.status == 'ACTIVE'}">
    <span class="badge-green">有效</span>
  </c:when>
  <c:when test="${policy.status == 'EXPIRED'}">
    <span class="badge-red">已到期</span>
  </c:when>
  <c:otherwise>
    <span class="badge-grey">未知</span>
  </c:otherwise>
</c:choose>

<%-- 3. 迴圈：c:forEach --%>
<c:forEach var="claim" items="${claimList}" varStatus="loop">
  <tr class="${loop.odd ? 'row-odd' : 'row-even'}">
    <td>${loop.index + 1}</td>
    <td>${claim.claimNo}</td>
    <td>${claim.status}</td>
  </tr>
</c:forEach>

<%-- 4. 空列表战行 --%>
<c:if test="${empty claimList}">
  <tr><td colspan="3">目前焠理賠紀錄</td></tr>
</c:if>

<%-- 5. 計算列表數量 --%>
<p>共 ${fn:length(claimList)} 筆理賠</p>
```
### Step 2：理解表單生命週期

- 初次進頁
- 填表
- 提交
- 驗證失敗回填
- 成功轉頁或重整

### Step 2-1：很多 legacy 問題本質上是狀態回填失控

這一段很值得講細。表單畫面混亂常不是因為技術太舊，而是開發者沒有把流程拆清楚：

- 哪些值是初始值
- 哪些值來自前次查詢
- 哪些值是驗證失敗後回填

如果這些狀態混在 JSP、Servlet、JS 裡，頁面就會難以維護。

### Step 2-2：畫表單生命週期比直接改 code 更重要

建議學員先用文字或流程圖畫出：

1. 進頁時有哪些欄位
2. 送出時往哪裡送
3. 失敗時哪些值要保留
4. 成功時是轉頁還是顯示結果

這可以大幅降低亂改 JSP 的風險。

### Step 3：導入漸進式重構

不是一次把整頁改成 SPA，而是先把高風險或高變動區塊抽出，逐步 API 化。

### Step 3-1：漸進式重構先排風險，不先排喜好

優先順序通常應該是：

- 先修安全風險
- 再修高變動區塊
- 再考慮互動升級
- 最後才是整體美化或框架統一

這很符合企業現場，而不是學生專案式的全面重做。

### Step 3-2：什麼區塊比較適合先抽成 API

通常優先考慮：

- 查詢結果區塊
- 補件狀態更新
- 重複使用的明細查詢

不一定優先處理：

- 靜態說明頁
- 低變動、低互動的管理頁

## 範例：XSS 風險判斷

假設 `remark` 來自使用者輸入，若直接輸出：

```jsp
<div>${remark}</div>
```

就應提醒學員：這不是「看起來能顯示」就算結束，而要追問：

- 這個值來自哪裡
- 是否做過 escaping
- 是否可能夾帶惡意內容
#### XSS 安全與不安全的寫法對照

```jsp
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- ✘ 不安全：直接輸出使用者輸入，可能擭入 <script> 標籤 --%>
<div class="remark">${remark}</div>

<%-- ✔ 安全：用 fn:escapeXml 轉讏 HTML 特殊字元 --%>
<div class="remark">${fn:escapeXml(remark)}</div>
```

`fn:escapeXml` 會把 `<`、`>`、`&`、`"`、`'` 轉被 HTML 安全對應符，防止 `<script>alert('xss')</script>` 被瀏覽器執行。

> **原則**：所有來自使用者輸入或外部來源的值，輸出到 HTML 時一律用 `fn:escapeXml` 包裹。

卖點：JSP 直接輸出 `${...}` 不會自動 escape，這就是為什麼舊系統有時會有 XSS。
## 範例：表單生命週期

以保單查詢頁為例：

1. 初次進頁：欄位為空
2. 使用者輸入保單號
3. 送出表單到 controller
4. 查詢成功：顯示結果
5. 查詢失敗：保留原輸入並顯示錯誤訊息

#### 將表單生命週期轉譯成 Spring MVC 代碼

```java
// PolicyController.java

@GetMapping("/policy-search")
public String showSearchForm() {
  // Step 1：初次進頁，什麼都不用做，JSP 所有欄位空白
  return "policy-search";
}

@PostMapping("/policy-search")
public String submitSearch(
    @RequestParam(required = false) String policyNo,
    Model model) {

  // Step 3 & 4：出現驗證失敗，保留原輸入
  if (policyNo == null || policyNo.isBlank()) {
    model.addAttribute("policyNo", policyNo);   // 回填表單
    model.addAttribute("error", "保單號碼不可空白");
    return "policy-search";                     // 回到表單頁
  }

  try {
    PolicySummary policy = policyService.findByPolicyNo(policyNo);
    model.addAttribute("policy", policy);
    model.addAttribute("policyNo", policyNo);   // 回填副本
    return "policy-search";                     // 同一頁顯示結果
  } catch (PolicyNotFoundException e) {
    model.addAttribute("policyNo", policyNo);   // 回填表單
    model.addAttribute("error", "查無此保單");
    return "policy-search";
  }
}
```

```jsp
<%-- policy-search.jsp --%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<form method="POST" action="/policy-search">
  <label>保單號碼</label>
  <%-- 回填原輸入値，驗證失敗後不白屏 --%>
  <input type="text" name="policyNo" value="${fn:escapeXml(policyNo)}" />
  <button type="submit">查詢</button>
</form>

<%-- 錯誤訊息 --%>
<c:if test="${not empty error}">
  <div class="error">${fn:escapeXml(error)}</div>
</c:if>

<%-- 查詢結果 --%>
<c:if test="${not empty policy}">
  <table border="1">
    <tr><th>保單號</th><td>${policy.policyNo}</td></tr>
    <tr><th>保戶</th><td>${fn:escapeXml(policy.holderName)}</td></tr>
    <tr><th>狀態</th><td>${policy.status}</td></tr>
  </table>
</c:if>
```

重點：
- controller 統一負責資料音驗證與寫入 model，JSP 只負責類生 HTML。
- `fn:escapeXml` 於使用者可控內容的欄位上一律使用。
- 回填襘於 `model.addAttribute("policyNo", policyNo)` 與 JSP 內 `value="${...}"` 配合，不需 JavaScript。

這樣的拆法能幫學員理解「回填」其實是流程設計問題。

## 常見錯誤

- 直接輸出使用者輸入內容。
- 表單回填邏輯散在 JSP、Servlet 與 JavaScript 中。
- 一發現問題就想全面改寫，而不是先做風險排序。
- 誤以為 JSTL / EL 會自動處理所有安全問題。
- 不畫生命週期就直接改欄位回填邏輯。

## 自我檢查清單

- 我能指出頁面上的高風險輸出點嗎？
- 我能畫出表單從進頁到成功或失敗的生命週期嗎？
- 我知道哪些問題應先修安全，哪些才是重構優化嗎？
- 我能寫出一份有順序的漸進式重構建議嗎？

## 練習題

1. 設計一題 JSP 輸出未 escaping 的排查題。
2. 畫出某個查詢表單的生命週期。
3. 分析某個欄位為什麼應保留回填，另一個欄位不應保留。
4. 寫一份漸進式重構的優先順序建議。

## 解題方向

1. XSS 題要從資料來源與輸出點切入。
2. 表單題要把頁面狀態與伺服器回應串起來。
3. 回填題要從使用者流程與狀態一致性角度回答。
4. 重構題要先定義風險與收益，不要只憑技術喜好。

## 完整參考答案

標準答案重點：

- 先保安全，再談優雅重構
- 漸進式重構需要明確優先順序與驗收點

## 驗收標準

- 能解釋 JSTL / EL 與輸出安全的差異
- 能畫出查詢表單的生命週期
- 能指出高風險輸出點與回填風險
- 能寫出有優先順序的漸進式重構建議