package com.company.training.jdbcdemo.dto;

import com.company.training.jdbcdemo.model.PolicyFinancialSummary;

public class PolicyFinancialSummaryResponse {

    private final String policyNo;
    private final String policyHolderName;
    private final String policyStatus;
    private final String productCode;
    private final String lastPaymentDate;
    private final Integer openClaimCount;

    public PolicyFinancialSummaryResponse(PolicyFinancialSummary summary) {
        this.policyNo = summary.getPolicyNo();
        this.policyHolderName = summary.getPolicyHolderName();
        this.policyStatus = summary.getPolicyStatus();
        this.productCode = summary.getProductCode();
        this.lastPaymentDate = summary.getLastPaymentDate();
        this.openClaimCount = summary.getOpenClaimCount();
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public String getPolicyHolderName() {
        return policyHolderName;
    }

    public String getPolicyStatus() {
        return policyStatus;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getLastPaymentDate() {
        return lastPaymentDate;
    }

    public Integer getOpenClaimCount() {
        return openClaimCount;
    }
}