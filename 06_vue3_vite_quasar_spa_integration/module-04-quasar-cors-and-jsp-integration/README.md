# Module 04 — Quasar / CORS / Vue + JSP 漸進式整合

## 模組目標

這一模組的重點不是「把畫面做得更漂亮」，而是讓學員開始理解前端專案的三個現實問題：

- 如何用 Quasar 快速建立可維護、風格一致的頁面骨架
- 為什麼前後端分離之後，瀏覽器會開始出現 CORS 問題
- 為什麼企業專案常常不是一次全面重寫，而是 Vue SPA 與 JSP 先並存

學完後，學員應能說明：

1. Quasar 在這門課中的角色是版型與互動一致性的基礎建設。
2. CORS 是瀏覽器的安全機制，不是 API 壞掉。
3. Vue 與 JSP 並存需要有清楚的路徑邊界與遷移順序。

## 先備知識

- 已理解 Vue 3 基本 component 與 router 概念
- 已知道登入流程與 JWT token 的基本用途
- 已接觸 Spring Boot REST API

## 情境說明

目前前端頁面跑在 `http://localhost:5173`，後端 API 跑在 `http://localhost:8082`。學員在 Postman 測登入 API 正常，但把同一支 API 接到瀏覽器後卻出現跨域錯誤。另一方面，企業現場還有一批 JSP 頁面正在服務舊流程，短期內不能直接關閉。

這個模組要處理的不是單一技術，而是整合問題：

- 新前台怎麼建立一致畫面
- API 怎麼正確被瀏覽器呼叫
- 新舊系統怎麼先一起活著

## 本章核心問題

1. Quasar 為什麼值得學，而不是只用原生 HTML 加 CSS？
2. 為什麼 Postman 可以成功，瀏覽器卻失敗？
3. CORS 要由誰負責設定，責任邊界在哪裡？
4. Vue 與 JSP 並存時，哪些頁面先改、哪些先保留？

## 教學地圖

1. 用 Quasar 建立頁面骨架
2. 釐清 CORS 與 preflight request
3. 建立後端正式開放規則
4. 規劃 Vue SPA 與 JSP 的共存邊界

---

## Step 1：用 Quasar 建立一致的 UI 結構

### Step 1-1：先建立正確心智模型

對初學者來說，Quasar 最容易被誤解成「很多現成元件」。

這個說法沒有錯，但不夠精準。更好的理解方式是：

- `QLayout` 負責整個應用程式的骨架
- `QHeader`、`QDrawer`、`QPageContainer` 負責頁面結構分區
- `QForm`、`QInput`、`QBtn` 負責表單互動
- `QCard`、`QBanner`、`QTable` 負責資料展示與狀態回饋

也就是說，Quasar 幫你解的是「一致性」問題，而不是只解「元件不夠用」的問題。

### Step 1-2：教學順序要先 layout，後 form/table

推薦順序：

1. `QLayout`
2. `QHeader` / `QDrawer` / `QPageContainer`
3. `QPage`
4. `QForm` / `QInput` / `QBtn`
5. `QTable` / `QBanner` / `QCard`

原因很簡單：

- 先學 layout，學員才知道頁面是怎麼被組成的
- 之後再看登入頁與查詢頁，才知道元件放在哪裡、誰管誰

### 最小版型範例

下面這個範例刻意保持很小，只保留「主版型」最必要的部分：

```vue
<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-toolbar-title>Insurance Training SPA</q-toolbar-title>
        <q-btn flat dense label="Dashboard" :to="{ name: 'dashboard' }" />
        <q-btn flat dense label="Policies" :to="{ name: 'policies' }" />
        <q-space />
        <q-btn flat dense label="Logout" @click="handleLogout" />
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>
```

這個結構對應到現有專案中的 `frontend-demo/src/layouts/AppLayout.vue`。教學時可直接對照真實檔案，讓學員看到「教材不是抽象範例」。

### Step 1-3：理解 `view` 字串，不要只會照抄

