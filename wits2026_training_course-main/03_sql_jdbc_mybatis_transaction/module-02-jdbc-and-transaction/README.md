# Module 02 — JDBC 實作與 JDBC Transaction

## 模組目標

這一模組的目的，是讓學員知道 ORM 之前到底發生了什麼。若學員沒有看過 JDBC transaction，後面很難真正理解 `@Transactional` 為什麼有效或失效。

學完這一章後，學員至少應該能回答：

1. 一段 JDBC 查詢從拿 connection 到回傳結果，中間做了哪些事。
2. 為什麼預設 auto-commit 會讓多段更新失去原子性。
3. 發生例外時，如果沒 rollback，資料庫會留下什麼狀態。

還要再往前一步，讓學員知道 ORM 與 `@Transactional` 之所以好用，是因為它們把這一章的低階責任包起來了，而不是憑空產生交易能力。

## 核心重點

- DriverManager / DataSource 基本觀念
- Connection、PreparedStatement、ResultSet
- autoCommit
- commit 與 rollback

## 情境說明

你要實作一個「理賠入帳」流程，會做兩件事：

1. 在 `claim_case` 建立一筆理賠案件。
2. 在 `claim_ledger` 新增一筆理賠台帳。

如果第二步失敗，而第一步已經成功寫入，就會產生資料不一致。這是交易控制最常見、也最危險的真實案例。

## 先建立一個正確心智模型

JDBC 的核心不是 API 名稱，而是三層責任：

- `Connection`：交易與連線邊界
- `PreparedStatement`：SQL 與參數綁定
- `ResultSet`：查詢結果逐列讀取

教學上若沒有把這三者的責任講清楚，學員後面看到框架封裝時，會不知道到底被封裝掉了什麼。

## JDBC 執行流程拆解

### Step 1：取得連線

```java
Connection connection = dataSource.getConnection();
```

這一行的意思不只是「拿到連線」，而是：

- 取得與資料庫溝通的通道
- 準備在同一個 transaction scope 中執行多段 SQL
- 後續的 commit / rollback 都會發生在這個 connection 上

這裡一定要講一句關鍵話：如果兩段更新不是用同一個 connection，它們就不在同一個 JDBC transaction 裡。

### Step 2：準備 SQL 與參數

```java
PreparedStatement statement = connection.prepareStatement(
	"insert into claim_case (claim_no, policy_no, status, created_at) values (?, ?, ?, ?)"
);
statement.setString(1, claimNo);
statement.setString(2, policyNo);
statement.setString(3, "PENDING");
statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
```

這一步要補充兩個重點：

- `PreparedStatement` 幫助避免字串拼接與 SQL injection 風險
- 參數綁定順序若錯，程式不一定立刻報錯，但資料可能寫錯欄位

### Step 3：執行更新

```java
int updatedRows = statement.executeUpdate();
```

這裡可以順帶建立一個習慣：

- 查詢用 `executeQuery`
- 新增、更新、刪除用 `executeUpdate`

不要把它們當成只是不同函式名稱，而要知道回傳意義不同。

### Step 4：決定 commit 或 rollback

若使用預設 auto-commit，每一段 SQL 執行後都可能立即提交。這代表你很難把多段操作當成一個整體。

這一點是本章最重要的主軸之一。要讓學員看到：

- auto-commit 適合單條簡單操作
- 一旦進入多段更新流程，auto-commit 就很危險
- transaction 的本質是把多段操作綁成同生共死的一個單位

## 手動交易控制範例

```java
public void createClaimWithLedger(RegisterClaimCommand command) {
	Connection connection = null;
	PreparedStatement claimStatement = null;
	PreparedStatement ledgerStatement = null;

	try {
		connection = dataSource.getConnection();
		connection.setAutoCommit(false);

		claimStatement = connection.prepareStatement(
			"insert into claim_case (claim_no, policy_no, status, created_at) values (?, ?, ?, ?)"
		);
		claimStatement.setString(1, command.getClaimNo());
		claimStatement.setString(2, command.getPolicyNo());
		claimStatement.setString(3, "PENDING");
		claimStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
		claimStatement.executeUpdate();

		ledgerStatement = connection.prepareStatement(
			"insert into claim_ledger (claim_no, ledger_status, amount, created_at) values (?, ?, ?, ?)"
		);
		ledgerStatement.setString(1, command.getClaimNo());
		ledgerStatement.setString(2, "POSTING");
		ledgerStatement.setBigDecimal(3, command.getAmount());
		ledgerStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
		ledgerStatement.executeUpdate();

		if (command.isForceFailure()) {
			throw new IllegalStateException("simulate ledger failure");
		}

		connection.commit();
	} catch (Exception exception) {
		if (connection != null) {
			try {
				connection.rollback();
			} catch (SQLException rollbackException) {
				throw new RuntimeException("rollback failed", rollbackException);
			}
		}
		throw new RuntimeException("claim processing failed", exception);
	} finally {
		closeQuietly(ledgerStatement);
		closeQuietly(claimStatement);
		resetAutoCommit(connection);
		closeQuietly(connection);
	}
}
```

