# Scenario 03 — Interceptor 攔截範圍錯誤

## 練習目標

這一題要讓學員知道：攔截範圍不是小設定，而是日誌品質與維運成本的核心設計之一。攔太多會讓噪音淹沒真正需要稽核的資訊，攔太少又會留下觀測缺口。

## 背景

系統已經有 RequestLoggingInterceptor，但上線後發現健康檢查與靜態資源也被大量記錄，導致日誌噪音過高，真正需要稽核的保單查詢、理賠進度與核保審核請求反而難以追蹤。

## 問題拆解

這題的本質不是「有沒有攔截器」，而是「攔截器是否攔對了東西」。

如果全部攔：

- 噪音過高
- 日誌成本增加
- 稽核重點被掩蓋

如果攔太少：

- 重要業務路徑缺失
- 稽核斷點無法還原

## 任務

1. 調整 Interceptor 的註冊範圍。
2. 排除不需要的路徑，例如：
   - `/health`
   - `/favicon.ico`
   - `/css/**`
   - `/js/**`
3. 保留需要被稽核的業務路徑，例如：
   - `/policies/**`
   - `/claims/**`
   - `/underwriting/**`
4. 以 Git 提交這次調整。

## 預期解法

在 `WebMvcConfigurer#addInterceptors` 中：

- 指定 include patterns
- 指定 exclude patterns
- 不要把所有請求一律攔截

## 建議作答步驟

### Step 1：先列出哪些路徑屬於業務稽核範圍

例如：

- `/policies/**`
- `/claims/**`
- `/underwriting/**`

### Step 2：再列出哪些路徑應排除

例如：

- `/health`
- `/favicon.ico`
- `/css/**`
- `/js/**`

### Step 3：調整 Interceptor 註冊策略

在 `WebMvcConfigurer#addInterceptors` 中設計 include 與 exclude patterns，而不是在 Interceptor 內部用大量 `if` 判斷來硬擋。

### Step 4：驗證噪音是否下降

調整後要實際觀察：

- 健康檢查與靜態資源是否不再佔滿日誌
- 業務 API 是否仍能留下完整稽核紀錄

## 工程規範

Commit message 範例：

```bash
git commit -m "fix(interceptor): narrow audit logging paths"
```

## 常見錯誤

- 只加 exclude，不先定義真正要稽核的 include 範圍。
- 在 AOP 層處理這題，忽略這其實是 MVC 路徑層設計。
- 把健康檢查完全納入業務稽核，導致噪音失控。
- 修改後沒有用實際請求驗證攔截結果。

## 自我檢查清單

- 我能清楚分辨業務稽核路徑與系統性雜訊路徑嗎？
- 我知道這題該在 MVC Interceptor 註冊層處理嗎？
- 我是否驗證過 include / exclude patterns 的實際效果？
- 我能解釋為什麼攔截範圍本身就是設計的一部分嗎？

## 考核點

- 是否理解攔截範圍本身就是設計的一部分
- 是否能平衡稽核需求與日誌噪音
- 是否知道這類問題應在 MVC 層調整，而不是塞進 AOP

## 解答方向

1. 正確方向不是把所有請求都攔住，而是只攔真正需要稽核的業務路徑。
2. 這題應在 Interceptor 註冊與路徑策略層解決，不應轉嫁到 AOP 或 controller。
3. 驗收重點不只是程式有改，而是日誌噪音降低且核心業務路徑仍保留完整觀測。
