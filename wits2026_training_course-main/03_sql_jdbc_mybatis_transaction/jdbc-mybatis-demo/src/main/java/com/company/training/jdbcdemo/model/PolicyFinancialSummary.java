package com.company.training.jdbcdemo.model;

public class PolicyFinancialSummary {

    private String policyNo;
    private String policyHolderName;
    private String policyStatus;
    private String productCode;
    private String lastPaymentDate;
    private Integer openClaimCount;

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public String getPolicyHolderName() {
        return policyHolderName;
    }

    public void setPolicyHolderName(String policyHolderName) {
        this.policyHolderName = policyHolderName;
    }

    public String getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(String policyStatus) {
        this.policyStatus = policyStatus;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(String lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public Integer getOpenClaimCount() {
        return openClaimCount;
    }

    public void setOpenClaimCount(Integer openClaimCount) {
        this.openClaimCount = openClaimCount;
    }
}