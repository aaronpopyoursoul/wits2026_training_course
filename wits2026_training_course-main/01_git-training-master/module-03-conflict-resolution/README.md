# Module 03 — 衝突解決

> **對象**：0.5-1 年資歷學員
> **預計時間**：3 小時

---

## 開發痛點

`git pull` 一下，終端機噴出一堆 `<<<<<<< HEAD`，整個專案跑不起來。你一邊看著衝突標記，一邊在想「我到底要保留哪些程式碼？另一個人改的是什麼意思？」

更糟糕的是：有人慌了，直接刪掉「看起來像別人的」那段，結果把同事辛苦寫的邏輯整個覆蓋掉，事後才發現。

解決衝突不難，難的是**在壓力下冷靜推理**：這段衝突代表什麼意思？兩邊的意圖能共存嗎？

---

## 核心觀念

### Merge vs Rebase：選擇的本質

這兩者不是誰比較好，而是**適用情境不同**。

#### git merge

```
develop:  A─B─C
                \
feature:         D─E─F
```

執行 `git checkout develop && git merge feature` 後：

```
develop:  A─B─C─────G  (G 是合併 commit)
                \  /
feature:         D─E─F
```

**優點**：完整保留歷史；可以清楚看出功能合併的時間點
**缺點**：多人協作時，線圖會出現很多交叉，`git log` 變得難以閱讀

#### git rebase

```
develop:  A─B─C
feature:  A─B─D─E─F
```

執行 `git checkout feature && git rebase develop` 後：

```
develop:  A─B─C
feature:  A─B─C─D'─E'─F'  (D/E/F 被重新播放在 C 之後)
```

**優點**：線圖呈現線性，`git log` 清晰
**缺點**：改寫了歷史（D 變成 D'）；

### ⚠️ Rebase 的黃金規則

**永遠不要 rebase 已經 push 到遠端的共享分支。**

原因：Rebase 會重新計算 commit hash。如果其他人的本地分支是基於舊的 hash，等他們 pull 下來就會遇到無法追溯的分歧，製造出更多衝突。

安全使用 rebase 的場景：
- 在自己的 feature 分支上，把 develop 的最新改動同步進來（之後合回 develop 時更乾淨）
- `git pull --rebase`（等同 fetch + rebase，比 merge 更常用）
- `git rebase -i`（整理自己的 commit 歷史，push 前做）

---

## 技術解法

### 衝突標記解讀

```
<<<<<<< HEAD
  loginAttempts.value++            // ← 你的修改（目前分支）
=======
  if (loginAttempts.value >= 5) {  // ← 對方的修改（被合併進來的）
    lockAccount()
  }
>>>>>>> feature/login-attempt-limit
```

三個區域：
1. `<<<<<<< HEAD` 到 `=======`：你目前分支的版本
2. `=======` 到 `>>>>>>>`：被合入的分支版本
3. 你的工作：**理解雙方意圖**，決定最終版本

本模組的 `conflict-example/` 資料夾提供了真實的三版本衝突範例。

### 解衝突流程

```bash
# 步驟一：觸發合併（或 rebase）
git checkout develop
git merge feature/login-attempt-limit

# 步驟二：查看所有衝突檔案
git diff --name-only --diff-filter=U

# 步驟三：用 VS Code 開啟衝突編輯器
code src/store/user.ts
# VS Code 會在衝突位置顯示「接受目前變更／接受傳入變更／接受兩者」按鈕

# 步驟四：手動整合雙方邏輯後，標記為已解決
git add src/store/user.ts

# 步驟五：完成合併
git commit  # Git 會自動帶入合併訊息

# 若是 rebase 衝突
git rebase --continue  # 每個衝突 commit 解完後執行
# 或放棄整個 rebase
git rebase --abort
```

### 使用 git mergetool（三向比較）

```bash
# 設定 VS Code 為預設 merge tool
git config --global merge.tool vscode
git config --global mergetool.vscode.cmd 'code --wait $MERGED'

# 啟動三向比較編輯器
git mergetool
```

三向比較顯示：左（BASE 原始版本）、中（MERGED 你要編輯的）、右（REMOTE 對方版本）。比只看衝突標記更清楚。

---

## conflict-example/ 實際操作

`conflict-example/` 資料夾包含三個版本的 `user.ts`（Pinia store 的 `login` 動作）：

| 檔案 | 代表 |
|------|------|
| `user.ts.base` | 衝突前雙方都認同的版本（共同祖先） |
| `user.ts.ours` | 你的開發版本（加入 `rememberMe` 功能） |
| `user.ts.theirs` | 同事的開發版本（加入登入錯誤次數限制） |

**操作練習：**

```bash
cd module-03-conflict-resolution/conflict-example

# 用 VS Code 並排比較三個版本，理解雙方的改動意圖
code .

# 挑戰：在不丟失任何一方邏輯的前提下，手動合併出一個同時包含
#「rememberMe 功能」與「登入錯誤限制」的最終版本 user.ts
```

**預期的正確合併結果**：
- `login()` 函式接受 `rememberMe: boolean` 參數
- `loginAttempts` ref 追蹤錯誤次數，達 5 次後設定 `isLocked` 狀態
- 登入成功時，若 `rememberMe` 為 true 則存入 localStorage

---

## 工程規範

### 何時用 Merge，何時用 Rebase

| 情境 | 建議操作 | 理由 |
|------|----------|------|
| feature 合回 develop/main | `merge --no-ff` | 保留功能邊界，可清楚看出哪批 commit 屬於哪個功能 |
| 同步 develop 最新進度到自己的 feature | `rebase` | 讓 feature 分支基底保持在最新，減少最後合併時的衝突量 |
| `git pull` | `git pull --rebase` | 避免產生無意義的 merge commit（例如 "Merge branch 'main' of github.com/..."） |
| `hotfix` 合回 main 與 develop | `merge --no-ff` | 緊急修復需要明確的合併記錄 |

**設定 pull 預設行為：**

```bash
git config --global pull.rebase true
```

之後 `git pull` 就等同 `git pull --rebase`。

### 解衝突的工程態度

1. **先理解再動手**：閱讀衝突兩側的程式碼，確認雙方意圖，再決定如何整合
2. **不能只選一邊了事**：單純點「接受傳入變更」覆蓋對方，是最差的解法
3. **解完要跑一次**：確保解衝突後的程式碼邏輯正確，不要 commit 一個根本執行不了的版本
4. **commit message 說明解了什麼**：`fix: merge login attempt limit with remember-me feature`

---

## 本模組自我檢驗

- [ ] 能解釋 merge 與 rebase 在線圖上的差異
- [ ] 知道什麼情況下不應該使用 rebase
- [ ] 使用 VS Code 的三向衝突編輯器完成  `conflict-example/` 的合併練習
- [ ] 能說明解衝突後為何一定要重新驗證程式邏輯
