# 情境三：被拒絕的推送 (The Remote Conflict)

## 背景

你在本地修改了 `src/store/user.ts` 的 `login()` 函式，加入了 `rememberMe` 功能，並且已經 commit。

就在你準備 push 之前，你的同事 Alex 也改了同一個檔案的同一行（加入了登入錯誤次數限制），並且已經先 push 到遠端了。

現在你執行 `git push`，得到：

```
! [rejected]  main -> main (fetch first)
error: failed to push some refs to 'origin'
hint: Updates were rejected because the remote contains work that you do not have locally.
```

---

## 前置：設定本地「假遠端」環境

> 詳細步驟見 `.vscode/instructions.md`。以下是快速版本。

```bash
# 建立一個 bare repo 模擬遠端
mkdir -p ~/training-remote
git init --bare ~/training-remote/git-training-origin.git

# 在本專案加入這個遠端
cd /path/to/git-training-master
git remote add origin ~/training-remote/git-training-origin.git
git push -u origin main

# 模擬「Developer B」(同事 Alex)
git clone ~/training-remote/git-training-origin.git ~/training-developer-b
cd ~/training-developer-b

# Alex 修改 src/store/user.ts（加入錯誤次數限制）
# 直接複製 module-03 的 .theirs 版本當作 Alex 的改動
cp /path/to/git-training-master/module-03-conflict-resolution/conflict-example/user.ts.theirs \
   src/store/user.ts

git add src/store/user.ts
git commit -m "feat(auth): add login attempt limit with 30-min lockout"
git push origin main   # Alex 先 push

# 回到你的工作目錄
cd /path/to/git-training-master
```

### 你的本地改動（模擬）

```bash
# 確認你在自己的工作目錄
cd /path/to/git-training-master

# 複製 OURS 版本當作你的本地改動
cp module-03-conflict-resolution/conflict-example/user.ts.ours src/store/user.ts

git add src/store/user.ts
git commit -m "feat(auth): add remember-me option to persist login token"

# 嘗試 push，觸發拒絕錯誤
git push origin main
```

---

## 任務步驟

### Step 1：理解錯誤訊息並分析現況

```bash
# 查看本地和遠端的差距
git fetch origin
git log HEAD..origin/main --oneline      # 遠端有哪些你沒有的 commit
git log origin/main..HEAD --oneline      # 你有哪些遠端沒有的 commit
```

此時你會看到：
- 遠端比你多：`feat(auth): add login attempt limit with 30-min lockout`
- 你比遠端多：`feat(auth): add remember-me option to persist login token`

這兩個 commit 都修改了 `src/store/user.ts` 的 `login()` 函式，製造了衝突。

### Step 2：用 git pull --rebase 解決

```bash
# 把 Alex 的 commit 拉下來，並把你的 commit 重新播放在後面
git pull --rebase origin main
```

Git 會嘗試自動合併，但因為兩人都改了同一個函式，會停下來說：

```
CONFLICT (content): Merge conflict in src/store/user.ts
error: could not apply a1b2c3d... feat(auth): add remember-me option...
hint: Resolve all conflicts manually...
hint: use "git rebase --continue" to continue...
```

### Step 3：在 VS Code 解衝突

```bash
# 用 VS Code 開啟衝突檔案
code src/store/user.ts
```

VS Code 會在衝突位置顯示以下 UI：
- **接受目前變更 (Accept Current Change)**：保留 Alex 的版本（rebase 基底）
- **接受傳入變更 (Accept Incoming Change)**：保留你自己的改動
- **接受兩者 (Accept Both Changes)**：兩段都留（通常需要再手動整理）
- **比較變更 (Compare Changes)**：並排檢視差異

**本情境的正確解法**：不能只選其中一邊，必須整合雙方邏輯：
- 保留 Alex 的 `loginAttempts`、`isLocked`、鎖定檢查邏輯
- 保留你的 `rememberMe` 參數與 localStorage 邏輯
- 最終的 `login()` 要同時支援兩個功能

**衝突區塊範例：**

```typescript
<<<<<<< HEAD  (Alex 的版本，已在遠端)
async function login(email: string, password: string): Promise<boolean> {
  if (isLocked.value) {
    loginError.value = `帳號已鎖定，請稍後再試`
    return false
  }
=======
async function login(
  email: string,
  password: string,
  rememberMe: boolean = false
): Promise<boolean> {
>>>>>>> feat(auth): add remember-me option  (你的版本)
```

手動整合後應該是：

```typescript
async function login(
  email: string,
  password: string,
  rememberMe: boolean = false  // 保留你的 rememberMe
): Promise<boolean> {
  // 保留 Alex 的鎖定檢查
  if (isLocked.value) {
    if (lockUntil.value && Date.now() < lockUntil.value) {
      loginError.value = `帳號已鎖定，請 ${remainingLockSeconds.value} 秒後再試`
      return false
    }
    // ...
  }
  // ... 後續加入 rememberMe 邏輯
```

### Step 4：完成 rebase

```bash
# 解完衝突後標記為已完成
git add src/store/user.ts

# 繼續 rebase 流程
git rebase --continue

# 若有多個 commit 有衝突，重複 Step 3-4 直到完成
```

### Step 5：驗證並 Push

```bash
# 確認 log 線性且清晰
git log --oneline -5

# 確認程式碼邏輯正確（reviewable）
git diff HEAD~1 HEAD

# Push（rebase 後 hash 改變，遠端仍照常 push 即可）
git push origin main
```

---

## 完成標準

```bash
git log --oneline origin/main
```

預期輸出（線性歷史，沒有多餘的 merge commit）：
```
b3c4d5e feat(auth): add remember-me option to persist login token
a1b2c3d feat(auth): add login attempt limit with 30-min lockout
9f0e1d2 chore: initial project setup
```

---

## 考核重點

| 能力點 | 觀察方式 |
|--------|----------|
| 不直接 `--force` 覆蓋 | 觀察第一個反應 |
| 能用 `git fetch` 分析現況再行動 | 執行 `git log` 前先 fetch |
| 正確使用 `git pull --rebase` | 不產生多餘 merge commit |
| 衝突解法整合雙方邏輯 | 最終 `login()` 同時有兩個功能 |
| `git rebase --continue` 流程熟練 | 沒有遺漏任何 conflict file |

---

## 常見錯誤

❌ **直接 `git push --force`**
→ 覆蓋 Alex 的所有改動，製造生產問題

❌ **只接受自己的版本（Accept Incoming）**
→ Alex 的登入安全改動全部遺失

❌ **忘記 `git add` 就執行 `git rebase --continue`**
→ Git 會提示錯誤：`No changes - did you forget to use git add?`
