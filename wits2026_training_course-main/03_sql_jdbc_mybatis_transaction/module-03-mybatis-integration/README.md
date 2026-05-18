# Module 03 — MyBatis / MyBatis Plus 整合

## 模組目標

要讓學員知道 MyBatis 不是拿來逃避 SQL，而是把 SQL 以可維護方式放進專案。MyBatis Plus 可以加速單表 CRUD，但不能代替對交易與複雜查詢的理解。

學完這一章後，學員應該能判斷：

1. 哪些查詢適合寫在 mapper XML。
2. 哪些 CRUD 可以交給 MyBatis Plus。
3. 哪些查詢如果硬塞在 service，後續維護一定會痛苦。

此外，還要讓學員理解 MyBatis 的價值不是省掉 SQL，而是把 SQL 與 Java 職責拆乾淨，讓資料存取更可讀、更可測、更容易排查。

## 核心重點

- Mapper 介面與 XML / Annotation SQL
- 參數綁定與結果映射
- MyBatis Plus 的快速 CRUD 與限制
- 複雜 SQL 仍應回到明確查詢設計

## 為什麼專案會需要 MyBatis

當 SQL 變複雜後，如果你把它們都寫在 Java 字串裡，會遇到以下問題：

- SQL 不好閱讀
- 條件一多就很難維護
- Java 與 SQL 混在一起，責任邊界不清楚
- 測試與除錯時很難快速定位問題

MyBatis 的價值，不是「讓你不用學 SQL」，而是「讓 SQL 留在它應該待的地方」。

## 先建立一個正確心智模型

這一章教的不是單純框架操作，而是分層設計：

- mapper：定義資料存取行為
- XML / annotation SQL：描述查詢與更新邏輯
- DTO / entity：承接資料映射結果
- service：承接業務規則、例外語意與交易邊界

如果這些責任分不清，學員很容易把 mapper 變成 service、把 service 變成 SQL 拼接區。

## 基本結構

### Mapper 介面

```java
public interface PolicyQueryMapper {

	PolicySummary findPolicySummary(@Param("policyNo") String policyNo);

	List<OverduePolicyView> findOverduePolicies(@Param("batchDate") LocalDate batchDate);
}
```

### XML 查詢

```xml
<select id="findPolicySummary" resultType="com.company.training.jdbcdemo.dto.PolicySummary">
	select p.policy_no,
		   p.product_code,
		   h.holder_name,
		   coalesce(sum(pp.paid_amount), 0) as total_paid_amount
	from policy p
	join policy_holder h on h.holder_id = p.holder_id
	left join premium_payment pp on pp.policy_id = p.id
	where p.policy_no = #{policyNo}
	group by p.policy_no, p.product_code, h.holder_name
</select>
```

### Step 1：方法名稱要能表達業務意圖

`findPolicySummary`、`findOverduePolicies` 這類名稱的價值，在於團隊看到介面就知道這支查詢在做什麼。若方法名稱只有 `query1`、`selectData`，後續維護成本會快速上升。

### Step 2：parameter 與 result mapping 要當成契約處理

mapper 介面的輸入與輸出，本質上也是資料層契約。要引導學員思考：

- 單一參數還是條件物件
- 回傳單筆、列表、Optional 還是分頁物件
- 這支查詢回 DTO、entity 還是 view model 更合理

## 這段範例要帶出的觀念

### 1. Java 方法名稱對應的是業務意圖

`findPolicySummary` 比 `query1` 有意義得多。學員需要養成「方法名稱描述業務目的」的習慣。

### 2. SQL 仍然要自己負責

用 MyBatis 不代表可以放棄 SQL 設計。join、group by、索引、查詢成本仍然是資料層工程師必須理解的事。

這裡最好明講：

- MyBatis 不是 ORM 魔法
- 它不會替你修正錯誤 join
- 它也不會自動讓慢查詢變快

### 3. DTO 與資料映射要清楚

