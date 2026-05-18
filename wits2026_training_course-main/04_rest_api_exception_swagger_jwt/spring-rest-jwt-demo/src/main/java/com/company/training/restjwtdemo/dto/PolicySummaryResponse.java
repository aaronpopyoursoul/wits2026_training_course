package com.company.training.restjwtdemo.dto;

public class PolicySummaryResponse {

    private final String policyNo;
    private final String policyHolderName;
    private final String productCode;
    private final String policyStatus;

    public PolicySummaryResponse(String policyNo, String policyHolderName, String productCode, String policyStatus) {
        this.policyNo = policyNo;
        this.policyHolderName = policyHolderName;
        this.productCode = productCode;
        this.policyStatus = policyStatus;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public String getPolicyHolderName() {
        return policyHolderName;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getPolicyStatus() {
        return policyStatus;
    }
}