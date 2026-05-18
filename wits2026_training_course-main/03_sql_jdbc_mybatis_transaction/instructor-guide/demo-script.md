# Demo Script

## Demo 1：從資料表開始，而不是從註解開始

1. 先開 schema 與 data 檔。
2. 讓學員指出 policy、claim、ledger 的關係。
3. 再開始寫 SQL。

## Demo 2：JDBC 與手動 rollback

1. 開 repository 程式。
2. 示範 `setAutoCommit(false)`。
3. 故意製造失敗，讓學員看 rollback 前後差異。

## Demo 3：MyBatis 與 dynamic SQL

1. 開 mapper 與 XML。
2. 解釋為什麼複雜 SQL 不該散在 service。
3. 示範 `<if>`、`<foreach>` 的用途。

## Demo 4：`@Transactional` 失效與 propagation

1. 示範 self-invocation。
2. 再解釋 checked exception、`rollbackFor` 與 propagation。
3. 最後帶 exercises。