## 上面這段程式要教什麼

### 1. `setAutoCommit(false)` 是交易開始的重要訊號

不做這一步，就很可能每一段 SQL 都各自提交。

也要補充：

- 不是寫了 rollback 就一定有效
- rollback 是否有效，前提是前面真的還沒被 auto-commit 提交出去

### 2. 例外不是只拿來印 log

如果例外發生時不 rollback，資料庫就可能留下半套資料。

這裡的教學重點要明確拉回「資料一致性」，不要讓學員把 rollback 只理解成程式風格問題。

### 3. `finally` 區塊不是多餘的

JDBC 是低階 API，資源釋放責任在你手上。連線、statement 沒關，長期下來就是資源耗盡或連線池異常。

### 4. reset auto-commit 也是低階實作常被忽略的細節

若 connection 來自連線池，而你把 auto-commit 關掉後直接還回池中，後續借到同一連線的人可能會遇到很難追的異常。因此教材若有示範 reset 動作，最好把原因講清楚。

## 查詢範例

```java
public Optional<ClaimSummary> findClaimSummary(String claimNo) {
	String sql = "select claim_no, policy_no, status, approved_amount from claim_case where claim_no = ?";

	try (Connection connection = dataSource.getConnection();
		 PreparedStatement statement = connection.prepareStatement(sql)) {

		statement.setString(1, claimNo);

		try (ResultSet resultSet = statement.executeQuery()) {
			if (!resultSet.next()) {
				return Optional.empty();
			}

			ClaimSummary summary = new ClaimSummary(
				resultSet.getString("claim_no"),
				resultSet.getString("policy_no"),
				resultSet.getString("status"),
				resultSet.getBigDecimal("approved_amount")
			);
			return Optional.of(summary);
		}
	} catch (SQLException exception) {
		throw new RuntimeException("query claim summary failed", exception);
	}
}
```

## 查詢流程要教什麼

這段查詢程式除了示範 JDBC API，也是在教：

- 查詢與更新的 JDBC 路徑不同
- `ResultSet` 是逐列讀取，不是一次把所有東西 magically 變成物件
- DTO 建構是你自己負責，不是框架自動幫你完成

這能幫助學員後面理解 MyBatis 的 result mapping 價值。

## 手動交易排查順序

當學員發現資料沒回滾時，可以要求他照這個順序檢查：

1. 是否真的用了同一個 connection
2. 是否在操作前關掉 auto-commit
3. 是否有例外被吞掉
4. 是否真的執行到 rollback
5. 是否中途有人先 commit

這一段非常重要，因為它會直接銜接後面的 `@Transactional` 排查思路。

## 常見錯誤

- 只要用到 JDBC 就到處 `throws SQLException`，沒有整理成應用層可理解的錯誤。
- 忘記 `setAutoCommit(false)`，導致 rollback 看起來有寫但根本無效。
- 發生例外後只印 log，不 rollback。
- `finally` 沒有關 statement / connection。
- 在同一流程混用不同 connection，結果你以為同一交易，實際上根本不是。
- 用字串拼 SQL，結果參數轉型與注入風險一起進來。
- 查詢與更新都用同一種處理方式，沒有意識到回傳語意不同。
- 只驗 Java 執行結果，不回資料庫確認實際狀態。

## 練習題

1. 實作「新增理賠案件 + 新增理賠台帳」手動交易版。
2. 在第二段更新前故意丟出例外，驗證資料是否回滾。
3. 把 auto-commit 打開，再做一次相同測試，觀察差異。
4. 實作一個查詢方法，將 ResultSet 映射成 DTO，並比較與框架自動映射的差異。

## 練習解答方向

1. 成功解不只看 Java 沒報錯，要回資料庫確認資料筆數。
2. 若 rollback 沒發生，先檢查是否真的用了同一個 connection。
3. 若發現第一筆資料還在，通常代表 auto-commit 未關閉，或 commit 時機錯了。
4. DTO 映射題要讓學員看見欄位名稱、型別與 null 處理都必須自己負責。

## 驗收標準

- 能寫出一個基本查詢與一個更新流程
- 能用手動交易處理兩段更新
- 能說明為什麼例外發生時要 rollback
- 能解釋 auto-commit 對多段資料更新的影響
- 能描述 Connection、PreparedStatement、ResultSet 各自的責任