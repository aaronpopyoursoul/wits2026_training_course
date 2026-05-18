# Module 04 — 交易控制與 @Transactional 深入解析

## 模組目標

這一模組要把學員從會用註解，拉到會判斷交易為什麼沒生效。金融與壽險系統中，一次核保審核或理賠入帳若只成功一半，後果比單純 API 500 還嚴重。

學完這一章後，學員應該能自己排查以下問題：

- 為什麼方法上有 `@Transactional`，資料還是沒回滾。
- 為什麼丟了例外卻沒有 rollback。
- 為什麼同一個 service 內部互相呼叫時交易失效。

還要再往前一步，讓學員知道 `@Transactional` 本質上是框架幫你代理與包裝交易邏輯，不是魔法開關。這個觀念一旦建立，後面看到 propagation、readOnly、timeout 才不會只是背設定。

## 核心重點

- `@Transactional` 基本行為
- `rollbackFor`
- propagation
- self-invocation 失效
- checked exception 與 runtime exception 差異

## 先建立一個正確心智模型

`@Transactional` 不是直接改變 Java 語法，而是透過 Spring AOP 代理，在方法進入前開交易、方法成功後提交、發生符合條件的例外時回滾。

所以你至少要知道三件事：

1. 這個方法是不是經過 Spring 管理的 bean。
2. 這次呼叫是不是經過代理物件，而不是自己在同類別內直接呼叫自己。
3. 發生的例外類型是否符合 rollback 規則。

這一段最好再補一個判斷：交易邊界應該設在「完整業務動作」的位置，而不是看到方法就亂加 annotation。

## 基本範例

```java
@Service
public class ClaimProcessingService {

	private final ClaimCaseRepository claimCaseRepository;
	private final ClaimLedgerJdbcRepository claimLedgerJdbcRepository;

	public ClaimProcessingService(
		ClaimCaseRepository claimCaseRepository,
		ClaimLedgerJdbcRepository claimLedgerJdbcRepository
	) {
		this.claimCaseRepository = claimCaseRepository;
		this.claimLedgerJdbcRepository = claimLedgerJdbcRepository;
	}

	@Transactional(rollbackFor = Exception.class)
	public void registerClaim(RegisterClaimCommand command) {
		claimCaseRepository.insert(command.toClaimCase());
		claimLedgerJdbcRepository.insertLedger(command.toLedger());

		if (command.isForceFailure()) {
			throw new Exception("simulate claim posting failure");
		}
	}
}
```

## 上面這段程式的重點

### 1. 交易邊界在 service，不在 controller

因為 service 才代表一個完整業務動作。controller 是 HTTP 入口，不應承擔交易管理責任。

也不建議把交易邊界放到 repository，因為 repository 只負責單一資料操作，無法代表完整商業流程。

### 2. `rollbackFor = Exception.class` 是刻意補強 checked exception

Spring 預設對 runtime exception 與 error 回滾。若丟的是 checked exception，沒有設定 `rollbackFor` 時，常會出現「有報錯，但資料沒回滾」的情況。

這裡很適合補一句：

- `RuntimeException` 與 `Exception` 在交易行為上的差異，不是 Java 考試題，而是直接影響資料一致性

## 常見失效場景

### 失效場景 1：self-invocation

```java
@Service
public class BatchClaimService {

	public void processBatch(List<RegisterClaimCommand> commands) {
		for (RegisterClaimCommand command : commands) {
			saveSingleClaim(command);
		}
	}

	@Transactional
	public void saveSingleClaim(RegisterClaimCommand command) {
		// do insert
	}
}
```

問題在哪裡：

- `processBatch` 直接呼叫同類別內的 `saveSingleClaim`
- 呼叫沒有經過 Spring 代理
- `@Transactional` 不會生效

修正方式：

- 將 `saveSingleClaim` 拆到另一個 bean
- 或重新設計交易邊界，不在同類別內直接呼叫

這一段一定要讓學員意識到：不是 annotation 寫在那裡就有用，而是呼叫路徑有沒有經過 Spring proxy 才重要。

### 失效場景 2：例外被吃掉

```java
@Transactional
public void registerClaim(RegisterClaimCommand command) {
	try {
		claimCaseRepository.insert(command.toClaimCase());
		claimLedgerJdbcRepository.insertLedger(command.toLedger());
		throw new IllegalStateException("simulate failure");
	} catch (Exception exception) {
		log.error("registerClaim failed", exception);
	}
}
```

問題在哪裡：

- 例外被 catch 住後沒有再丟出
- Spring 看起來像方法正常結束
- 最終可能直接 commit

