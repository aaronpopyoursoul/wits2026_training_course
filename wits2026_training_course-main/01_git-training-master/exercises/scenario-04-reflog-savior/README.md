# 情境四：致命的誤刪 (The Reflog Savior)

## 背景

你和另一位工程師分工開發 API 整合層。你的負責部分存在 `feature/important-api` 分支，花了三天寫了商品查詢、訂單建立、使用者驗證三套 API 封裝，**尚未 merge 進 develop，也沒有 push 到遠端備份**。

今天清理分支時，手滑執行了：

```bash
git branch -D feature/important-api
```

終端機顯示：

```
Deleted branch feature/important-api (was 4a3f2e1).
```

三天的工作瞬間消失。

但先不要慌——**Git 幾乎不丟資料**。

---

## 核心概念：Git 的安全網

Git 有一個鮮為人知但非常重要的機制：**Reflog（Reference Log）**。

每一次 HEAD 的移動（checkout、commit、merge、reset...），Git 都會在 Reflog 中留下記錄，預設保留 **90 天**。

即使你刪除了分支，那個分支上的 commit 物件仍然存在於 Git 的物件資料庫中，只是沒有任何「標籤」（分支名稱）指向它們而已。在被 `git gc` 清理之前，你都有機會救回。

---

## 前置：製造誤刪情境

```bash
# 確保你在 git-training-master 目錄
cd /path/to/git-training-master

# 建立「重要的 API 分支」並加入一些有意義的 commit
git checkout -b feature/important-api develop

# 模擬三天的開發工作（加入 API 服務層）
mkdir -p src/services

cat > src/services/productApi.ts << 'EOF'
import type { Product, ApiResponse } from '@/types'

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'https://api.example.com'

export async function fetchProducts(): Promise<ApiResponse<Product[]>> {
  const res = await fetch(`${BASE_URL}/v1/products`)
  if (!res.ok) return { success: false, data: null, message: res.statusText }
  const data = await res.json()
  return { success: true, data, message: 'ok' }
}

export async function fetchProductById(id: string): Promise<ApiResponse<Product>> {
  const res = await fetch(`${BASE_URL}/v1/products/${encodeURIComponent(id)}`)
  if (!res.ok) return { success: false, data: null, message: res.statusText }
  const data = await res.json()
  return { success: true, data, message: 'ok' }
}
EOF
git add src/services/productApi.ts
git commit -m "feat(api): add product API service layer"

cat > src/services/orderApi.ts << 'EOF'
import type { Order, ShippingAddress, ApiResponse } from '@/types'

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'https://api.example.com'

export async function createOrder(
  userId: string,
  shippingAddress: ShippingAddress
): Promise<ApiResponse<Order>> {
  const res = await fetch(`${BASE_URL}/v1/orders`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userId, shippingAddress })
  })
  if (!res.ok) return { success: false, data: null, message: res.statusText }
  const data = await res.json()
  return { success: true, data, message: '訂單建立成功' }
}
EOF
git add src/services/orderApi.ts
git commit -m "feat(api): add order creation API service"

cat > src/services/authApi.ts << 'EOF'
import type { User, AuthToken, ApiResponse } from '@/types'

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'https://api.example.com'

export async function loginRequest(
  email: string,
  password: string
): Promise<ApiResponse<AuthToken>> {
  const res = await fetch(`${BASE_URL}/v1/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  })
  if (!res.ok) return { success: false, data: null, message: res.statusText }
  const data = await res.json()
  return { success: true, data, message: '登入成功' }
}

export async function fetchMeRequest(token: string): Promise<ApiResponse<User>> {
  const res = await fetch(`${BASE_URL}/v1/auth/me`, {
    headers: { Authorization: `Bearer ${token}` }
  })
  if (!res.ok) return { success: false, data: null, message: res.statusText }
  const data = await res.json()
  return { success: true, data, message: 'ok' }
}
EOF
git add src/services/authApi.ts
git commit -m "feat(api): add auth API service with JWT bearer token"

# 確認目前狀態（記住最後一個 commit hash）
git log --oneline -5

# === 模擬手滑刪除 ===
git checkout main  # 先切回 main
git branch -D feature/important-api  # 刪除！