以 `view="hHh lpR fFf"` 為例，教學時不需要把每個字母全部背起來，但至少要讓學員知道：

- 這是一種版型配置語法
- 它控制 header、drawer、footer 與 page 的排列方式
- 不同字串組合會讓固定區塊與滾動區塊行為不同

可以先用「足夠理解」的方式說明：

| 區塊 | 常見元件 | 角色 |
| --- | --- | --- |
| Header | `QHeader` | 頂端導覽、品牌、操作按鈕 |
| Drawer | `QDrawer` | 側邊選單、模組導覽 |
| Page Container | `QPageContainer` | 真正承載各頁內容 |
| Page | `QPage` | 單一頁面的內容範圍 |

教學重點不是背縮寫，而是理解為什麼我們要先定義整體骨架。

### Step 1-4：登入頁與查詢頁的 Quasar 用法不同

登入頁重點在：

- 欄位輸入
- 表單驗證
- 送出中狀態
- 失敗訊息顯示

查詢頁重點在：

- 查詢條件輸入
- loading / error / empty / success 狀態切換
- 結果區塊或表格顯示

這裡剛好可以自然接到下一個模組的 data fetching 與 error handling。

### 最小登入頁範例

```vue
<template>
  <q-page class="row items-center justify-center bg-grey-2 q-pa-lg">
    <q-card flat bordered style="width: 420px; max-width: 100%">
      <q-card-section>
        <div class="text-h5 text-weight-bold">課程前台登入</div>
      </q-card-section>

      <q-card-section>
        <q-form class="column q-gutter-md" @submit.prevent="submitLogin">
          <q-input v-model="form.username" outlined label="Username" />
          <q-input v-model="form.password" outlined label="Password" type="password" />

          <q-banner v-if="errorMessage" rounded dense class="bg-red-1 text-negative">
            {{ errorMessage }}
          </q-banner>

          <q-btn color="primary" label="登入" type="submit" :loading="isLoading" />
        </q-form>
      </q-card-section>
    </q-card>
  </q-page>
</template>
```

教學時要點出三個地方：

1. `q-form` 幫你把送出流程集中到同一個事件。
2. `q-banner` 比 `alert()` 更適合做頁面內錯誤提示。
3. `q-btn` 的 `:loading` 讓使用者知道請求還在進行。

現有專案對照檔：

- `frontend-demo/src/pages/LoginPage.vue`

### 最小查詢頁範例

```vue
<template>
  <q-page class="q-pa-lg">
    <q-card flat bordered class="q-mb-lg">
      <q-card-section class="row q-col-gutter-md items-end">
        <div class="col-12 col-md-6">
          <q-input v-model="policyNo" outlined label="Policy No" />
        </div>
        <div class="col-12 col-md-auto">
          <q-btn color="primary" label="查詢" :loading="isLoading" @click="loadPolicy" />
        </div>
      </q-card-section>
    </q-card>

    <q-banner v-if="errorMessage" rounded class="bg-red-1 text-negative q-mb-md">
      {{ errorMessage }}
    </q-banner>

    <q-card v-if="policySummary" flat bordered>
      <q-card-section>
        <div>保單號：{{ policySummary.policyNo }}</div>
        <div>保戶：{{ policySummary.policyHolderName }}</div>
      </q-card-section>
    </q-card>
  </q-page>
</template>
```

這裡先故意不用 `QTable`，因為初學者先理解查詢流程，比先理解表格配置更重要。

現有專案對照檔：

- `frontend-demo/src/pages/PolicySummaryPage.vue`

---

## Step 2：理解為什麼會有 CORS

### Step 2-1：CORS 不是後端 bug，而是瀏覽器的安全規則

當前端從 `http://localhost:5173` 呼叫 `http://localhost:8082` 時，這是跨來源請求。瀏覽器會先判斷這個請求是否被允許。

`origin` 由三個部分組成：

- protocol
- host
- port

只要其中一個不同，就不是同源。

