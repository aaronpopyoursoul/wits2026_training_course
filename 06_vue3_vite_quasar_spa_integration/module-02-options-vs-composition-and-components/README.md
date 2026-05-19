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

這一步的核心不是把畫面切碎，而是讓每個區塊有清楚責任。對 06 這門課來說，元件拆分至少要回答三個問題：

1. 哪些東西屬於整頁流程。
2. 哪些東西只是某個功能區塊。
3. 哪些東西未來可能在多個頁面重用。

若沒有先回答這三個問題，學員很容易把整頁都寫在一個 `Page.vue`，或反過來把畫面拆成太多微小元件，最後造成閱讀成本上升。

### Step 2-0：先判斷「這是頁面問題、功能問題，還是共用 UI 問題」

以「保單查詢頁」為例，可以先這樣分類：

- 頁面層：負責路由進入、整體查詢流程、錯誤狀態、loading 狀態
- 功能區塊層：負責保單輸入表單、摘要卡片、提示訊息
- 共用 UI 層：負責標題、卡片外框、空狀態提示

如果這樣分，學員在實作時就比較知道：

- `PolicySummaryPage.vue` 不應自己負責每個 input 的細節 UI
- `PolicySearchForm.vue` 不應直接決定整頁導頁或全域狀態
- `PageSectionTitle.vue` 不應知道任何 API 細節

### Step 2-0-1：一個簡單判斷口訣

可以教學員用這個口訣快速判斷：

- 會不會跟著 route 切換：通常是 page
- 會不會在同一頁出現一整塊功能：通常是 feature component
- 會不會在很多頁都長得差不多：通常是 shared component

## 更完整的拆分範例

假設要做一個保單查詢頁，需求如下：

1. 使用者輸入保單號。
2. 送出後顯示 loading。
3. 查詢成功後顯示保單摘要。
4. 查詢失敗後顯示錯誤訊息。

若全部都塞在同一個 page 中，短期雖可運作，但很快就會出現：

- template 太長
- 表單與結果混在一起
- 後續想重用查詢卡片時只能複製貼上

比較合理的拆法可如下：

```text
pages/
  PolicySummaryPage.vue
components/
  policy/
    PolicySearchForm.vue
    PolicySummaryCard.vue
  shared/
    PageSectionTitle.vue
```

### Page 層範例

```vue
<script setup lang="ts">
import { ref } from 'vue'
import PolicySearchForm from '../components/policy/PolicySearchForm.vue'
import PolicySummaryCard from '../components/policy/PolicySummaryCard.vue'
import PageSectionTitle from '../components/shared/PageSectionTitle.vue'
import { getPolicySummary } from '../services/insurance'
import type { PolicySummary } from '../types/insurance'

const policy = ref<PolicySummary | null>(null)
const loading = ref(false)
const errorMessage = ref('')

async function search(policyNo: string) {
  loading.value = true
  errorMessage.value = ''

  try {
    policy.value = await getPolicySummary(policyNo)
  } catch (error) {
    policy.value = null
    errorMessage.value = '查詢失敗，請確認保單號或稍後再試'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <PageSectionTitle
    title="保單摘要查詢"
    subtitle="輸入保單號後，查詢目前狀態與商品資訊"
  />

  <PolicySearchForm :loading="loading" @submit="search" />

  <p v-if="errorMessage">{{ errorMessage }}</p>

  <PolicySummaryCard
    v-if="policy || loading"
    :policy="policy"
    :loading="loading"
  />
</template>
```

這段 page 範例要教的不是語法而已，而是責任分工：

- page 管流程
- page 負責決定何時查詢
- page 負責持有結果、loading、error
- page 負責把資料傳給子元件

### Feature Component：表單元件範例

```vue
<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  loading: boolean
}>()

const emit = defineEmits<{
  submit: [policyNo: string]
}>()

const policyNo = ref('')

function onSubmit() {
  emit('submit', policyNo.value.trim())
}
</script>

<template>
  <form @submit.prevent="onSubmit">
    <label for="policyNo">保單號</label>
    <input id="policyNo" v-model="policyNo" type="text" />
    <button type="submit" :disabled="props.loading || !policyNo.trim()">
      {{ props.loading ? '查詢中...' : '查詢' }}
    </button>
  </form>
</template>
```

這個元件的責任很明確：

- 管自己的輸入欄位
- 把查詢請求往上丟
- 不直接碰 API
- 不直接決定整頁結果顯示方式

### Feature Component：結果卡片範例

```vue
<script setup lang="ts">
import type { PolicySummary } from '../../types/insurance'

defineProps<{
  policy: PolicySummary | null
  loading: boolean
}>()
</script>

<template>
  <section>
    <p v-if="loading">資料讀取中...</p>

    <template v-else-if="policy">
      <h3>查詢結果</h3>
      <p>保單號：{{ policy.policyNo }}</p>
      <p>保戶姓名：{{ policy.policyHolderName }}</p>
      <p>商品代碼：{{ policy.productCode }}</p>
      <p>保單狀態：{{ policy.policyStatus }}</p>
    </template>

    <p v-else>尚未查詢資料</p>
  </section>
</template>
```

