# Module 04 — 遠端協作

> **對象**：0.5-1 年資歷學員
> **預計時間**：2.5 小時

---

## 開發痛點

`git push` 失敗，終端機顯示：

```
! [rejected]  main -> main (fetch first)
error: failed to push some refs to 'origin'
hint: Updates were rejected because the remote contains work that you do not have locally.
```

看到這個，很多人的第一反應是 `git push --force`。這個動作在共享分支上會直接覆蓋同事剛 push 的 commit，是遠端協作中最危險的操作之一。

另一個常見問題是：Fork 了一個開源專案後，過了幾個月發現自己的 Fork 落後上游 200 個 commit，不知道怎麼同步。

---

## 核心觀念

### Origin vs Upstream

```
你的電腦 (Local)
    │── origin   → 你自己 GitHub 上的 Fork
    │── upstream → 原始專案的遠端 repo

上游專案 (Upstream Remote)
    └── 其他人貢獻的變更都在這裡
```

**一般公司內部開發流程：**

```
你的電腦 ──push──→ origin (GitHub/GitLab)
                        │
                        └──Pull Request──→ main (保護分支)
                                              │
                                        Tech Lead review
```

### fetch / pull 的差異

```bash
git fetch origin      # 下載遠端最新狀態，但不改動本地工作目錄
git merge origin/main # 再手動合併

# 等同以上兩步合一（預設行為是 merge）
git pull origin main
```

**為什麼建議習慣用 `git fetch` + 手動決策？**
因為 `git pull` 會立刻合併，如果你有未完成的本地改動，可能製造不必要的衝突。養成先 fetch、再查看差異、再決定合併方式的習慣。

---

## 技術解法

### 情境：Push 被拒絕的正確處理方式

```bash
# 錯誤做法（絕對不要在共享分支上做）
git push --force

# 正確做法
# 1. 先 fetch 下來看看對方改了什麼
git fetch origin

# 2. 查看差異
git log HEAD..origin/main --oneline

# 3. 用 rebase 把自己的 commit 重新寫在對方改動之後
git pull --rebase origin main

# 4. 如果沒有衝突，直接推
git push origin main

# 5. 如果有衝突，解完後繼續
git rebase --continue
git push origin main
```

### 萬不得已需要 force push 時

只有一種場景可接受 force push：**自己獨立使用的 feature 分支**（非共享），且你已確認沒有其他人在用這個分支。

```bash
# 比 --force 安全的版本
# 如果遠端分支被別人改過（hash 不符），push 就會失敗，保護你不會意外覆蓋
git push --force-with-lease origin feature/my-feature
```

**`--force-with-lease` 的原理：** 它會在 push 之前確認遠端分支的 hash 是否和你 fetch 到的一致。如果不一致，代表有人在你不知道的情況下 push 了新內容，操作就會被中止。

### 設定 Upstream（Fork 工作流）

```bash
# 查看目前遠端設定
git remote -v

# 加入上游原始 repo
git remote add upstream https://github.com/original-owner/repo.git

# 同步上游的最新改動
git fetch upstream

# 把上游的 main 合進自己的 main
git checkout main
git merge upstream/main

# 或者用 rebase（讓本地 commit 接在上游最新 commit 之後）
git rebase upstream/main
```

### 在本地模擬完整的遠端協作

詳細操作見 `.vscode/instructions.md`。以下是快速摘要：

```bash
# 建立一個 bare repo 作為「假的遠端」
git init --bare ~/training-remote/git-training-origin.git

# 在本專案加入這個遠端
git remote add origin ~/training-remote/git-training-origin.git
git push -u origin main

# 模擬另一個開發者 clone 並做改動
git clone ~/training-remote/git-training-origin.git ~/training-developer-b
cd ~/training-developer-b
# 修改 src/store/user.ts 並 push

# 回到原本的工作目錄，嘗試 push 觸發衝突
cd /path/to/git-training-master
# 修改同一行並 commit
git push # 觸發 rejected 錯誤，練習正確的處理流程
```

---

## 工程規範

### 遠端操作標準流程

每次開始工作前，先同步遠端狀態：

```bash
git fetch origin
git status  # 查看本地與遠端的差距
```

**每日工作開始的基本儀式：**

```bash
git switch develop          # 切回整合分支
git pull --rebase origin develop  # 同步最新
git switch feature/my-feature     # 切回工作分支
git rebase develop          # 把最新 develop 同步到 feature
```

### Git 遠端操作禁止事項

| 操作 | 狀況 | 原因 |
|------|------|------|
| `git push --force` | 共享分支 | 直接覆蓋他人 commit，無法恢復 |
| `git push --force` | Protected Branch | 通常 server 端會直接拒絕 |
| `git rebase origin/main` | main 是共享分支 | 改寫共享歷史，讓所有人的本地分支分岐 |
| `git push` 未測試的程式碼 | main/develop | 影響所有人的建置與部署 |

### 正確的 force push 使用情境

```bash
# 情境：在 feature 分支做了 git rebase -i 整理 commit 後，需要更新遠端
git push --force-with-lease origin feature/PROJ-142-checkout-form
```

注意事項：force push 後，同一個分支上的其他協作者需要執行：
```bash
git fetch origin
git reset --hard origin/feature/PROJ-142-checkout-form
```

---

## 本模組自我檢驗

- [ ] 能解釋 `origin` 與 `upstream` 的差異
- [ ] `git push` 被拒絕時，能用 `git pull --rebase` 正確處理
- [ ] 知道 `--force-with-lease` 比 `--force` 安全在哪裡
- [ ] 能完成 `.vscode/instructions.md` 中的本地雙 clone 模擬練習
- [ ] 能說明「每日同步習慣」的必要性