echo "分支已刪除，現在嘗試救回..."
```

---

## 任務步驟

### Step 1：查看 Reflog

```bash
# 顯示所有 HEAD 移動記錄
git reflog
```

輸出類似：

```
9a8b7c6 HEAD@{0}: checkout: moving from feature/important-api to main
4a3f2e1 HEAD@{1}: commit: feat(api): add auth API service with JWT bearer token
b2c3d4e HEAD@{2}: commit: feat(api): add order creation API service
c5d6e7f HEAD@{3}: commit: feat(api): add product API service layer
8f9e0d1 HEAD@{4}: checkout: moving from main to feature/important-api
...
```

你需要找的是：**最後一個屬於 `feature/important-api` 的 commit hash**。

在這個例子中是 `4a3f2e1`（`feat(api): add auth API service...`）。

### Step 2：確認那個 commit 仍然存在

```bash
# 使用你找到的 hash 查看 commit 資訊
git show 4a3f2e1

# 會顯示 commit message、作者、時間與改動內容
# 如果能看到，表示這個 commit 尚未被 GC 清理，可以救回
```

### Step 3：重建分支

```bash
# 方法一：在那個 commit 上建立新分支
git checkout -b feature/important-api 4a3f2e1

# 方法二（效果相同，更現代的語法）
git switch -c feature/important-api 4a3f2e1

# 確認分支已恢復，且所有 commit 都在
git log --oneline
```

預期輸出：
```
4a3f2e1 (HEAD -> feature/important-api) feat(api): add auth API service with JWT bearer token
b2c3d4e feat(api): add order creation API service
c5d6e7f feat(api): add product API service layer
8f9e0d1 chore: initial project setup (develop branch)
```

### Step 4：確認檔案完整

```bash
# 確認三個 API 服務檔案都已救回
ls -la src/services/

# 查看檔案內容
cat src/services/productApi.ts
cat src/services/orderApi.ts
cat src/services/authApi.ts
```

### Step 5：Push 到遠端備份（避免再次發生）

```bash
# 立刻推到遠端，以後就不怕本地誤刪了
git push -u origin feature/important-api
```

---

## 延伸：Reflog 的其他用途

### 救回被 reset 覆蓋的 commit

```bash
# 假設你執行了 git reset --hard HEAD~3，想後悔
git reflog

# 找到 reset 前的 commit hash（通常是 HEAD@{1}）
git reset --hard HEAD@{1}
```

### 找回某個時間點的狀態

```bash
# 查看 2 小時前 HEAD 在哪
git reflog --since="2 hours ago"

# 或更直觀的格式
git log -g --pretty=format:"%gd %ci %s" | head -20
```

### 救回被 `git rebase` 弄壞的分支

```bash
git reflog
# 找到 rebase 開始前的 commit（會標記 rebase: start...）
git reset --hard HEAD@{n}  # n 是 rebase 開始前的位置
```

---

## 完成標準

```bash
# 分支已存在
git branch | grep feature/important-api

# 三個服務檔案存在
ls src/services/

# Commit 歷史完整
git log --oneline feature/important-api | head -5
```

---

## 考核重點

| 能力點 | 觀察方式 |
|--------|----------|
| 知道用 `git reflog` 而非放棄 | 第一反應是查 reflog，不是說「沒辦法了」 |
| 能從 reflog 輸出中找到正確的 hash | 找的是最後一個屬於該分支的 commit（非 checkout 的那個） |
| `git checkout/switch -b <branch> <hash>` 指令正確 | 分支在正確的 commit 上重建 |
| 理解 reflog 的有效期限 | 知道預設 90 天、知道 `git gc` 的影響 |
| 事後立刻 push 作為補救 | 避免同樣事故再次發生 |

---

## 預防措施

救回之後，應該養成這兩個習慣：

1. **開發中的分支定期 push 到遠端**（即使未完成）
   ```bash
   git push -u origin feature/important-api
   ```

2. **刪除分支前先確認是否已 merge**
   ```bash
   # 安全刪除（已合併才能刪）
   git branch -d feature/important-api  # 用小寫 -d，未 merge 會拒絕

   # 強制刪除前先查看分支上有幾個獨立 commit
   git log develop..feature/important-api --oneline
   ```

---

## 技術補充：reflog 存放位置

```bash
# reflog 實際上是純文字檔案，存放於 .git/logs/
cat .git/logs/HEAD

# 每個分支也有自己的 reflog
cat .git/logs/refs/heads/feature/important-api
```
