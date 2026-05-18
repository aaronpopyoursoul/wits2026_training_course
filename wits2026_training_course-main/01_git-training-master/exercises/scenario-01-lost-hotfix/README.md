# 情境一：消失的修補程式 (The Lost Hotfix)

## 背景

你正在 `feature/cart-logic` 分支上開發購物車的進階邏輯（已寫了一半，尚未完成），這時主管 Slack 傳來消息：

> 「線上 main 分支的結帳計算有 Bug：不管買幾件商品，運費都算成同一個金額。客訴一大堆，需要馬上修復。」

你打開 `src/store/cart.ts`，確認了問題所在：`calcTotal` 計算 `subtotal` 時，忘記乘以 `item.quantity`。

現在你有兩個進行中的工作：
1. 未完成的 `feature/cart-logic` 開發
2. 需要立刻修復的線上 Bug

---

## 任務步驟

### Step 1：暫存目前的開發進度

你在 `feature/cart-logic` 上有未 commit 的修改（模擬：修改了 `src/composables/useCart.ts` 加入了一些半成品邏輯）。

```bash
# 先確認目前狀態
git status
git diff

# 暫存所有改動，並附上說明訊息
git stash push -m "WIP: adding discount coupon logic to useCart"

# 確認 stash 清單
git stash list

# 確認工作目錄已乾淨
git status
```

**關鍵理解**：`git stash` 會把 Working Directory 和 Staging Area 的改動打包成一個臨時 commit，讓你切換分支而不會遺失工作。它存在本地，不會 push 到遠端。

### Step 2：切換回 main 並建立 hotfix 分支

```bash
# 切換回 main（確保是最新狀態）
git checkout main
git pull --rebase origin main  # 同步遠端最新

# 從 main 建立 hotfix 分支
git checkout -b hotfix/calc-error

# 確認你現在在正確的分支
git branch
```

### Step 3：修復 Bug

開啟 `src/store/cart.ts`，找到 `calcTotal` 的計算：

```typescript
// 🐛 錯誤版本
const subtotal = items.value.reduce(
  (sum, item) => sum + item.product.price,  // ← 忘記乘以 quantity
  0
)

// ✅ 修正後
const subtotal = items.value.reduce(
  (sum, item) => sum + item.product.price * item.quantity,
  0
)
```

修復後提交：

```bash
git add src/store/cart.ts
git commit -m "fix(cart): correct subtotal calculation to multiply by quantity

Previously calcTotal only summed unit prices without considering quantity,
causing the checkout total to always reflect single-item pricing.

Fixes #87"
```

### Step 4：合併 hotfix 回 main

```bash
git checkout main
git merge --no-ff hotfix/calc-error -m "Merge hotfix/calc-error: fix cart subtotal calculation"
git push origin main

# 清理 hotfix 分支
git branch -d hotfix/calc-error
```

### Step 5：回到 feature 分支並同步修復

有兩種方式把 hotfix 的修復同步到 `feature/cart-logic`：

**方式 A：Rebase（推薦，讓 feature 分支基底保持最新）**
```bash
git checkout feature/cart-logic
git rebase main

# 若有衝突，解完後 git rebase --continue
```

**方式 B：Cherry-pick（僅把那一個 commit 摘過來）**
```bash
git checkout feature/cart-logic

# 先查看 hotfix commit 的 hash
git log main --oneline | head -5

# 把那個 commit 複製到目前分支
git cherry-pick <hotfix-commit-hash>
```

**什麼時候用 cherry-pick 而非 rebase？**
- 當你只想要特定的一個或幾個 commit，不想把整個 main 的新內容都帶進來
- 當 rebase 產生太多衝突，但那個 hotfix commit 本身很乾淨時

### Step 6：恢復暫存的工作進度

```bash
# 查看 stash 清單
git stash list

# 恢復最新的 stash
git stash pop

# 確認改動已回來
git status
git diff
```

**`pop` vs `apply` 的差異：**
- `git stash pop`：恢復後從 stash 清單中刪除
- `git stash apply`：恢復後保留在 stash 清單（可以多次 apply）

---

## 完成標準

執行以下指令，確認成果：

```bash
# 線圖應清楚顯示 hotfix 從 main 分出，再合回 main
git log --graph --oneline --all
```

預期輸出類似：
```
* a1b2c3d (HEAD -> feature/cart-logic) WIP 恢復後的繼續開發...
* e4f5g6h fix(cart): correct subtotal calculation (cherry-picked)
* ...
*   h7i8j9k (main) Merge hotfix/calc-error
|\
| * k0l1m2n fix(cart): correct subtotal calculation to multiply by quantity
|/
* n3o4p5q feat: initial cart store implementation
```

---

## 考核重點

| 能力點 | 觀察方式 |
|--------|----------|
| 正確使用 stash 暫存進行中的工作 | 切換前後 git status 都是乾淨的 |
| hotfix 從 main 分出（不是從 feature 分出） | git log 線圖結構正確 |
| Commit Message 符合規範 | type + scope + subject 格式完整 |
| 正確選擇 cherry-pick 或 rebase 同步修復 | 能解釋選擇的原因 |
| stash pop 後工作進度完整恢復 | 無改動遺失 |

---

## 常見錯誤

❌ **直接 commit 半成品再切換分支**
→ 產生一個「WIP」commit 在歷史裡，之後需要用 `git commit --amend` 或 `git reset` 清理

❌ **忘記從 main 建 hotfix，直接在 feature 上修**
→ hotfix 和新功能混在同一個分支，無法獨立部署

❌ **用 `git stash` 但後來忘記 pop**
→ 定期 `git stash list` 確認，清理不需要的 stash：`git stash drop stash@{n}`
