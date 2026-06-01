# Module 02 — Prototype.js / jQuery 舊式頁面互動

## 模組目標

Prototype.js 與 jQuery 要放在同一模組，是因為兩者都代表同一代網頁互動思維：直接在頁面上抓 DOM、綁事件、手動更新節點。學員要能辨識這種寫法的特徵與限制，因為真實專案維護常會遇到。

## 核心重點

- DOM selector 與事件綁定
- 顯示與隱藏區塊
- 表單輸入值讀取
- 舊式 AJAX / remote call 概念

## 情境說明

你接手一個理賠查詢頁面，需求只是「按一下按鈕後顯示更多欄位」。結果你一打開程式，發現頁面裡有 inline script、外部 js 檔、jQuery click handler，還有一些 Prototype.js 寫法混在一起。這就是 05 要學的真實現場，而不是乾淨的新專案。

## 這一代前端程式的典型特徵

- HTML、CSS、JavaScript 緊密耦合
- 用 selector 直接抓節點
- 事件大多綁在按鈕、表單、連結上
- UI 狀態通常靠手動 show / hide 管理

### Step 1：先找事件入口，不要先讀整頁 HTML

維運這類程式時，最有效率的方式通常不是先讀 markup，而是先找：

- `click`
- `change`
- `submit`
- `observe`

因為使用者行為通常就是流程入口。找到入口後，才比較容易往下追：

- 讀了哪些欄位
- 改了哪些 DOM
- 有沒有送 request

## jQuery 範例

```javascript
$("#toggleClaimPanel").on("click", function () {
	$("#claimPanel").toggle();
});

$("#searchButton").on("click", function () {
	var policyNo = $("#policyNo").val();
	$("#currentPolicyNo").text(policyNo);
});
```

### Step 2：把 selector、事件、DOM 更新拆開理解

這段 jQuery 範例至少包含三件事：

- `#searchButton`：事件綁定目標
- `#policyNo`：讀取輸入值
- `#currentPolicyNo`：更新顯示區塊

很多學員之所以看不懂，不是語法太難，而是沒有把這三層拆開。

## Prototype.js 風格示例

```javascript
$("searchButton").observe("click", function () {
	var policyNo = $("policyNo").getValue();
	$("currentPolicyNo").update(policyNo);
});
```

### Step 3：Prototype.js 與 jQuery 的共同語意

雖然兩者語法不同，但本質上都在做同一件事：

- 抓 DOM
- 監聽事件
- 改畫面

這點要明講，否則學員會把 Prototype.js 當成完全陌生技術，實際上它只是另一種老派 DOM 操作風格。

## 這種寫法的優點與問題

### 優點

- 直接、快速、容易上手
- 小型頁面可很快完成互動

### 問題

- DOM id 改名就容易整段失效
- 頁面邏輯分散在多個 script 區塊
- 畫面狀態難追蹤
- 測試性通常不高

## 維運時怎麼讀這種程式

1. 先找事件入口，例如 `click`、`change`、`submit`。
2. 再看 selector 指向哪些 DOM 節點。
3. 最後畫出流程：讀值、判斷、更新畫面、可能送 request。

### Step 4：判斷這段程式是 UI 問題還是 API 問題

這是維運時非常關鍵的一步。畫面沒更新，不一定是 API 壞掉，可能只是：

- selector 寫錯
- DOM id 改了
- 事件沒有綁到新元素
- 更新位置不對

所以在 05 裡要建立一個習慣：

- 先判斷是前端 DOM 流程斷掉，還是後端回應有問題

### Step 5：事件委派為什麼重要

舊系統常有動態新增 DOM 的情況。若事件只綁在初始元素上，新增元素通常不會自動繼承行為。

這時要讓學員知道：

#### 直接綁定 vs 事件委派的差異

```javascript
// ❌ 直接綁定：只對「頁面初始存在」的 .claim-row 有效
// 若後來 AJAX 新增了新的 .claim-row，不會觸發這個 handler
$(".claim-row").on("click", function () {
  $(this).toggleClass("selected");
});

// ✅ 事件委派：綁在穩定存在的父容器上，子元素新增後仍可觸發
// 語法：$(靜態父容器).on(事件, 動態子選擇器, handler)
$("#claimTable").on("click", ".claim-row", function () {
  $(this).toggleClass("selected");
});
```

Prototype.js 等效寫法（較舊系統常見）：

```javascript
// Prototype.js 通常需要在每次 DOM 更新後重新 observe，
// 這也是為什麼舊系統常出現「新增後按鈕沒反應」的問題
document.on("click", ".claim-row", function(event, element) {
  element.toggleClassName("selected");
});
```

## 完整維運情境範例

以下模擬一個真實接手的 JSP 頁（保單查詢 + 顯示理賠面板），混有 jQuery 與 inline script：

