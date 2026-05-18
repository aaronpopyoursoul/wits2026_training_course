# Module 01 — Vue 3 / Vite / TypeScript 與 SFC 基礎

## 模組目標

這一模組要把學員從「知道 Vue 是前端框架」帶到「能看懂並建立一個現代 Vue 專案骨架」。學完這一章後，學員應能理解 Vite 為什麼取代傳統 bundler 起步方式、SFC 為什麼是 Vue 的基本工作單位，以及 TypeScript 在前端專案中的實際價值。

## 情境說明

假設你要為既有保險後台建立一個新的前台入口，需求包括登入、查詢保單、查詢理賠進度。這時如果還用單一 HTML 加 script 的方式，很快就會面臨結構混亂、狀態難管理、頁面難拆分的問題。因此需要一個能支援元件化、型別化與快速本地開發的前端基礎。

## 核心重點

- Vue 3 專案結構
- Vite 開發流程
- TypeScript 在 Vue 中的用途
- SFC 單檔元件
- `ref` / `reactive`
- `computed` / `watch`
- `v-model`、`v-if`、`v-for`

## 教學步驟

### Step 1：理解 Vite 專案的角色

Vite 不是只負責「把專案跑起來」，它同時提供：

- 開發伺服器
- 快速 HMR
- build 輸出
- 與 Vue / TypeScript 的整合基礎

### Step 2：理解 SFC 為什麼重要

Vue 的核心工作單位不是傳統 HTML 頁面，而是 `.vue` 單檔元件。它通常包含：

- `<template>`：畫面結構
- `<script setup lang="ts">`：行為與資料
- `<style>`：局部樣式

### Step 3：理解響應式基礎

最小範例：

```vue
<script setup lang="ts">
import { computed, ref } from 'vue'

const policyNo = ref('PL20240001')
const uppercasePolicyNo = computed(() => policyNo.value.toUpperCase())
</script>

<template>
  <div>
    <input v-model="policyNo" />
    <p>{{ uppercasePolicyNo }}</p>
  </div>
</template>
```

但如果只停在這個範例，學員通常還是不知道 `ref`、`reactive`、`computed`、`watch` 到底各自該在什麼時候用。因此這一節要拆開講清楚，而不是只讓學員看到程式能跑。

### Step 3-1：`ref` 是什麼，什麼時候用

`ref` 適合處理單一值或單一物件參考，例如：

- 輸入框文字
- 是否載入中
- 目前 token
- 被選中的保單號

範例：

```vue
<script setup lang="ts">
import { ref } from 'vue'

const policyNo = ref('PL20240001')
const loading = ref(false)
</script>

<template>
  <input v-model="policyNo" />
  <p v-if="loading">資料載入中...</p>
</template>
```

講解重點：

- `policyNo` 本身不是字串，而是 Vue 包裝過的響應式參考。
- 在 `script` 裡取值要用 `policyNo.value`。
- 在 `template` 裡 Vue 會自動解包，所以直接寫 `policyNo` 即可。

常見錯誤：

- 在 `script` 裡忘記 `.value`。
- 以為普通變數也會自動更新畫面。
- 把所有資料都塞成一個巨大 `ref` 物件，反而更難維護。

### Step 3-2：`reactive` 是什麼，什麼時候用

`reactive` 適合處理一組彼此相關的欄位，尤其是表單或查詢條件物件。

範例：

```vue
<script setup lang="ts">
import { reactive } from 'vue'

const searchForm = reactive({
  policyNo: '',
  insuredId: '',
  includeExpired: false
})
</script>

<template>
  <input v-model="searchForm.policyNo" placeholder="保單號" />
  <input v-model="searchForm.insuredId" placeholder="身分證字號" />
</template>
```

講解重點：

- `reactive` 比較像是把整個物件轉成可追蹤狀態。
- 學員會更容易用它處理多欄位表單。
- 若只是單一值，不一定要用 `reactive`。

`ref` 與 `reactive` 的選擇原則：

- 單一值、簡單旗標、單一選擇值：優先用 `ref`
- 一組關聯欄位、表單、查詢條件：通常適合 `reactive`

常見錯誤：

- 用 `reactive` 包 primitive 值。
- 解構 `reactive` 後直接使用，結果失去響應式語意。
- 不知道 `ref` 與 `reactive` 都能處理物件，卻不知道該如何依可讀性選型。

### Step 3-3：`computed` 是什麼，什麼時候用

`computed` 是「衍生值」，意思是它不是原始資料，而是從現有狀態算出來的結果。

