# Git 實戰情境練習題

> 這些練習模擬真實開發中你很可能遇到的「爛攤子」。每個情境都有明確的背景說明、操作任務與考核重點。
> 
> **事前準備**：確保你已完成 Module 01-04 的學習，並在本機完成 `git init` 初始化。

---

## 練習索引

| 編號 | 情境名稱 | 核心技能 | 難度 |
|------|----------|----------|------|
| 01 | [消失的修補程式 (The Lost Hotfix)](./scenario-01-lost-hotfix/README.md) | `git stash`, `cherry-pick` | ★★☆ |
| 02 | [混亂的 Commit 歷史 (Clean Up Your Mess)](./scenario-02-messy-commits/README.md) | `git rebase -i`, `squash` | ★★★ |
| 03 | [被拒絕的推送 (The Remote Conflict)](./scenario-03-remote-conflict/README.md) | `git pull --rebase`, 衝突解決 | ★★★ |
| 04 | [致命的誤刪 (The Reflog Savior)](./scenario-04-reflog-savior/README.md) | `git reflog`, 分支恢復 | ★★☆ |

---

## 評分標準

各情境評核時，SE 觀察以下維度：

### 1. 指令的精準度（40%）
- 使用了正確的指令，沒有繞路
- 沒有使用危險操作（例如不必要的 `--force`）

### 2. 對「為什麼」的理解（30%）
- 能口頭解釋每個指令做了什麼、為什麼這樣做
- 不是死記指令，是理解 Git 的內部模型

### 3. 意外狀況的應對（20%）
- 遇到衝突或錯誤時，先分析再動作
- 知道如何用 `--abort` 退回到安全狀態

### 4. 結果的整潔度（10%）
- 完成後的 `git log --graph` 是否清晰
- Commit Message 是否符合 Conventional Commits 格式

---

## 環境重置方式

每個情境練習完，若要重新開始：

```bash
# 方法一：回到特定 commit（適合單分支情境）
git reset --hard <clean-state-commit-hash>

# 方法二：刪除並重新建立分支（適合多分支情境）
git checkout main
git branch -D feature/cart-logic
git checkout -b feature/cart-logic
```

---

## 進階挑戰

完成四個情境後，嘗試以下挑戰：

1. 用 Git Alias 為常用指令建立縮寫：
   ```bash
   git config --global alias.st "status"
   git config --global alias.lg "log --graph --oneline --all --decorate"
   git config --global alias.pr "pull --rebase"
   ```

2. 撰寫一個 `.git/hooks/commit-msg` hook，拒絕不符合 Conventional Commits 格式的 Commit Message。

3. 探索 `git bisect` 的用法：在本專案中故意引入一個 Bug commit，然後用 `git bisect` 二分搜尋找到它。
