<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Policy Dashboard</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 24px; }
        .card { border: 1px solid #d0d7de; border-radius: 8px; padding: 16px; margin-bottom: 16px; }
        .label { color: #57606a; font-size: 14px; }
        .value { font-size: 18px; font-weight: bold; }
    </style>
</head>
<body>
    <h1>保單查詢首頁</h1>

    <div class="card">
        <div class="label">保單號碼</div>
        <div class="value"><%= request.getAttribute("policyNo") == null ? "POL20260001" : request.getAttribute("policyNo") %></div>
    </div>

    <div class="card">
        <div class="label">保戶姓名</div>
        <div class="value"><%= request.getAttribute("policyHolderName") == null ? "陳小美" : request.getAttribute("policyHolderName") %></div>
    </div>

    <div class="card">
        <div class="label">JSP 教學重點</div>
        <ul>
            <li>畫面內容可由 server 先放入 request attribute</li>
            <li>JSP 主要負責輸出 HTML，不應塞滿複雜商業邏輯</li>
            <li>若部分資料要改成動態刷新，可再逐步接 jQuery AJAX 或 Axios</li>
        </ul>
    </div>
</body>
</html>