# Module 02 — 分支策略

> **對象**：0.5-1 年資歷學員
> **預計時間**：2.5 小時

---

## 開發痛點

三個人同時在同一個 `main` 分支上開發，一個人推了未完成的程式碼，整個 CI 就炸了，影響另外兩個人的工作。或是反過來：每個人開了一堆分支，沒人知道哪個分支才是當前的工作主線。

**沒有分支策略的後果：**
- 無法確定「線上目前跑的是哪個版本的程式碼」
- 緊急 Hotfix 需要混在新功能開發中處理
- Release 時不知道哪些功能可以上、哪些還沒完成

---

## 兩種主流策略

### Git Flow

適合：有明確版本號的產品（APP、套件庫），Release 週期較長（週/雙週）。

```
main          ●─────────────────────────────●──── (v2.0.0)
              │                             │
hotfix        │         ●──●                │
              │         │  │                │
release/2.0   │         │  └────────────────●
              │         │                   │
develop       ●─●─●─●───●───●─●─●──────────●
                │ │ │       │ │ │
feature/xxx    ─● │ │       ─● │ │
feature/yyy      ─● │         ─● │
feature/zzz        ─●           ─●
```

**五種分支角色：**

| 分支 | 壽命 | 用途 |
|------|------|------|
| `main` | 永久 | 代表線上版本，每個 commit 都是可部署狀態 |
| `develop` | 永久 | 整合分支，功能開發的目標基底 |
| `feature/*` | 短暫 | 單一功能開發，從 develop 出發，合回 develop |
| `release/*` | 短暫 | 預發佈分支，只修 bug 不加新功能 |
| `hotfix/*` | 短暫 | 線上緊急修復，從 main 出發，合回 main 與 develop |

### Trunk-Based Development (TBD)

適合：有 CI/CD 自動化覆蓋、Feature Flag 機制的團隊。Release 頻率高（每日/每小時）。

```
main  ●─●─●─●─●─●─●─●─●─● (持續部署)
      │ │ │ │ │ │ │ │ │ │
      feature 分支存活 < 2 天，快速合回
```

**核心規則：**
- 所有開發者每天至少把改動 merge 或 rebase 回 `main` 一次
- 未完成的功能用 Feature Flag 控制，不阻塞 Release
- 分支壽命不超過 2 天

**如何選擇？** 你的團隊若沒有完整的 CI 測試覆蓋，不要強行採用 TBD。先用 Git Flow 建立規範，再隨著自動化成熟度升級。

---

## 技術解法

### 分支操作指令

```bash
# 建立並切換到新分支（等同 git branch + git checkout）
git checkout -b feature/PROJ-123-user-profile

# 更現代的寫法（Git 2.23+）
git switch -c feature/PROJ-123-user-profile

# 列出所有本地分支（含最後 commit 訊息）
git branch -v

# 列出所有遠端分支
git branch -r

# 從遠端拉取並追蹤分支
git checkout -t origin/develop

# 刪除已合併的分支（安全刪除）
git branch -d feature/PROJ-123-user-profile

# 強制刪除（未合併，要確認後才用）
git branch -D feature/unfinished-experiment

# 重新命名分支
git branch -m old-name new-name

# 查看哪些分支已合併入 develop
git branch --merged develop
```

### 在本專案模擬 Git Flow

```bash
# 假設已在 git-training-master 完成初始化
git init
git add .
git commit -m "chore: initial project setup"

# 建立 develop 分支
git checkout -b develop

# 開始開發購物車功能
git checkout -b feature/PROJ-001-cart-logic develop

# 修改 src/store/cart.ts 並提交
git add src/store/cart.ts
git commit -m "feat(cart): add addItem and removeItem actions"

# 功能完成，合回 develop（使用 --no-ff 保留合併節點）
git checkout develop
git merge --no-ff feature/PROJ-001-cart-logic -m "Merge branch 'feature/PROJ-001-cart-logic'"
git branch -d feature/PROJ-001-cart-logic
```

**為什麼用 `--no-ff`？**
Fast-forward merge 會讓這個功能的 commit 直接接在 develop 上，看不出來這批 commit 屬於同一個功能。`--no-ff` 強制建立一個合併節點，讓你能用 `git log --graph` 清楚看到功能分支的邊界。

---

## 工程規範

### 分支命名規則

```
<type>/<ticket-id>-<short-description>
```

| 類型 | 格式 | 範例 |
|------|------|------|
| 功能開發 | `feature/PROJ-xxx-short-desc` | `feature/PROJ-142-checkout-form` |
| Bug 修復 | `fix/PROJ-xxx-short-desc` | `fix/PROJ-200-cart-total-wrong` |
| 緊急修復 | `hotfix/short-desc` | `hotfix/checkout-calc-error` |
| 預發佈 | `release/vX.Y.Z` | `release/v2.1.0` |
| 技術改善 | `refactor/short-desc` | `refactor/cart-store-composition` |

**強制規則（會被 CI 擋回）：**
- 分支名稱只能使用小寫英文、數字、`/`、`-`
- 不允許空格、底線、大寫字母
- `main`、`develop` 分支設定為 **Protected Branch**，需透過 Pull Request 合併，不得直接 push

### Pull Request 規範

PR 描述必須包含：

```markdown
## 變更說明
簡單說明這個 PR 做了什麼

## 關聯 Issue
Closes #142

## 測試方式
1. 進入購物車頁面
2. 新增 3 件商品
3. 確認總金額計算正確

## Checklist
- [ ] 通過本地 type-check (`yarn type-check`)
- [ ] 沒有引入新的 console.log
- [ ] 有更新相關文件（如有需要）
```

---

## 本模組自我檢驗

- [ ] 能說明 Git Flow 的五種分支角色與合併方向
- [ ] 能說明 Trunk-Based Development 適合的場景
- [ ] 用標準命名建立 feature 分支並完成 merge --no-ff
- [ ] 理解 Protected Branch 的作用