範例：

```vue
<script setup lang="ts">
import { computed, ref } from 'vue'

const policyNo = ref('pl20240001')
const normalizedPolicyNo = computed(() => policyNo.value.trim().toUpperCase())
</script>

<template>
  <p>原始輸入：{{ policyNo }}</p>
  <p>格式化結果：{{ normalizedPolicyNo }}</p>
</template>
```

講解重點：

- `computed` 適合做格式化、彙總、顯示文字轉換。
- 它應該是「讀資料算結果」，而不是做 API 呼叫或副作用。
- 在保險場景中，可用來產出狀態顯示文字、格式化保單號、整理畫面標題。

常見錯誤：

- 在 `computed` 裡做非同步請求。
- 用 `computed` 取代所有 methods，導致概念混亂。
- 看不出「原始狀態」與「衍生狀態」的差異。

### Step 3-4：`watch` 是什麼，什麼時候用

`watch` 用來觀察某個狀態變化後，執行額外動作。這裡的重點不是「算結果」，而是「狀態改了，要不要觸發某件事」。

範例：

```vue
<script setup lang="ts">
import { ref, watch } from 'vue'

const policyNo = ref('')

watch(policyNo, (newValue, oldValue) => {
  console.log('policyNo changed:', oldValue, '->', newValue)
})
</script>
```

在本課脈絡中，`watch` 常見用途包括：

- 監看 route 參數變化後重查資料
- 監看查詢欄位變化後重設錯誤訊息
- 監看登入狀態變化後導頁

講解重點：

- `computed` 是拿來算值。
- `watch` 是拿來做反應動作。

常見錯誤：

- 能用 `computed` 解的問題卻寫成 `watch`。
- 在 `watch` 裡塞太多副作用，導致流程難懂。
- 不清楚是要 watch 單一欄位、整個物件，還是路由參數。

### Step 3-5：`computed` 與 `watch` 怎麼分

這是初學 Vue 最容易混淆的一段，建議直接用一句話記：

- `computed`：我要根據現有資料算出一個新值
- `watch`：我要在資料變化後執行某個動作

對照表：

| 情境 | 較適合 | 原因 |
|------|--------|------|
| 把保單號轉大寫 | `computed` | 只是衍生顯示值 |
| 登入成功後跳轉頁面 | `watch` | 需要觸發副作用 |
| 顯示理賠狀態中文名稱 | `computed` | 由既有狀態映射顯示文字 |
| route 參數改變後重查 API | `watch` | 需要重新執行請求 |

### Step 3-6：`v-model` 是什麼，為什麼重要

`v-model` 是 Vue 最常用的雙向綁定語法，初學者在做登入表單、查詢表單時會大量使用。

範例：

```vue
<script setup lang="ts">
import { ref } from 'vue'

const username = ref('')
</script>

<template>
  <input v-model="username" placeholder="請輸入帳號" />
  <p>目前輸入：{{ username }}</p>
</template>
```

講解重點：

- 輸入框內容會同步回狀態。
- 狀態更新後畫面也會重新顯示。
- 它是表單開發最基礎的連接點。

常見錯誤：

- 不知道 `v-model` 綁的是哪個狀態。
- 一個欄位被多個狀態來源控制，造成資料不同步。
- 只會在原生 input 上使用，不知道元件也能透過 props / emit 對接 `v-model`。

### Step 3-7：`v-if` 是什麼，什麼時候用

`v-if` 用來做條件式渲染，也就是某段畫面是否要出現。

範例：

```vue
<template>
  <p v-if="loading">資料載入中...</p>
  <p v-else-if="errorMessage">{{ errorMessage }}</p>
  <p v-else>資料已載入完成</p>
</template>
```

在本課情境中，`v-if` 常用來處理：

- 尚未登入時不顯示受保護內容
- 查詢中顯示 loading
- 查詢失敗顯示錯誤訊息
- 沒資料時顯示 empty state

常見錯誤：

- 把所有條件邏輯直接寫進 template，導致可讀性下降。
- 沒分清楚 loading、error、empty 三種不同狀態。
- 畫面不出現就以為是 CSS 問題，其實是 `v-if` 條件沒成立。

### Step 3-8：`v-for` 是什麼，什麼時候用

`v-for` 用來根據陣列資料渲染多筆項目，是列表頁、選單、表格的基礎。

範例：

