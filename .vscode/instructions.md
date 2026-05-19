# 本地模擬遠端協作環境 — 操作指南

> **用途**：在不依賴 GitHub/GitLab 的情況下，透過本地資料夾模擬「遠端倉庫」的完整工作流程，讓學員練習 push/pull/衝突解決等協作指令。

---

## 核心概念

### Bare Repository

一般的 Git Repo（Non-bare）有工作目錄，你可以在裡面直接編輯檔案。

**Bare Repo** 只有 Git 的物件資料庫（`.git/` 裡的東西），沒有工作目錄——就像 GitHub 遠端一樣，它的職責只是儲存和接收 push/pull，不直接編輯。

```bash
# 非 bare repo（一般你開發用的）
.git/
src/
package.json
...

# bare repo（模擬遠端用）
HEAD
config
objects/
refs/
...  (沒有 src/ 等工作目錄)
```

---

## Step 1：建立本地 Bare Repo（模擬 Origin）

```bash
# 建立資料夾並初始化 bare repo
mkdir -p ~/training-remote
git init --bare ~/training-remote/git-training-origin.git

# 確認它是 bare repo
cat ~/training-remote/git-training-origin.git/config
# 應該看到 bare = true
```

---

## Step 2：把本專案連接到這個「假遠端」

```bash
cd /path/to/git-training-master

# 初始化（如果尚未 init）
git init

# 加入遠端
git remote add origin ~/training-remote/git-training-origin.git

# 確認遠端設定
git remote -v
# 輸出應該是：
# origin  ~/training-remote/git-training-origin.git (fetch)
# origin  ~/training-remote/git-training-origin.git (push)

# 第一次推送並設定 upstream tracking
git add .
git commit -m "chore: initial project setup"
git push -u origin main

# 確認 push 成功
git log --oneline origin/main
```

---

## Step 3：模擬「Developer B」（第二位開發者）

在同一台機器上，用另一個視窗模擬另一位開發者 clone 同個遠端：

```bash
# 模擬 Developer B clone 專案
git clone ~/training-remote/git-training-origin.git ~/training-developer-b

# 切換到 Developer B 的工作目錄
cd ~/training-developer-b

# 確認是完整的 clone
git log --oneline
git remote -v
```

**在 VS Code 開啟兩個視窗**：
- 視窗一：`/path/to/git-training-master`（你）
- 視窗二：`~/training-developer-b`（Developer B）

這樣可以並排操作，直觀模擬兩位工程師同時在工作的情境。

---

## Step 4：模擬推送衝突

### Developer B 先 push（視窗二）

```bash
cd ~/training-developer-b

# Developer B 修改 src/store/user.ts
# 例如加入登入錯誤次數限制（參考 module-03/conflict-example/user.ts.theirs）
code src/store/user.ts

git add src/store/user.ts
git commit -m "feat(auth): add login attempt limit"
git push origin main   # Developer B 先 push，成功
```

### 你也做了修改並嘗試 push（視窗一）

```bash
cd /path/to/git-training-master

# 你同時修改了 src/store/user.ts
# 例如加入 rememberMe 功能（參考 module-03/conflict-example/user.ts.ours）
code src/store/user.ts

git add src/store/user.ts
git commit -m "feat(auth): add remember-me option"

# 嘗試 push → 觸發 rejected 錯誤
git push origin main
```

→ 此時就進入情境三的練習流程：`git pull --rebase` 解衝突。

---

## Step 5：模擬 Upstream 關係（Fork 工作流）

這個更進階，模擬「Fork 開源專案後同步上游」的場景：

```bash
# 把 bare repo 當作上游（upstream）
git remote add upstream ~/training-remote/git-training-origin.git

# 確認遠端設定
git remote -v
# origin    ~/training-remote/git-training-origin.git (fetch/push)  ← 你的 Fork
# upstream  ~/training-remote/git-training-origin.git (fetch/push)  ← 上游

# 同步上游最新改動
git fetch upstream
git rebase upstream/main
```

（在真實 Fork 工作流中，origin 和 upstream 指向不同的 URL，這裡因為是模擬環境所以相同。）

---

## 常見情境的模擬指令速查

### 情境一：Stash → Hotfix → Cherry-pick（情境練習 01）

```bash
# 在視窗一製造半成品改動
echo "// WIP code" >> src/composables/useCart.ts

# 不 commit，直接 stash
git stash push -m "WIP: coupon discount logic"

# 切換到 hotfix 分支並修復 src/store/cart.ts
git checkout -b hotfix/calc-error main
# 修改 calcTotal 的 Bug 後 commit
git add src/store/cart.ts
git commit -m "fix(cart): multiply by quantity in subtotal calculation"

# 合回 main
git checkout main
git merge --no-ff hotfix/calc-error
git push origin main
```

### 情境二：Interactive Rebase（情境練習 02）

```bash
# 快速製造 5 個無意義 commit
for msg in "wip" "fix" "update" "test" "done"; do
  echo "// $msg" >> src/store/user.ts
  git add -A
  git commit -m "$msg"
done

# 進行 interactive rebase
git rebase -i HEAD~5
```

### 情境四：Reflog 救回（情境練習 04）

```bash
# 建立分支並加入幾個 commit
git checkout -b feature/important-api
echo "// api service" > src/services/api.ts
git add -A && git commit -m "feat(api): add initial API service"

# 切回 main 並強制刪除
git checkout main
git branch -D feature/important-api

# 用 reflog 找回
git reflog | head -5
git checkout -b feature/important-api <找到的hash>
```

---

## 清理環境

練習結束後，清理模擬環境：

```bash
# 移除假遠端
git remote remove origin

# 刪除 Developer B 的資料夾
rm -rf ~/training-developer-b

# 刪除 bare repo（可選）
rm -rf ~/training-remote
```

---

## 給 SE（資深工程師）的引導建議

### 常見卡關點與引導方式

| 學員卡在 | 引導方向 |
|----------|----------|
| 不知道 push 被拒絕的原因 | 請他先讀完錯誤訊息，問他「hint 行說了什麼？」 |
| 解衝突時只想選一邊 | 要求他說明「對方的改動是什麼目的？能丟掉嗎？」 |
| rebase -i 不敢下手 | 先在測試 repo 操作一次，確認可以用 `git rebase --abort` 反悔 |
| reflog 找不到正確的 hash | 引導他看 reflog 的 action 欄位（`commit`、`checkout`） |
| stash 後忘記在哪裡 | 執行 `git stash list`，說明 stash 是先進後出的堆疊 |

### 評核時的觀察重點

評核不是考記憶力，而是觀察**遇到問題時的思考過程**：

1. 遇到錯誤，第一反應是查閱 `git status` / `git log` 還是直接瞎試指令？
2. 解衝突前有沒有先理解雙方改動的業務意義？
3. 知道什麼時候可以回頭（`--abort`）？
4. 完成後有沒有主動驗證（跑 `git log --graph` 確認歷史結構）？