例如：

- `http://localhost:5173`
- `http://localhost:8082`

兩者 port 不同，所以就是跨來源。

### Step 2-2：為什麼 Postman 可以，瀏覽器不行

這是教學一定要講透的地方。

- Postman 不是瀏覽器，不受同源政策限制
- 瀏覽器會在送出正式請求前，先檢查是否允許跨域

所以出現「Postman 成功、前端失敗」時，第一個懷疑對象通常是 CORS，而不是 API 業務邏輯。

### Step 2-3：preflight request 要怎麼教

只要請求不是「簡單請求」，瀏覽器就可能先送出 `OPTIONS`。

最常見觸發條件：

- 帶 `Authorization` header
- `Content-Type` 不是簡單表單類型
- 使用 `PUT`、`DELETE` 等方法

瀏覽器可能先送：

```http
OPTIONS /api/policies HTTP/1.1
Origin: http://localhost:5173
Access-Control-Request-Method: GET
Access-Control-Request-Headers: Authorization, Content-Type
```

如果後端沒有正確回覆：

- `Access-Control-Allow-Origin`
- `Access-Control-Allow-Methods`
- `Access-Control-Allow-Headers`

那正式請求根本不會送出去。

教學重點：

- CORS 錯誤很多時候發生在正式 API 執行之前
- 所以 DevTools 看到錯誤，不代表 controller 一定有收到 request

### Step 2-4：怎麼看 DevTools

建議在課堂上示範這個排查順序：

1. 看 Network 面板有沒有先出現 `OPTIONS`
2. 看 response header 有沒有 `Access-Control-Allow-Origin`
3. 看 status code 是 200、401、403 還是根本被 browser block
4. 再回頭檢查前端 base URL 與後端 CORS 設定

---

## Step 3：由後端正式定義 CORS 規則

### Step 3-1：為什麼主軸不是只靠 Vite proxy

Vite proxy 很方便，但它只解決開發階段的「本機繞路」問題。

如果只教 proxy，不教 CORS，學員就會誤以為：

- 前端只要把路徑改一改就好
- 正式環境不需要清楚定義允許來源

這樣的理解是不夠的。

本課的主軸應該是：

- 開發階段可以用 proxy 幫助開發
- 正式環境仍要由後端正式宣告跨域規則

### Step 3-2：CORS 設定欄位要說清楚

| 設定 | 代表意思 |
| --- | --- |
| `allowedOrigins` | 哪些前端來源可以來呼叫 |
| `allowedMethods` | 允許哪些 HTTP 方法 |
| `allowedHeaders` | 允許哪些 request headers |
| `allowCredentials` | 是否允許帶 cookie 或認證資訊 |
| `maxAge` | preflight 結果可快取多久 |

特別要提醒：

- `allowCredentials(true)` 時，`allowedOrigins` 不應亂設成 `*`
- 如果有登入、cookie、或授權資訊，跨域規則要更精準

### 最小 Spring Boot CORS 範例

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);
    return source;
}
```

這個範例可直接對照現有專案：

- `04_rest_api_exception_swagger_jwt/spring-rest-jwt-demo/src/main/java/com/company/training/restjwtdemo/config/SecurityConfig.java`

### Step 3-3：為什麼 Spring Security 也要一起看

如果有 Spring Security，光有 CORS 設定還不夠，還要注意 security filter chain 是否攔掉 preflight request。

最小概念要教到這裡：

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf().disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .anyRequest().authenticated();
}
```

這不是要學員背語法，而是要他們知道：

- CORS 可能在 MVC 層設定
- 但 Security 層也可能影響結果

### Step 3-4：Vite proxy 要怎麼介紹才不會教歪

可以給一個最小範例，但要明講它的角色只是「本地開發輔助」。

```ts
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
    },
  },
})
```

課堂上一定要補一句：

> 代理可以幫你在開發時先工作，但它不等於正式環境已經解好跨域。

---

