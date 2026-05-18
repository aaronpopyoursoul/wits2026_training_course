# Module 02 — Options API / Composition API 與元件化設計

## 模組目標

這一模組要讓學員不是只聽過 Options API 與 Composition API，而是能比較兩者在真實專案中的差異，並理解為什麼本課主軸會選 Composition API。同時也要建立元件拆分、props / emit / slots 與 composables 的基本設計感。

## 情境說明

假設你要做登入頁、保單查詢頁與理賠查詢頁。如果所有畫面、狀態與 API 呼叫都塞進同一個元件，很快就會變得難維護。因此需要理解：哪些邏輯應拆成元件、哪些應抽成 composable、哪些適合透過 props / emit 傳遞。

## 核心重點

- Options API 與 Composition API 比較
- Components 拆分原則
- props / emit
- slots
- composables 的用途

## 比較範例

### Options API

```vue
<script lang="ts">
export default {
  data() {
    return {
      policyNo: ''
    }
  },
  methods: {
    submit() {
      console.log(this.policyNo)
    }
  }
}
</script>
```

### Composition API

```vue
<script setup lang="ts">
import { ref } from 'vue'

const policyNo = ref('')

function submit() {
  console.log(policyNo.value)
}
</script>
```

## 教學步驟

### Step 1：先比較兩者在閱讀與維護上的差異

Options API 對初學者較直觀，但邏輯容易分散在 `data`、`methods`、`computed`、`watch`。Composition API 則更適合把同一功能的相關邏輯聚在一起。

### Step 1-1：不要只比語法，要比「功能被放在哪裡」

很多學員比較 Options API 與 Composition API 時，只會說一個比較舊、一個比較新，這種比較方式太表面。真正要比較的是同一個功能在專案裡如何被組織。

以登入表單為例，Options API 常會把邏輯拆在：

- `data`：表單欄位
- `computed`：按鈕可否送出
- `watch`：欄位變化時清錯誤
- `methods`：登入送出

Composition API 則可把同一組登入邏輯放在一起，閱讀時比較容易沿著功能走。

### Step 1-2：Options API 比較適合什麼情境

不是所有專案都必須完全排斥 Options API。對教學來說，學員至少要知道它的優點：

- 初學者較容易從 `data`、`methods` 直覺理解
- 小型元件、邏輯簡單時可讀性不差

但它的限制也要說清楚：

- 同一功能邏輯容易分散
- 元件變大後，維護成本上升
- 重用邏輯時，沒有 composable 那麼自然

### Step 1-3：Composition API 比較適合什麼情境

Composition API 在這門課之所以是主軸，不是因為它「比較潮」，而是因為 06 的主題會碰到：

- 表單邏輯
- 路由切換
- API 呼叫
- loading / error state
- JWT 狀態管理

這些邏輯若分散在多個 options 區塊裡，專案長大後會很難追。Composition API 較適合把同一功能相關邏輯聚在一起。

### Step 2：建立元件拆分觀念

以保單查詢畫面為例，可以拆成：

- `PolicySearchForm`
- `PolicySummaryCard`
- `PageSectionTitle`

### Step 2-1：元件拆分不是為了拆而拆

很多學員第一次學元件化，容易把畫面切得過細，最後變成：

- 一個標題一個元件
- 一個按鈕一個元件
- 一個段落一個元件

這種拆法會讓檔案變多，但不一定更好維護。判斷標準應該是：

- 這段 UI 是否有清楚責任
- 是否可能被重用
- 是否能讓父頁面更容易閱讀

### Step 2-2：頁面、區塊元件、基礎元件的分層

建議直接教學員分三層：

- Page：負責整頁流程，例如 `PolicySummaryPage`
- Section / Feature Component：負責某個功能區塊，例如 `PolicySearchForm`
- Base / Shared Component：較通用的 UI 元件，例如 `PageTitle`

這樣做的好處是，學員在 06 後半段就比較知道 composable、service、component 各自該放哪裡。

### Step 3：理解 props / emit

父元件傳資料給子元件用 props，子元件通知父元件則用 emit，不要用隱性共享狀態代替元件溝通。

### Step 3-1：props 是由上往下的資料流

範例：

```vue
<script setup lang="ts">
defineProps<{
  policyNo: string
  loading: boolean
}>()
</script>

<template>
  <p>保單號：{{ policyNo }}</p>
  <p v-if="loading">查詢中...</p>
</template>
```

講解重點：

