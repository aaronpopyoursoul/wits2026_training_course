package com.company.training.jdbcdemo.dto;

public class ClaimApprovalResponse {

    private final String claimNo;
    private final String claimStatus;
    private final String approvedBy;
    private final String transactionResult;

    public ClaimApprovalResponse(String claimNo, String claimStatus, String approvedBy, String transactionResult) {
        this.claimNo = claimNo;
        this.claimStatus = claimStatus;
        this.approvedBy = approvedBy;
        this.transactionResult = transactionResult;
    }

    public String getClaimNo() {
        return claimNo;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public String getTransactionResult() {
        return transactionResult;
    }
}