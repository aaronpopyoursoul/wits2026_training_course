# Module 03 — Maven 與 Spring Boot 打包機制

## 模組目標

這一模組要把學員從「會下 `mvn package`」拉到「知道產物是什麼、為什麼能部署、部署失敗時要先看哪裡」。學完這一章後，學員應能區分 JAR 與 WAR、理解 `provided` scope，並解釋 Spring Boot 外部 Tomcat 部署為什麼需要額外設定。

此外，學員還要能把 Maven lifecycle、依賴 scope、產物類型與部署目標串成一條完整因果鏈，而不是各記各的名詞。

## 開發痛點

學員常知道 `mvn package` 會產生檔案，但不知道產生的是什麼、為什麼能跑、又為什麼換到外部 Tomcat 時需要 WAR 而不是 JAR。

在金融與壽險專案中，這種不理解的後果很直接：

- 把可執行 JAR 拿去丟外部 Tomcat，導致保單查詢模組部署失敗
- 把 servlet container 打進 WAR，造成外部容器衝突
- 不知道 `provided` scope 的用途，導致類別重複或啟動異常

## 先建立一個正確心智模型

這一章最容易被學成背誦題，所以要先建立一個實務順序：

1. 先確認部署目標是什麼
2. 再決定產物類型是 JAR 還是 WAR
3. 再決定依賴該怎麼參與打包
4. 最後才看 Maven 指令與 plugin 細節

如果這個順序倒過來，學員常常會知道怎麼改 `pom.xml`，卻不知道為什麼要這樣改。

## 情境說明

假設你已經完成一個可在本機 main class 啟動的 Spring Boot 專案，現在團隊要求你將它交付給外部 Tomcat。這時如果你不知道 JAR 與 WAR 的差異，也不了解 `spring-boot-starter-tomcat` 為什麼要設成 `provided`，就很容易讓部署在測試環境失敗。

## 技術解法

### Maven lifecycle 基本觀念

本課程至少要理解：

- `clean`：清除先前輸出
- `compile`：編譯主程式
- `test`：執行測試
- `package`：打包產出 artifact
- `install`：安裝到本機 Maven repository

這裡要明講：lifecycle 不是一串要背的單字，而是描述交付過程的節點。

### Maven lifecycle 怎麼記

建議不要死背，而是對照交付流程理解：

1. `clean`：清掉舊產物，避免殘留干擾
2. `compile`：確認主程式是否能編譯
3. `test`：確認既有測試與基本邏輯未壞
4. `package`：產出可交付 artifact
5. `install`：讓本機其他模組也能引用這份產物

### Step 1：把 lifecycle 與交付動作對上

例如：

- `compile` 失敗，代表原始碼還沒達到可編譯狀態
- `test` 失敗，代表你還不能自信交付
- `package` 成功，才代表有產物可被部署驗證

這種理解方式會比死背 phase 更實用。

### 依賴管理重點

Maven 不只是下載 jar，它的核心價值是：

1. 用 `pom.xml` 明確描述專案依賴
2. 用 scope 控制依賴是否參與編譯、測試、執行與部署
3. 讓團隊成員與 CI 可以重建相同的依賴圖

這一步也要補一個核心觀念：`pom.xml` 是專案依賴契約，不是只給 Maven 自己看的檔案。

### JAR 與 WAR 的差異

#### JAR

- 通常搭配內嵌 Tomcat
- 適合本地快速開發與自帶容器執行
- 啟動方式通常是 `java -jar xxx.jar`

#### WAR

- 適合部署到外部 Tomcat
- 容器由伺服器端提供
- `spring-boot-starter-tomcat` 多半需改成 `provided`
- Application 類別需考慮 `SpringBootServletInitializer`

### Step 2：JAR 與 WAR 的差異不是副檔名，而是執行責任不同

- JAR 常代表應用自己帶容器跑起來
- WAR 常代表應用交給外部容器承載

這就是為什麼部署目標一改，打包策略、scope 與初始化方式也要跟著改。

## 關鍵設定範例

### `pom.xml` 片段

```xml
<packaging>war</packaging>

<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-tomcat</artifactId>
	<scope>provided</scope>
</dependency>
```

### Application 類別片段

```java
@SpringBootApplication
public class TrainingAuditDemoApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TrainingAuditDemoApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(TrainingAuditDemoApplication.class, args);
	}
}
```

這段要讓學員知道：Application 類別不是永遠只要 `main` 方法。當部署目標變成外部容器時，初始化入口也要跟著調整。

## 逐步操作

