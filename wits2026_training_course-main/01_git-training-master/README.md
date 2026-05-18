# Git 實戰教材 — git-training-master

> **定位**：培訓 0.5-1 年資歷學員的 Git 實戰教材
> **專案類型**：Vue 3 + TypeScript 模擬電商專案（不需執行，作為真實開發情境的載體）

---

## 學習路徑

建議按照以下順序進行，每個模組都有明確的前後依賴。

```
Module 01          Module 02            Module 03              Module 04
核心觀念    →   分支策略       →    衝突解決        →    遠端協作
(2h)              (2.5h)               (3h)                    (2.5h)
                                         ↓
                                     exercises/
                                  四個實戰情境練習
                                        (4-6h)
```

**總學習時間估計**：10-14 小時（含情境練習反覆操作）

---

## 資料夾結構

```
git-training-master/
│
├── .vscode/
│   └── instructions.md           ← 本地模擬遠端協作的完整操作手冊
│
├── src/                          ← Vue 3 + TypeScript 模擬電商程式碼
│   ├── types/index.ts            ← 全域型別定義（User, CartItem, Order...）
│   ├── store/
│   │   ├── user.ts               ← 使用者認證 store（情境三的衝突焦點）
│   │   └── cart.ts               ← 購物車 store（情境一的 Bug 修復目標）
│   ├── composables/
│   │   ├── useAuth.ts            ← 認證邏輯封裝
│   │   └── useCart.ts            ← 購物車邏輯封裝
│   ├── components/
│   │   ├── CartItem.vue          ← 購物車項目元件
│   │   ├── CheckoutForm.vue      ← 結帳表單元件
│   │   └── UserProfile.vue       ← 使用者資訊元件
│   ├── views/
│   │   ├── HomeView.vue          ← 首頁
│   │   ├── CartView.vue          ← 購物車頁面
│   │   └── LoginView.vue         ← 登入頁面
│   ├── App.vue                   ← 應用根元件（含導航列）
│   └── main.ts                   ← 應用入口（Vue + Pinia + Router 設定）
│
├── module-01-core-concepts/
│   └── README.md                 ← Staging Area、Commit 原子性、.gitignore、Conventional Commits
│
├── module-02-branching-strategies/
│   └── README.md                 ← Git Flow vs Trunk-Based、分支命名規則、PR 規範
│
├── module-03-conflict-resolution/
│   ├── README.md                 ← Merge vs Rebase 權衡、衝突解決流程
│   └── conflict-example/
│       ├── user.ts.base          ← 共同祖先版本（三向合併的 BASE）
│       ├── user.ts.ours          ← 你的版本（加入 rememberMe 功能）
│       └── user.ts.theirs        ← 同事的版本（加入登入錯誤次數限制）
│
├── module-04-remote-collaboration/
│   └── README.md                 ← Origin/Upstream、push 被拒絕的正確處理、--force-with-lease
│
├── exercises/
│   ├── README.md                 ← 練習索引與評分標準
│   ├── scenario-01-lost-hotfix/  ← git stash + cherry-pick 熱修復流程
│   ├── scenario-02-messy-commits/← git rebase -i squash 整理 commit 歷史
│   ├── scenario-03-remote-conflict/ ← git pull --rebase 解遠端衝突
│   └── scenario-04-reflog-savior/   ← git reflog 救回誤刪分支
│
├── .gitignore
├── package.json                  ← Vue 3.4 + Vite 5 + Pinia + TypeScript 5
├── tsconfig.json
└── vite.config.ts
```

---

## 前置要求

| 項目 | 版本要求 | 確認指令 |
|------|----------|----------|
| Git | 2.35+ | `git --version` |
| Node.js | 18+ | `node --version` |
| VS Code | 最新穩定版 | — |

### 建議安裝的 VS Code 擴充

- **GitLens** (`eamodio.gitlens`)：強化 Git 歷史顯示，內嵌 blame 資訊
- **Git Graph** (`mhutchie.git-graph`)：視覺化分支線圖
- **EditorConfig** (`editoritorconfig.editorconfig`)：統一縮排設定

---

## 教材設計原則

### 為什麼用 Vue 3 + TypeScript 專案？

Git 教材通常用空的 `hello.txt` 做範例，但實際工作中你面對的是真實的程式碼衝突。這個教材刻意使用有業務邏輯的 TypeScript 程式碼，讓學員練習的衝突情境和工作中會遇到的狀況一致。

### 每個模組的知識結構

每個 README 都遵循相同的三段結構，這不是格式要求，而是建立正確的學習順序：

1. **開發痛點**：確保學員先感受到問題，再接受解法。知道「為什麼」的指令才能靈活應用，不只是死記步驟。
2. **技術解法**：具體的指令與操作流程，附帶關鍵的「為什麼這樣做」說明。
3. **工程規範**：資深工程師的標準，告訴學員「可以用」和「應該這樣用」之間的差距。

---

## 給 SE 的教學建議

### 如何使用這份教材

1. **不要直接給學員所有內容**：讓他們先嘗試自己解決情境練習，卡住了再引導去看對應的 Module 說明

2. **情境練習的重點不在結果，在過程**：觀察學員在遇到錯誤時是否會先閱讀錯誤訊息，還是隨機嘗試指令。這反映的是問題排查的思維方式。

3. **特別關注這些理解點**：
   - 情境一：學員是否理解 `stash` 的本質（臨時 commit），而不是把它當作「儲存」按鈕
   - 情境二：學員是否理解 rebase 改寫了歷史（新 hash），以及這代表什麼後果
   - 情境三：學員解衝突時是否真的理解雙方改動的業務意義，而不是隨意選邊
   - 情境四：學員是否從「Git 幾乎不丟資料」這個角度建立信心，而不是恐懼誤操作

4. **reflog 與 `--abort` 是安全網，要明確說明**：很多學員害怕 Git 操作是因為擔心「搞壞了回不去」。讓他們知道幾乎所有操作都可以用 reflog 或 `--abort` 還原，才能讓他們放膽練習。

---

## 快速開始（10 分鐘熱身）

```bash
# 1. 初始化這個專案為 Git Repo
cd /path/to/git-training-master
git init

# 2. 按照 Module 01 的說明設定 gitignore 並做第一個 commit
git add .gitignore
git commit -m "chore: add gitignore for Vue 3 TypeScript project"

# 3. 按順序提交 src/ 的程式碼，練習 Conventional Commits 格式
git add src/types/
git commit -m "feat(types): define core domain types (User, CartItem, Order)"

git add src/store/
git commit -m "feat(store): add user auth and cart Pinia stores"

# 4. 查看你的 commit 歷史
git log --oneline

# 5. 建立 develop 分支，開始 Module 02
git checkout -b develop
```

---

## 版本資訊

| 項目 | 版本 |
|------|------|
| Vue | 3.4.x |
| Pinia | 2.1.x |
| Vite | 5.x |
| TypeScript | 5.4.x |
| 教材版本 | 1.0.0 |
| 最後更新 | 2026-05 |