```html
<!-- policy-query.jsp（模擬接手的頁面片段）-->
<div id="searchSection">
  <input type="text" id="policyNo" placeholder="輸入保單號">
  <button id="searchButton">查詢</button>
</div>

<div id="resultSection" style="display:none">
  <p>保單：<span id="resultPolicyNo"></span></p>
  <p>狀態：<span id="resultStatus"></span></p>
  <button id="toggleClaimButton">顯示理賠明細</button>
</div>

<div id="claimPanel" style="display:none">
  <ul id="claimList"></ul>
</div>

<script>
// 查詢按鈕 ── 讀值、送 request、更新畫面三步驟
$("#searchButton").on("click", function () {
  var policyNo = $("#policyNo").val();
  if (!policyNo) {
    alert("請輸入保單號");
    return;
  }

  $.ajax({
    url: "/api/policies/" + policyNo,
    method: "GET",
    headers: { Authorization: "Bearer " + localStorage.getItem("accessToken") },
    success: function (data) {
      // 1. 把值填進 DOM
      $("#resultPolicyNo").text(data.policyNo);
      $("#resultStatus").text(data.status);
      // 2. 顯示結果區塊
      $("#resultSection").show();
      // 3. 重置理賠面板狀態（避免舊資料殘留）
      $("#claimPanel").hide();
      $("#claimList").empty();
    },
    error: function (xhr) {
      if (xhr.status === 404) {
        alert("查無此保單");
      } else if (xhr.status === 401) {
        alert("未登入或 token 已過期，請重新登入");
      } else {
        alert("系統錯誤，請稍後再試");
      }
    }
  });
});

// 切換理賠明細面板
$("#toggleClaimButton").on("click", function () {
  var policyNo = $("#resultPolicyNo").text();
  var panel = $("#claimPanel");

  if (panel.is(":visible")) {
    panel.hide();
    $(this).text("顯示理賠明細");
  } else {
    $.get("/api/policies/" + policyNo + "/claims",
      function (claims) {
        var list = $("#claimList").empty();
        if (claims.length === 0) {
          list.append("<li>目前無理賠紀錄</li>");
        } else {
          $.each(claims, function (i, claim) {
            list.append(
              "<li>" + claim.claimNo + " — " + claim.status + "</li>"
            );
          });
        }
        panel.show();
        $("#toggleClaimButton").text("隱藏理賠明細");
      }
    );
  }
});
</script>
```

**維運閱讀步驟**（教學員用）：

1. 先找所有 `.on("click", ...)` ── 找出兩個事件入口：`#searchButton`、`#toggleClaimButton`。
2. `#searchButton` 做了三件事：讀 `#policyNo` 的值 → 送 AJAX → 填 `#resultPolicyNo`、`#resultStatus`。
3. `#toggleClaimButton` 根據面板是否可見決定收合或展開，展開時才打 API（延遲載入）。
4. 兩個 handler 都有錯誤回饋，不依賴 console。
5. 理賠列表延遲載入：只有使用者點按鈕才呼叫 API，符合舊系統常見的按需查詢模式。

- 直接綁定：適合固定 DOM
- delegated event：適合動態元素

這可以順接到後續 exercises 的 event delegation 題。

## 常見錯誤

- 只看 HTML，不看載入的 script 檔。
- 看不懂是因為沒先找事件入口，而是從上到下硬讀。
- 改了 DOM id 卻忘了同步更新 selector。
- 在多處重複綁定同一事件，造成行為重複觸發。
- 看到頁面沒反應就先懷疑 API，沒有先檢查 DOM 與事件。
- 動態生成元素仍沿用直接綁定事件，導致新元素沒反應。

## 自我檢查清單

- 我能快速找出頁面的事件入口嗎？
- 我能把一段 jQuery / Prototype.js 程式拆成讀值、判斷、更新三段嗎？
- 我能區分 DOM 問題與 API 問題嗎？
- 我知道什麼情況應考慮事件委派嗎？

## 練習題

1. 找出某頁面點擊按鈕後，哪些 DOM 元素會被更新。
2. 把一段 jQuery click handler 用自然語言描述成流程。
3. 說明 Prototype.js 與 jQuery 寫法有哪些共同點。
4. 解釋某個動態新增元素為什麼沒有綁到 click 行為。

## 練習解答方向

1. 從 selector 與 `text()`、`html()`、`toggle()` 這類方法找起。
2. 描述流程時要包含讀值、判斷、畫面更新三段。
3. 兩者共同點都是直接操作 DOM，而不是用元件狀態管理。
4. 動態元素題要先檢查事件綁定時機與是否應改用 delegated event。

## 驗收標準

- 能看懂 selector 與 click handler
- 能說出這類程式碼為什麼容易與畫面耦合
- 能將一段舊式互動邏輯用文字描述成流程
- 能指出改動 DOM 時最容易出錯的地方
- 能用維運角度區分 UI 問題與 API 問題