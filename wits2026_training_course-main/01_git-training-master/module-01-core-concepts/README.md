# Module 01 — Git 核心觀念

---

## 開發痛點

你剛完成了一個功能，興沖沖地用了 `git add .` 然後 `git commit`，結果 Push 上去後同事跑來說：「你怎麼把 `.env` 和 `node_modules` 都推上來了？」

這是幾乎每個新手都會踩的坑。根本原因是：**不理解 Git 的三個工作區域，以及 Commit 的原子性原則**。

另一個常見問題：Commit Message 一律是 `update`、`fix bug`，三個月後誰也看不懂當時在改什麼。

---

## 核心觀念

### 1. Git 三個工作區域

```
工作目錄 (Working Directory)
    │  git add <file>
    ▼
暫存區 (Staging Area / Index)
    │  git commit
    ▼
本地倉庫 (Local Repository)
    │  git push
    ▼
遠端倉庫 (Remote Repository)
```

**為什麼要有 Staging Area？**

它讓你能夠「精準控制」這次 Commit 要包含哪些改動。一個典型場景：你同時修了一個 Bug 並加了一個新功能，你可以用 Staging Area 把它們拆成兩個 Commit，讓歷史更清晰。

```bash
# 查看目前三個區域的狀態
git status

# 以互動式方式選擇要 stage 的程式碼片段（hunk）
# 這是掌控 Commit 粒度最有效的工具
git add -p
```

### 2. Commit 的原子性

**一個 Commit 只做一件事。** 這不是美觀問題，是工程紀律：

- 當你需要 `git revert` 還原某個功能時，一個 Commit 做多件事就會造成連帶損害
- Code Review 時，過大的 Commit 讓 Reviewer 無從判斷每個改動的意圖
- `git bisect` 找 Bug 時，粒度太粗的 Commit 會讓定位範圍差很多

**實驗**：開啟本專案的 `src/store/cart.ts`，修改 `calcTotal` 邏輯後再修改 `addItem` 的邏輯。練習用 `git add -p` 把這兩個改動分成兩個獨立的 Commit。

### 3. .gitignore 設定原則

```bash
# 在本專案目錄查看 .gitignore
cat ../.gitignore
```

**絕對要忽略的三類檔案：**

| 類型 | 範例 | 原因 |
|------|------|------|
| 依賴套件 | `node_modules/` | 體積龐大，可從 `package.json` 還原 |
| 環境變數 | `.env`, `.env.local` | 包含 API Key、DB 密碼等機密 |
| 建置輸出 | `dist/`, `*.tsbuildinfo` | 是程式碼的衍生品，不是原碼本身 |

**正確補救方式（已誤提交時）：**

```bash
# 1. 加入 .gitignore
echo "node_modules/" >> .gitignore

# 2. 從 Git 追蹤中移除，但保留本地檔案
git rm -r --cached node_modules/

# 3. 提交這個移除動作
git commit -m "chore: remove node_modules from tracking"
```

---

## 技術解法

### 常用指令速查

```bash
# 查看詳細的暫存區差異（比 git status 更詳細）
git diff --staged

# 互動式分段暫存（必學）
git add -p

# 修改最後一個 Commit（尚未 Push 前才能用）
git commit --amend

# 查看某個 Commit 的完整內容
git show <commit-hash>

# 查看某個檔案的所有修改歷史
git log --follow -p src/store/user.ts
```

### 實際操作練習

```bash
# 在本目錄初始化 Git（如果尚未初始化）
cd /path/to/git-training-master
git init
git add .gitignore
git commit -m "chore: add gitignore for Vue 3 project"

# 接著只提交 src/types/index.ts
git add src/types/index.ts
git commit -m "feat: define core domain types (User, CartItem, Order)"

# 練習 git add -p
# 修改 src/store/cart.ts 的兩個函式，用 -p 分開提交
```

---

## 工程規範

### Conventional Commits 格式

資深工程師要求的標準 Commit Message 格式：

```
<type>(<scope>): <subject>

[optional body]

[optional footer]
```

**type 分類：**

| type | 用途 |
|------|------|
| `feat` | 新功能 |
| `fix` | Bug 修復 |
| `refactor` | 重構（不影響行為的程式碼改動） |
| `perf` | 效能改善 |
| `test` | 測試相關 |
| `docs` | 文件更新 |
| `chore` | 建置流程、工具設定（不影響程式邏輯） |
| `ci` | CI/CD 設定 |
| `revert` | 還原某個 Commit |

**好的 Commit Message 範例：**

```
feat(cart): implement quantity validation in addItem

Prevent users from adding more items than available stock.
Also coerce negative quantity inputs to 0 before removal.

Closes #142
```

**不可接受的 Commit Message（會在 Code Review 被退回）：**

```
fix
update
test123
aaa
WIP
```

### .gitignore 規範

- 使用 `.env.example` 提交環境變數範本（不含真實值）
- 提交 `.vscode/extensions.json`（推薦擴充清單），不提交 `.vscode/settings.json`（個人偏好）

---

## 本模組自我檢驗

- [ ] 能解釋 Working Directory、Staging Area、Repository 三者的差異
- [ ] 能用 `git add -p` 將一個檔案的改動拆成兩個 Commit
- [ ] `.gitignore` 中能分辨哪些該忽略、哪些不該
- [ ] 寫出符合 Conventional Commits 規範的 Message