修正方式：

- catch 後重新丟出
- 或明確使用 `TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()`，但不建議初學者一開始就走這條路

這裡可以補一個實務提醒：許多交易沒回滾的根因，不是 annotation 配錯，而是例外被吃掉。

### 失效場景 3：例外類型不符合 rollback 規則

```java
@Transactional
public void registerClaim(RegisterClaimCommand command) throws Exception {
	claimCaseRepository.insert(command.toClaimCase());
	throw new Exception("checked exception without rollbackFor");
}
```

問題在哪裡：

- 這是 checked exception
- 預設不一定回滾

修正方式：

```java
@Transactional(rollbackFor = Exception.class)
```

### 失效場景 4：方法不是 public 或不在 Spring 管理流程中

雖然不同代理方式細節略有差異，但教學上至少要建立一個保守原則：

- 把交易方法放在 Spring 管理的 service bean 中
- 以清楚的公開業務方法作為交易入口

這樣能避開很多初學者在代理機制上的混淆。

## propagation 要怎麼講

這一段不需要一開始就把所有 propagation 枚舉背完，而是先讓學員理解最常見的兩種：

- `REQUIRED`：有交易就加入，沒有就新開，這是預設值
- `REQUIRES_NEW`：不管外面有沒有交易，自己新開一個

### 課堂提醒

若外層交易失敗，但內層用了 `REQUIRES_NEW` 且已成功提交，內層資料可能保留。這不是框架壞掉，而是你刻意切開了交易邊界。

這裡可以帶入一個常見壽險情境：

- 主流程失敗要回滾
- 但審計紀錄想保留

此時是否使用 `REQUIRES_NEW`，就不是語法選項，而是業務決策。

## 測試驗證範例

```java
@Test
void shouldRollbackWhenLedgerInsertFails() {
	RegisterClaimCommand command = new RegisterClaimCommand(
		"CLM-20240009",
		"PL20240001",
		new BigDecimal("3000"),
		true
	);

	assertThatThrownBy(() -> claimProcessingService.registerClaim(command))
		.isInstanceOf(Exception.class);

	assertThat(claimCaseRepository.findByClaimNo("CLM-20240009")).isEmpty();
	assertThat(claimLedgerJdbcRepository.findByClaimNo("CLM-20240009")).isEmpty();
}
```

這個測試的教學價值很高，因為它不是只驗證「有拋錯」，而是驗證「資料庫狀態符合 rollback 預期」。

## `@Transactional` 排查順序

當交易沒有照預期回滾時，可要求學員依序檢查：

1. 方法是否在 Spring 管理的 bean 上
2. 呼叫是否有經過代理
3. 是否是同類別 self-invocation
4. 是否有例外被 catch 後吞掉
5. 例外類型是否符合 rollback 規則
6. 是否有 propagation 把交易邊界切開

這套順序能幫學員把排查從「猜設定」變成「按路徑檢查」。

## 常見錯誤

- 在 private 方法上加 `@Transactional`，然後以為會生效。
- 同類別內部直接呼叫 transactional 方法。
- catch 例外後不再往外丟。
- 不知道 checked exception 與 runtime exception 的差異。
- 看見資料沒回滾時，只檢查 annotation，卻不檢查呼叫路徑。
- 在 controller 或 repository 層隨意加 `@Transactional`，沒有想清楚邊界。
- 把 propagation 當成預設設定表，沒有結合業務意圖理解。
- 只驗證有沒有拋錯，沒有驗證資料最終狀態。

## 練習題

1. 設計一個跨兩張表的交易案例，並故意讓第二段更新失敗。
2. 實驗 self-invocation 導致交易失效的情境。
3. 比較 `@Transactional` 與 `@Transactional(rollbackFor = Exception.class)` 的差異。
4. 設計一個主流程失敗但審計紀錄要保留的情境，說明 propagation 選擇。

## 練習解答方向

1. 題目不能只看程式是否拋錯，必須查資料表確認 rollback。
2. self-invocation 題要先證明方法的確被同類別直接呼叫。
3. checked exception 題要特別記錄「為什麼有錯但沒回滾」。
4. propagation 題要從交易邊界與業務意圖兩個角度回答，不能只背 `REQUIRES_NEW` 名稱。

## 驗收標準

- 能說明常見失效場景
- 能判斷 rollback 為什麼沒有發生
- 能設計一個跨兩張表的交易案例
- 能用測試或 SQL 驗證交易是否真的生效
- 能用排查順序解釋 `@Transactional` 失效原因