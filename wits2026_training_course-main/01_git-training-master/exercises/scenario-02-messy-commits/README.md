# 情境二：混亂的 Commit 歷史 (Clean Up Your Mess)

## 背景

你花了一週實作使用者認證邏輯，但開發過程中 commit 習慣很差，產生了 10 個這樣的提交：

```
e9f8g7h  update again
d6e5f4g  fix
c3d2e1f  test
b0c9d8e  wip
a7b6c5d  fix typo
9f8e7d6  another fix
8d7c6b5  update user store
7c6b5a4  add login
6b5a4f3  start auth
5a4f3e2  initial
```

這個功能即將 merge 進 `develop`，但 Tech Lead 看了歷史，退回說：「你需要在 PR 合併前清理這些 commit，用一個符合規範的 message 呈現你做了什麼。」

---

## 任務步驟

### 前置：建立練習環境

先在本地製造這 10 個混亂 commit（練習用）：

```bash
git checkout -b feature/user-authentication develop

# 快速製造 10 個無意義的 commit
echo "// start" >> src/store/user.ts && git add -A && git commit -m "start auth"
echo "// login" >> src/store/user.ts && git add -A && git commit -m "add login"
echo "// store" >> src/store/user.ts && git add -A && git commit -m "update user store"
echo "// fix1" >> src/store/user.ts && git add -A && git commit -m "another fix"
echo "// typo" >> src/store/user.ts && git add -A && git commit -m "fix typo"
echo "// wip" >> src/store/user.ts  && git add -A && git commit -m "wip"
echo "// test" >> src/store/user.ts && git add -A && git commit -m "test"
echo "// fix2" >> src/store/user.ts && git add -A && git commit -m "fix"
echo "// v2"   >> src/store/user.ts && git add -A && git commit -m "update again"
echo "// done" >> src/store/user.ts && git add -A && git commit -m "done"

# 確認線圖
git log --oneline -12
```

### Step 1：啟動 Interactive Rebase

```bash
# 對最新 10 個 commit 進行互動式整理
git rebase -i HEAD~10
```

Git 會打開編輯器，顯示類似：

```
pick 5a4f3e2 start auth
pick 6b5a4f3 add login
pick 7c6b5a4 update user store
pick 8d7c6b5 another fix
pick 9f8e7d6 fix typo
pick a7b6c5d wip
pick b0c9d8e test
pick c3d2e1f fix
pick d6e5f4g update again
pick e9f8g7h done
```

### Step 2：設定 squash/fixup

把第 2 到第 10 個 commit 的 `pick` 改成 `squash`（或縮寫 `s`）：

```
pick 5a4f3e2 start auth    ← 保留這個作為最終 commit 的基礎
squash 6b5a4f3 add login
squash 7c6b5a4 update user store
squash 8d7c6b5 another fix
squash 9f8e7d6 fix typo
squash a7b6c5d wip
squash b0c9d8e test
squash c3d2e1f fix
squash d6e5f4g update again
squash e9f8g7h done
```

儲存後（`:wq`），Git 會讓你編輯合併後的 commit message。

**`squash` vs `fixup` 的差異：**
- `squash`：把這個 commit 合進前一個，並讓你編輯合併後的 message（有機會審視）
- `fixup`：把 commit 合進前一個，**直接丟棄**這個 commit 的 message（適合確定沒用的 message）

### Step 3：撰寫符合規範的 Commit Message

編輯器會打開，刪除所有原始 message，改成：

```
feat(auth): implement user authentication with remember-me support

Implement complete authentication flow for the Vue 3 store application:
- Add Pinia user store with login/logout actions
- Validate email format and minimum password length (8 chars)
- Support remember-me option to persist token in localStorage
- Add restoreSession() to recover login state on page reload
- Expose isAuthenticated, isAdmin, displayName computed properties

Closes #98
```

儲存後退出編輯器。

### Step 4：驗證結果

```bash
# 查看整理後的 log
git log --oneline -3

# 查看這個 commit 的完整內容
git show HEAD

# 確認改動的檔案仍然正確
git diff develop HEAD -- src/store/user.ts
```

### Step 5：若需要修正再次整理

如果 message 還不滿意，修改最後一個 commit：

```bash
git commit --amend
```

---

## rebase -i 的其他常用操作

在互動式 rebase 的編輯器中，除了 `pick`、`squash`、`fixup`，還有：

| 指令 | 縮寫 | 用途 |
|------|------|------|
| `pick` | `p` | 保留此 commit |
| `reword` | `r` | 保留 commit 但編輯 message |
| `edit` | `e` | 停在此 commit，可以修改內容再 `--amend` |
| `squash` | `s` | 合進前一個 commit，編輯 message |
| `fixup` | `f` | 合進前一個 commit，丟棄 message |
| `drop` | `d` | 刪除此 commit（改動也丟棄） |

**重新排序 commit**：直接調整行的順序，Git 會按照新順序重新播放。

---

## 完成標準

```bash
# 整理後只剩一個 commit（或合理的少數幾個）
git log develop..HEAD --oneline
```

預期輸出：
```
a1b2c3d feat(auth): implement user authentication with remember-me support
```

---

## 考核重點

| 能力點 | 觀察方式 |
|--------|----------|
| 正確進入 interactive rebase | 指令與範圍正確（`HEAD~10`） |
| 能理解並操作 squash | 10個 commit 正確合成 1 個 |
| Commit Message 符合 Conventional Commits | type、scope、subject、body 齊全 |
| 能解釋 squash 與 fixup 的差異 | 口頭說明 |
| 理解 rebase 後歷史被改寫的意涵 | 知道此時不能直接 push（需要 `--force-with-lease`） |

---

## ⚠️ 重要提醒

整理過的 commit 在 push 時會被遠端拒絕（因為歷史不同步）。

如果這個分支只有你一個人在用，這樣做是安全的：

```bash
git push --force-with-lease origin feature/user-authentication
```

**不要對 main、develop 等共享分支做 interactive rebase。**