不要什麼都回 `Map<String, Object>`。如果查詢有穩定用途，就應該有對應 DTO。

也可以再補上：

- 欄位別名要與 DTO 欄位語意一致
- 查詢用途若很明確，DTO 名稱也應反映用途，而不是通用到失去意義

## MyBatis Plus 什麼時候適合用

適合：

- 單表 CRUD
- 欄位規則簡單
- 查詢條件固定且不複雜

不適合：

- 多表 join
- 報表式查詢
- 有大量業務條件判斷的 SQL
- 對資料一致性與查詢可讀性要求高的核心交易查詢

### Step 3：MyBatis Plus 的價值與邊界要一起教

教學上很容易只講「它很方便」，但真正要建立的是判斷力：

- 方便不代表所有情況都適合
- wrapper 越寫越長時，往往表示查詢已超出它的舒適範圍
- 核心查詢若不夠可讀，後續調校與除錯會非常痛苦

## Service 層如何搭配

```java
@Service
public class PolicyQueryService {

	private final PolicyQueryMapper policyQueryMapper;

	public PolicyQueryService(PolicyQueryMapper policyQueryMapper) {
		this.policyQueryMapper = policyQueryMapper;
	}

	public PolicySummary getPolicySummary(String policyNo) {
		PolicySummary summary = policyQueryMapper.findPolicySummary(policyNo);
		if (summary == null) {
			throw new IllegalArgumentException("policy not found: " + policyNo);
		}
		return summary;
	}
}
```

這段要讓學員理解：

- mapper 專注資料存取
- service 專注業務規則與例外語意
- controller 不應直接碰 mapper

### Step 4：例外語意要在 service 層收斂

例如 mapper 查不到資料，service 要決定這是：

- 正常查無資料
- 還是業務上不允許發生的狀態

這一段若不講，學員會傾向讓 controller 直接面對 `null`，導致整體分層鬆散。

## XML、Annotation、MyBatis Plus 的選擇原則

可以明確整理成一張判斷表：

- Annotation：短小、固定、可讀的 SQL
- XML：複雜查詢、動態條件、多表 join
- MyBatis Plus：單表 CRUD、後台列表、簡單條件

這個判斷框架會讓學員後面在 module 06 的 dynamic SQL 更容易上手。

## 常見錯誤

- 把所有 SQL 都寫成 annotation，導致長查詢完全不可讀。
- service 直接組 SQL 片段，破壞分層。
- 過度依賴 MyBatis Plus，複雜查詢仍硬寫在 wrapper 中。
- mapper 回傳 `null` 後，service 沒處理，導致後面發生 `NullPointerException`。
- 把所有資料物件都混成同一種 class，失去查詢用途邊界。
- SQL 可跑，但 method 名稱與實際查詢意圖不一致。
- 查詢映射失敗時只怪框架，沒有先檢查欄位別名與 DTO 結構。

## 練習題

1. 把「有效保單但最近一期保費逾期」查詢放入 mapper XML。
2. 新增一個單表查詢，改用 MyBatis Plus 實作。
3. 比較同一查詢寫在 annotation 與 XML 的可讀性差異。
4. 為某支查詢設計合適的 DTO，並說明為什麼不直接回 entity。

## 練習解答方向

1. 複雜 SQL 題建議用 XML，不要硬用 annotation。
2. 若查詢只有單表與簡單條件，才適合示範 MyBatis Plus。
3. service 不要直接返回 mapper 結果給 controller，至少要有錯誤語意與業務檢查。
4. DTO 題要從欄位穩定性、查詢用途與契約清晰度回答。

## 驗收標準

- 能把複雜 SQL 放入 mapper 結構
- 能說明何時適合用 MyBatis Plus，何時不適合
- 能把資料層錯誤回報到 service 層
- 能解釋 mapper、service、DTO 三者的責任分界
- 能說明 XML、Annotation、MyBatis Plus 三者的選用原則