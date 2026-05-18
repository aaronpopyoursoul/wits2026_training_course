package com.company.training.auditdemo.dto;

public class PolicyInquiryResponse {

    private final String policyNo;
    private final String policyHolderName;
    private final String insuredIdMasked;
    private final String productCode;
    private final String policyStatus;
    private final String nextPremiumDueDate;

    public PolicyInquiryResponse(String policyNo,
                                 String policyHolderName,
                                 String insuredIdMasked,
                                 String productCode,
                                 String policyStatus,
                                 String nextPremiumDueDate) {
        this.policyNo = policyNo;
        this.policyHolderName = policyHolderName;
        this.insuredIdMasked = insuredIdMasked;
        this.productCode = productCode;
        this.policyStatus = policyStatus;
        this.nextPremiumDueDate = nextPremiumDueDate;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public String getPolicyHolderName() {
        return policyHolderName;
    }

    public String getInsuredIdMasked() {
        return insuredIdMasked;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getPolicyStatus() {
        return policyStatus;
    }

    public String getNextPremiumDueDate() {
        return nextPremiumDueDate;
    }
}