### Step 1：先確認部署目標

如果目標是外部 Tomcat，就要先問自己：

- 產物應該是 JAR 還是 WAR
- Tomcat 由誰提供
- 專案要不要自己內嵌 servlet container

這一步是全章最重要的判斷起點。若這裡答錯，後面 `pom.xml` 再怎麼改都可能方向錯誤。

### Step 2：檢查 `pom.xml`

重點檢查：

- 是否設定 `packaging` 為 `war`
- Tomcat starter scope 是否為 `provided`
- Java 版本與 parent/plugin 是否一致

這裡可以要求學員不要只檢查「有沒有設定」，還要能說出每個設定背後的部署意圖。

### Step 3：檢查 Application 類別

若要部署至外部容器，通常要補上 `SpringBootServletInitializer` 與 `configure` 方法，讓容器知道如何初始化應用。

這一步要建立一個觀念：Maven 打包設定與程式初始化方式是同一條部署鏈上的兩端，不能只改一邊。

### Step 4：實際打包與驗證

建議至少執行：

```bash
mvn clean package
mvn dependency:tree
```

第一個確認產物，第二個確認依賴圖與是否有不該被打進 WAR 的容器依賴。

### Step 5：把「打包成功」與「部署成功」分開

很多學員看到 `mvn clean package` 成功就以為完成，但這只代表產物被做出來，不代表外部 Tomcat 一定能正確部署與啟動。

### 為什麼本課程以 WAR 為主

因為交付要求明確指出：要能掛載於本地 Eclipse Tomcat Server 成功啟動。這代表學員不能只停留在 embedded Tomcat 模式，也不能把 WAR 與外部容器的責任混在一起。

這一段可順便收斂：部署目標先決，打包策略後決，這是本章的核心判斷線。

## 工程規範

- `pom.xml` 需清楚標明 Java 版本與 packaging 類型
- 打包策略要能被文件解釋，不接受只靠 IDE 神奇按鈕完成
- 若部署目標是外部 Tomcat，必須解釋為什麼 Tomcat 依賴要設為 `provided`
- 每次調整打包設定都應有可讀的 commit message

也建議在 README 或 SETUP 文件中標示：

- 產物名稱
- context path
- 部署目標容器版本
- 驗證 URL

## 常見錯誤

- packaging 還是 `jar`，卻想部署到外部 Tomcat。
- `spring-boot-starter-tomcat` 沒設 `provided`，造成容器重複。
- 只改 `pom.xml`，卻忘了 Application 類別初始化方式。
- 打包成功就以為部署一定成功，沒有再做外部容器驗證。
- 用 `mvn package` 當黑盒子，不理解產物與依賴圖。
- 無法從部署需求反推 `pom.xml` 的正確設定。
- 只會改設定值，不會描述背後原因。

## 常用指令

```bash
mvn clean
mvn clean package
mvn dependency:tree
mvn help:effective-pom
```

## 自我檢查清單

- 我能清楚說明 JAR 與 WAR 的責任差異嗎？
- 我知道 `provided` scope 不是「比較進階的語法」，而是部署責任切分嗎？
- 我有在外部 Tomcat 上驗證，而不是只看 `mvn package` 成功嗎？
- 我能從 `dependency:tree` 看出容器相關依賴嗎？
- 我能把部署目標、產物型態、scope 與初始化方式串成同一條因果鏈嗎？
- 我知道為什麼 `pom.xml` 與 Application 類別要一起看嗎？

## 練習題

1. 解釋為什麼這門課的專案要以 WAR 為主，而不是 JAR。
2. 說明若 `spring-boot-starter-tomcat` 沒設 `provided`，可能造成什麼風險。
3. 用文字描述從 `pom.xml` 到外部容器部署成功之間的因果鏈。
4. 解釋為什麼 `package` 成功不代表外部部署一定成功。

## 練習解答方向

1. WAR 題要從外部容器部署需求回答，不要只說「老師這樣要求」。
2. `provided` 題要談到容器重複、類別衝突與啟動異常風險。
3. 因果鏈題要能串起 packaging、dependency、initializer、deployment 四個環節。
4. 驗證題要指出打包、部署、啟動、URL 驗證是四個不同層次。

## 本模組驗收

- 能口頭說明 JAR 與 WAR 的差異
- 能說明 `provided` scope 的用途
- 能指出為什麼 Spring Boot 外部部署要繼承 `SpringBootServletInitializer`
- 能用部署目標反推正確的打包設定
- 能解釋從 Maven 打包到外部容器部署的完整因果鏈