- props 讓子元件只專注顯示與局部互動
- 資料來源仍由父元件控制

### Step 3-2：emit 是由下往上的事件通知

範例：

```vue
<script setup lang="ts">
const emit = defineEmits<{
  submit: [policyNo: string]
}>()

const policyNo = ref('')

function onSubmit() {
  emit('submit', policyNo.value)
}
</script>
```

講解重點：

- 子元件不直接決定整體流程
- 子元件只把事件往上丟，由父元件決定後續動作

### Step 3-3：props / emit 與 store 的邊界

不要因為專案有 Pinia，就讓所有元件都直接讀 store。教學上要讓學員知道：

- 父子元件之間的局部溝通，優先用 props / emit
- 跨頁或全域狀態，才考慮 store

這是避免元件過度耦合的重要觀念。

### Step 4：理解 slots 與 composables

- slots：處理可重用版型與插槽內容
- composables：處理跨元件重用的狀態與邏輯，例如登入流程、loading 控制、API 查詢封裝

### Step 4-1：slots 不是高級語法炫技

slots 的目的是讓元件保留結構一致性，同時允許局部內容替換。

範例：

```vue
<template>
  <section class="page-block">
    <header>
      <slot name="title" />
    </header>
    <div>
      <slot />
    </div>
  </section>
</template>
```

適合情境：

- 固定版型，不同內容
- 同樣卡片骨架，不同頁面內容

不適合情境：

- 只是為了少傳幾個 props
- 把商業邏輯藏進插槽內容，導致閱讀困難

### Step 4-2：composable 是邏輯重用，不是任何東西都抽出去

例如登入流程可抽成：

- 使用者輸入狀態
- submit 行為
- loading / error state

但如果某段邏輯只屬於單一頁面，未必需要抽成 composable。

### Step 4-3：什麼該留在元件，什麼該抽成 composable

建議用這個判斷方式：

- 跟畫面結構強綁定：留在元件
- 跨多個元件可重用：考慮抽成 composable
- 跟 HTTP 請求本身有關：通常放 service

## 完整對照範例

以下範例讓學員直接看到 page、component、props、emit、composable 的關係：

```vue
<!-- Parent Page -->
<script setup lang="ts">
import PolicySearchForm from './PolicySearchForm.vue'
import PolicySummaryCard from './PolicySummaryCard.vue'
import { usePolicySearch } from '../composables/usePolicySearch'

const { policy, loading, search } = usePolicySearch()
</script>

<template>
  <PolicySearchForm @submit="search" />
  <PolicySummaryCard :policy="policy" :loading="loading" />
</template>
```

這段要帶出的重點是：

- Page 管流程
- Form 元件負責輸入與 emit
- Card 元件負責顯示
- composable 管共通邏輯

## 常見錯誤

- 因為 Composition API 新，就把所有東西都抽成 composable。
- props / emit 邊界不清，導致資料流難理解。
- 把元件拆得太細，卻沒有實際重用價值。
- 把所有父子溝通都改成 store，反而讓元件耦合更深。
- 使用 slots 時只追求彈性，卻犧牲可讀性。
- 比較 Options API 與 Composition API 時只比語法，不比維護性。

## 自我檢查清單

- 我能解釋為什麼這門課主軸放 Composition API 嗎？
- 我知道 props 與 emit 各自解決什麼問題嗎？
- 我能判斷某段邏輯應留在元件內，還是抽成 composable 嗎？
- 我能指出元件拆分的目的是可讀性與重用，而不是追求檔案數量嗎？

## 練習題

1. 分別用 Options API 與 Composition API 寫一個簡單登入表單。
2. 把保單查詢頁拆成表單元件與結果元件。
3. 用 props / emit 完成父子元件間的查詢提交流程。
4. 設計一個帶 title slot 的共用區塊元件。
5. 把登入邏輯抽成 `useAuthForm` composable。

## 練習解答方向

1. 比較題要說明兩種寫法在維護與閱讀上的差異。
2. 拆元件題要清楚界定誰負責輸入、誰負責顯示。
3. props / emit 題要明確指出資料向下、事件向上。
4. slot 題要說明為什麼這題適合插槽，而不是只是多傳 props。
5. composable 題要避免把畫面專屬細節也一起抽出去。

## 驗收標準

- 能說明 Options API 與 Composition API 的差異
- 能用 props / emit 完成父子元件溝通
- 能判斷 slots 與 composable 的適用時機
- 能設計至少一個可重用 composable
- 能說明元件拆分背後的設計理由