這個元件適合教學員理解「顯示元件」的概念：

- 它不決定何時查詢
- 它不決定 error 怎麼轉譯
- 它專注於接收資料並顯示

### Shared Component：共用標題區塊

```vue
<script setup lang="ts">
defineProps<{
  title: string
  subtitle?: string
}>()
</script>

<template>
  <header>
    <h2>{{ title }}</h2>
    <p v-if="subtitle">{{ subtitle }}</p>
  </header>
</template>
```

這類元件通常不碰業務資料，只關心畫面結構與共用 UI。

### Step 2-1：元件拆分不是為了拆而拆

很多學員第一次學元件化，容易把畫面切得過細，最後變成：

- 一個標題一個元件
- 一個按鈕一個元件
- 一個段落一個元件

這種拆法會讓檔案變多，但不一定更好維護。判斷標準應該是：

- 這段 UI 是否有清楚責任
- 是否可能被重用
- 是否能讓父頁面更容易閱讀

還可以再補一個更實務的判斷：

- 抽出去之後，父頁面有沒有因此更容易看懂整體流程

如果抽出去之後，父頁面只是變成大量陌生元件名稱，卻沒有更清楚，那通常代表拆分方式還不夠成熟。

### 反例：拆得太粗

```vue
<script setup lang="ts">
import { ref } from 'vue'
import { getPolicySummary } from '../services/insurance'

const policyNo = ref('')
const policy = ref(null)
const loading = ref(false)
const error = ref('')

async function submit() {
  loading.value = true
  error.value = ''

  try {
    policy.value = await getPolicySummary(policyNo.value)
  } catch (e) {
    error.value = '查詢失敗'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div>
    <h2>保單摘要查詢</h2>
    <input v-model="policyNo" type="text" />
    <button @click="submit">查詢</button>
    <p v-if="loading">查詢中...</p>
    <p v-if="error">{{ error }}</p>
    <div v-if="policy">
      <p>{{ policy.policyNo }}</p>
      <p>{{ policy.policyHolderName }}</p>
      <p>{{ policy.productCode }}</p>
      <p>{{ policy.policyStatus }}</p>
    </div>
  </div>
</template>
```

這種寫法不是錯，但當頁面需求增加，例如：

- 加入欄位驗證
- 加入 reset 按鈕
- 加入空狀態
- 加入不同查詢模式

元件就會快速膨脹。

### 反例：拆得太細

另一個常見錯法是這樣拆：

- `PolicySearchLabel.vue`
- `PolicySearchInput.vue`
- `PolicySearchButton.vue`
- `PolicySummaryPolicyNo.vue`
- `PolicySummaryHolderName.vue`

這種拆法看似很元件化，實際上通常會造成：

- 檔案過多
- 父頁組裝成本高
- 真正重用價值低
- props 傳遞層級過深

因此教學上最好強調：**元件拆分的目標是責任清楚，不是檔案越多越專業。**

### Step 2-2：頁面、區塊元件、基礎元件的分層

建議直接教學員分三層：

- Page：負責整頁流程，例如 `PolicySummaryPage`
- Section / Feature Component：負責某個功能區塊，例如 `PolicySearchForm`
- Base / Shared Component：較通用的 UI 元件，例如 `PageTitle`

這樣做的好處是，學員在 06 後半段就比較知道 composable、service、component 各自該放哪裡。

### Step 2-2-1：三層分工與範例對照

| 層級 | 典型檔案 | 責任 | 不應做的事 |
| --- | --- | --- | --- |
| Page | `PolicySummaryPage.vue` | 串整流程、接 router、呼叫 service 或 composable | 不要塞太多細碎 UI markup |
| Feature Component | `PolicySearchForm.vue`、`PolicySummaryCard.vue` | 專注某個功能區塊 | 不要直接管理整頁導頁與全域狀態 |
| Shared Component | `PageSectionTitle.vue` | 提供可重用 UI 結構 | 不要綁定特定業務 API |

### Step 2-2-2：與 composable / service 的邊界

這裡很適合順手帶出 06 後續模組的分工：

- component：畫面互動與局部狀態
- composable：可重用的頁面邏輯，例如 loading / error / submit flow
- service：真正的 HTTP 請求細節

例如：

- `PolicySearchForm.vue`：收輸入、emit submit
- `usePolicySearch.ts`：管理查詢流程與狀態
- `insurance.ts`：呼叫 `/api/policies/{policyNo}/summary`

### Step 2-3：什麼時候該先不拆

也要讓學員知道，不是所有畫面一開始都要拆。若畫面只有：

- 很少欄位
- 沒有重用需求
- 邏輯非常單純

可以先保留在 page 裡，等到需求真的擴大再抽出。這樣比一開始過度設計更務實。

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