```vue
<script setup lang="ts">
const menuItems = [
  { name: '保單摘要', path: '/policies' },
  { name: '理賠進度', path: '/claims' }
]
</script>

<template>
  <ul>
    <li v-for="item in menuItems" :key="item.path">
      {{ item.name }}
    </li>
  </ul>
</template>
```

講解重點：

- `v-for` 常跟 API 回傳陣列資料一起出現。
- `:key` 不是裝飾，而是讓 Vue 正確辨識清單項目。
- 在保險系統前台，可用於保單列表、理賠紀錄列表、導覽選單。

常見錯誤：

- 忘記加 `:key`。
- 用 index 當 key，卻不知道在動態列表中會有風險。
- 在 `v-for` 中又疊很多複雜條件，導致 template 難以維護。

### Step 3-9：把這些概念串成一個完整畫面

下面這段程式要讓學員看到，這些語法不是分散知識點，而是會同時出現在真實頁面：

```vue
<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'

const searchForm = reactive({
  policyNo: ''
})
const loading = ref(false)
const result = ref<string[]>([])
const errorMessage = ref('')

const normalizedPolicyNo = computed(() => searchForm.policyNo.trim().toUpperCase())

watch(
  () => searchForm.policyNo,
  () => {
    errorMessage.value = ''
  }
)

async function submit() {
  loading.value = true

  try {
    result.value = normalizedPolicyNo.value ? [normalizedPolicyNo.value] : []
  } catch (error) {
    errorMessage.value = '查詢失敗'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div>
    <input v-model="searchForm.policyNo" placeholder="請輸入保單號" />
    <button @click="submit">查詢</button>

    <p v-if="loading">查詢中...</p>
    <p v-else-if="errorMessage">{{ errorMessage }}</p>
    <ul v-else-if="result.length">
      <li v-for="item in result" :key="item">{{ item }}</li>
    </ul>
    <p v-else>尚無資料</p>
  </div>
</template>
```

這一段可以順便讓學員建立三個觀念：

1. `ref`、`reactive` 是狀態來源
2. `computed` 是衍生值
3. `watch`、`v-model`、`v-if`、`v-for` 是把狀態與畫面行為連起來的關鍵橋梁

### Step 4：理解 TypeScript 在前端的實際價值

對這門課來說，TypeScript 的重點不是語法炫技，而是：

- DTO 型別清楚
- 元件 props 型別可驗證
- API response 結構不容易亂掉

## 常見錯誤

- 把 `.vue` 檔案當成只是 HTML 包裝。
- 不分 `ref` 與一般變數，導致畫面不更新。
- 對 `computed` 與 `watch` 的用途混淆。
- 把所有欄位都塞進單一狀態物件，導致資料來源不清楚。
- `v-if` 與 `v-for` 混用時，不知道畫面到底為什麼沒出現。
- 引入 TypeScript 後仍全部使用 `any`。

## 自我檢查清單

- 我能說明 Vite 與 Vue 專案的關係嗎？
- 我知道 SFC 的三個主要區塊用途嗎？
- 我能分辨 `ref`、`computed`、`watch` 的角色嗎？
- 我能說明 TypeScript 為什麼能幫助 API 對接嗎？

## 練習題

1. 建立一個保單查詢輸入框元件，使用 `v-model` 綁定輸入。
2. 用 `computed` 產生格式化後的保單號顯示。
3. 用 `watch` 監聽輸入變化，當使用者重新輸入時清掉錯誤訊息。
4. 用 `v-if` 顯示 loading、error、empty 三種狀態。
5. 用 `v-for` 渲染查詢結果清單。
6. 用 TypeScript 為保單摘要資料定義型別。

## 練習解答方向

1. 輸入框題要先分清資料來源與畫面顯示資料。
2. `computed` 題要指出它適合衍生值，不適合直接處理副作用。
3. `watch` 題要說明為什麼這是狀態變化後的動作，而不是衍生值。
4. `v-if` 題要把 loading、error、empty 拆成不同條件，不要全部混在一起。
5. `v-for` 題要補上合理的 `:key`。
6. 型別題要讓欄位名稱對齊既有 API，不要只求能編譯通過。

## 驗收標準

- 能說明 Vue 3、Vite、TypeScript 的基本角色
- 能建立至少一個 `.vue` 單檔元件
- 能分辨 `ref` 與 `reactive` 的使用情境
- 能分辨 `computed` 與 `watch` 的責任差異
- 能運用 `v-model`、`v-if`、`v-for` 完成基本互動與畫面切換
- 能定義前端對接 API 的基本型別