## Step 4：理解 Vue 與 JSP 的共存策略

### Step 4-1：企業現場很少一次全面重寫

真正的專案限制通常不是「技術做不到」，而是：

- 舊系統還在賺錢
- 後台流程還有人在用
- 一次重寫風險太高
- 團隊時間與預算有限

所以比較常見的做法是：

- 新功能前台優先改成 Vue
- 舊後台或低變動頁面先保留 JSP
- API 與登入機制先共用

### Step 4-2：並存一定要先定義 URL 邊界

如果沒有路徑邊界，最後常見結果就是：

- 導覽混亂
- 權限邏輯分散
- 重整行為不一致
- 測試與部署變得很難切分

建議先定義這種最小邊界：

| URL 前綴 | 處理者 | 用途 |
| --- | --- | --- |
| `/app/**` | Vue SPA | 新前台入口與新功能 |
| `/legacy/**` | JSP | 舊後台或尚未重寫頁面 |
| `/api/**` | Spring Boot | REST API，供兩邊共用 |

### Step 4-3：教學上要把「逐步替換」具體化

不要只說「之後慢慢把 JSP 換掉」。

應該改成：

1. 第一階段：登入、查詢前台改為 Vue
2. 第二階段：高互動流程頁改為 Vue
3. 第三階段：低互動後台頁再評估是否保留或重寫

這樣學生才知道 migration strategy 是可執行的，不是口號。

### 最小部署邊界範例

```nginx
server {
    listen 80;

    location /app/ {
        alias /var/www/vue-spa/;
        try_files $uri $uri/ /app/index.html;
    }

    location /legacy/ {
        proxy_pass http://localhost:8080;
    }

    location /api/ {
        proxy_pass http://localhost:8082;
    }
}
```

這個範例的教學價值不在 Nginx 語法本身，而是讓學生看到：

- Vue、JSP、API 可以在同一個 domain 下分工
- 路徑邊界定清楚之後，部署與驗收才會穩定

---

## 對照現有專案

建議授課時直接對照這幾個檔案：

- `frontend-demo/src/layouts/AppLayout.vue`
- `frontend-demo/src/pages/LoginPage.vue`
- `frontend-demo/src/pages/PolicySummaryPage.vue`
- `04_rest_api_exception_swagger_jwt/spring-rest-jwt-demo/src/main/java/com/company/training/restjwtdemo/config/SecurityConfig.java`

對照方式：

1. 先用 README 的最小範例建立概念
2. 再打開真實檔案，指出課堂範例在專案中的位置
3. 最後讓學員知道哪些地方是簡化版，哪些地方是實務版

## 常見錯誤

- 把 Quasar 當成「只是換皮」，看不到它的 layout 價值
- CORS 一出錯就懷疑 JWT 壞掉
- 只測 Postman，不看瀏覽器 Network 面板
- `allowCredentials` 與 `allowedOrigins` 設定過於寬鬆
- 把 Vue / JSP 並存講成抽象口號，沒有路徑與頁面邊界
- 認為有 Vite proxy 就等於正式環境沒問題

## 課堂練習

1. 用 Quasar 做出一個最小登入頁，包含欄位、送出按鈕、錯誤 banner。
2. 說明 `http://localhost:5173` 呼叫 `http://localhost:8082` 為什麼會觸發 CORS。
3. 用自己的話解釋 `allowedOrigins`、`allowedMethods`、`allowedHeaders`。
4. 畫出一份 Vue SPA 與 JSP 並存的路徑切分圖。
5. 寫出一段 migration 說明，回答哪些頁面先改、哪些先留。

## 驗收標準

- 能說明 Quasar 在專案中的角色是版型與互動一致性
- 能分辨 Postman 成功與瀏覽器失敗的原因差異
- 能解釋 preflight request 與後端 CORS 設定的關係
- 能寫出 Vue SPA 與 JSP 的最小共存策略
- 能把 UI、API、跨域、舊系統共存放進同一個專題脈絡