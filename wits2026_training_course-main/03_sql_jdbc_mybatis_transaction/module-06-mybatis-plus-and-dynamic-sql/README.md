# Module 06 — MyBatis Plus、Dynamic SQL 與 Batch 模式

## 模組目標

這一模組要把學員從「會寫單條 mapper SQL」帶到「能判斷哪些情況適合 MyBatis Plus，哪些情況必須回到明確 SQL 設計」。同時要補足 dynamic SQL、`<if>`、`<foreach>` 與 batch 操作的常見模式。

學完後，學員不只要會寫 `<if>`，還要能避免把 dynamic SQL 寫成無法維護的條件迷宮。

## 情境說明

保險系統的查詢條件很少永遠固定。學員會很快遇到：狀態可選、日期區間可選、業務別可選、批次查詢多筆單號。這時若只會寫固定 SQL，就無法支撐真實查詢需求。

## 先建立一個正確心智模型

Dynamic SQL 要解的是「條件可變」，不是「把所有業務判斷都塞進 XML」。

因此教學上要先建立三層區分：

- service：決定查詢意圖與商業規則
- query condition object：承接可選條件
- mapper XML：負責把條件安全地轉成 SQL

## 核心重點

- MyBatis Plus 的適用邊界
- `<if>` / `<choose>` / `<foreach>`
- batch insert / update 思維
- mapper 與 service 的責任分工

## 教學步驟

### Step 1：先回答什麼情況適合 MyBatis Plus

適合：單表 CRUD、簡單條件查詢、快速管理頁

不適合：多表 join、報表、複雜業務條件、交易核心流程

這裡要補強一個判斷：如果 query wrapper 已經長到閱讀者看不出 SQL 長相，通常代表它不再適合繼續留在 Plus wrapper 裡。

### Step 2：理解 dynamic SQL

```xml
<select id="findClaimsByCondition" resultType="ClaimSummary">
  select claim_no, status, assigned_adjuster
  from claim_case
  <where>
    <if test="status != null and status != ''">
      status = #{status}
    </if>
    <if test="assignedAdjuster != null and assignedAdjuster != ''">
      and assigned_adjuster = #{assignedAdjuster}
    </if>
  </where>
</select>
```

這段要帶出的重點不是 `<if>` 語法本身，而是：

- 只有條件存在時才拼進 SQL
- `<where>` 會幫忙處理前導 `and`
- 條件命名若不清楚，XML 可讀性會快速崩壞

### Step 3：理解 `<foreach>`

```xml
<foreach collection="claimNos" item="claimNo" open="(" separator="," close=")">
  #{claimNo}
</foreach>
```

這裡要補充兩個實務重點：

- 集合可能為空，要先決定空集合時應回空結果還是直接不查
- 批次查詢與批次更新都要思考數量上限與交易邊界

### Step 4：動態條件要收斂成可讀規則

若查詢條件變多，可以引導學員使用條件物件，例如：

- status
- assignedAdjuster
- startDate
- endDate
- productCode

這樣比 mapper 方法塞一長串參數更容易維護。

## Batch 模式的教學重點

batch 不只是「一次送很多筆」，還要讓學員理解：

- 每批大小會影響效能與記憶體
- 失敗時要不要整批回滾
- 哪些批次適合單一交易，哪些要切批處理

這一段很好和 module 02、04 的 transaction 基礎串起來。

## 常見錯誤

- 用 MyBatis Plus 強行處理複雜 join。
- dynamic SQL 判斷太多，最後比直接寫 SQL 更難維護。
- batch update 只顧跑得動，不考慮交易邊界與錯誤回滾。
- mapper 方法參數過多，卻沒有整理成條件物件。
- 空集合直接進 `<foreach>`，導致 SQL 結構異常或語意不清。
- 把業務規則直接寫進 XML 條件判斷，讓分層責任混亂。

## 自我檢查清單

- 我能說明 MyBatis Plus 與原生 MyBatis 的適用場景嗎？
- 我知道 `<if>` 與 `<foreach>` 最常解的問題是什麼嗎？
- 我能說明為什麼複雜 SQL 不應藏進 service 嗎？
- 我能辨識某支 dynamic SQL 是否已經過度複雜嗎？
- 我知道 batch 操作除了效能，還要考慮交易一致性嗎？

## 練習題

1. 寫一條帶可選條件的理賠查詢 dynamic SQL。
2. 寫一條批次查詢多筆理賠單號的 `<foreach>` 範例。
3. 用文字說明某功能為什麼適合或不適合 MyBatis Plus。
4. 設計一個批次更新流程，說明應該如何切分交易邊界。

## 解題方向

1. dynamic SQL 題要保持條件清楚，不要做出巨大 if 迷宮。
2. `<foreach>` 題要注意空集合與 SQL 結構。
3. MyBatis Plus 題要從查詢複雜度與維護性角度回答。
4. batch 題要同時回答效能、錯誤處理與 rollback 策略。

## 完整參考答案

標準答案重點：

- 能把「條件可變」與「SQL 可維護」同時兼顧
- 能明確區分資料層責任與業務層責任
- 能判斷何時該把 wrapper 查詢改回 XML
- 能知道 batch 不是只有 `<foreach>`，還包含交易與失敗處理策略