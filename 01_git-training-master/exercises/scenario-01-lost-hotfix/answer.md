# Git 實戰情境一：緊急熱修復與開發進度暫存 — 題解

本篇紀錄在功能開發途中，遭遇線上緊急 Bug 並成功進行熱修復的精簡流程。

---

## 🛠 實際操作步驟紀錄

### Step 1: 暫存未完成的功能
在 `feature/cart-logic` 分支開發到一半時，使用 `stash` 將半成品代碼藏入暫存區，保持工作區乾淨。
```powershell
git stash push -m "WIP: adding discount coupon logic to useCart"

Step 2: 建立熱修復分支
切換回主線 main，並以此為基準拉出獨立的 hotfix/calc-error 分支進行 Bug 修復。

git checkout main
git checkout -b hotfix/calc-error

Step 3: 修復 Bug 並提交
打開 src/store/cart.ts，將計算總金額少乘數量的 Bug 修正（補上 * item.quantity），隨後提交 Commit。

git add .
git commit -m "fix(cart): correct subtotal calculation to multiply by quantity"

太完美了！歷史接軌成功！！！ 🎉🎉🎉

你看倒數第二行：

HEAD is now at 6fe6416 Add modules for Quasar integration...
Switched to a new branch 'feature/cart-logic'

這代表你的本地專案已經百分之百認了老師的 GitHub 歷史當作祖先，而且此時你正站在最乾淨、最正確的 feature/cart-logic 開發分支上。這一次的起跑點完全正確！

接下來我們要用最快的速度把剛剛修 Bug 和題解的成果補進去。

Step 1: 把成果補回去（手動複製貼上）
新增 answer.md：
在 VS Code 左側檔案樹中，找到這個路徑：
01_git-training-master/exercises/scenario-01-lost-hotfix/
在該資料夾下新增一個檔案叫 answer.md，並把下面這段精簡版題解內容貼進去存檔（Ctrl + S）：

Markdown
# Git 實戰情境一：緊急熱修復與開發進度暫存 — 題解

本篇紀錄在功能開發途中，遭遇線上緊急 Bug 並成功進行熱修復的精簡流程。

---

## 🛠 實際操作步驟紀錄

### Step 1: 暫存未完成的功能
在 `feature/cart-logic` 分支開發到一半時，使用 `stash` 將半成品代碼藏入暫存區，保持工作區乾淨。
```powershell
git stash push -m "WIP: adding discount coupon logic to useCart"
Step 2: 建立熱修復分支
切換回主線 main，並以此為基準拉出獨立的 hotfix/calc-error 分支進行 Bug 修復。

PowerShell
git checkout main
git checkout -b hotfix/calc-error
Step 3: 修復 Bug 並提交
打開 src/store/cart.ts，將計算總金額少乘數量的 Bug 修正（補上 * item.quantity），隨後提交 Commit。

PowerShell
git add .
git commit -m "fix(cart): correct subtotal calculation to multiply by quantity"
Step 4: 合併回主線並刪除分支
切換回 main 分支，將修復完成的成果以 --no-ff 模式合併回主線，隨後刪除臨時的 hotfix 分支。

git checkout main
git merge --no-ff hotfix/calc-error -m "Merge hotfix/calc-error: fix cart subtotal calculation"
git branch -d hotfix/calc-error

太完美了！歷史接軌成功！！！ 🎉🎉🎉

你看倒數第二行：

HEAD is now at 6fe6416 Add modules for Quasar integration...
Switched to a new branch 'feature/cart-logic'

這代表你的本地專案已經百分之百認了老師的 GitHub 歷史當作祖先，而且此時你正站在最乾淨、最正確的 feature/cart-logic 開發分支上。這一次的起跑點完全正確！

接下來我們要用最快的速度把剛剛修 Bug 和題解的成果補進去。

Step 1: 把成果補回去（手動複製貼上）
新增 answer.md：
在 VS Code 左側檔案樹中，找到這個路徑：
01_git-training-master/exercises/scenario-01-lost-hotfix/
在該資料夾下新增一個檔案叫 answer.md，並把下面這段精簡版題解內容貼進去存檔（Ctrl + S）：

Markdown
# Git 實戰情境一：緊急熱修復與開發進度暫存 — 題解

本篇紀錄在功能開發途中，遭遇線上緊急 Bug 並成功進行熱修復的精簡流程。

---

## 🛠 實際操作步驟紀錄

### Step 1: 暫存未完成的功能
在 `feature/cart-logic` 分支開發到一半時，使用 `stash` 將半成品代碼藏入暫存區，保持工作區乾淨。

git stash push -m "WIP: adding discount coupon logic to useCart"
Step 2: 建立熱修復分支
切換回主線 main，並以此為基準拉出獨立的 hotfix/calc-error 分支進行 Bug 修復。


git checkout main
git checkout -b hotfix/calc-error
Step 3: 修復 Bug 並提交
打開 src/store/cart.ts，將計算總金額少乘數量的 Bug 修正（補上 * item.quantity），隨後提交 Commit。


git add .
git commit -m "fix(cart): correct subtotal calculation to multiply by quantity"
Step 4: 合併回主線並刪除分支
切換回 main 分支，將修復完成的成果以 --no-ff 模式合併回主線，隨後刪除臨時的 hotfix 分支。


git checkout main
git merge --no-ff hotfix/calc-error -m "Merge hotfix/calc-error: fix cart subtotal calculation"
git branch -d hotfix/calc-error

Step 5: 同步主線並彈回暫存代碼
回到原本的功能開發分支 feature/cart-logic，透過 rebase 把 main 剛修好的 Bug 同步進來，最後用 pop 將一開始寫到一半的代碼彈回工作區繼續開發。

git checkout feature/cart-logic
git rebase main
git stash pop