package com.company.training.auditdemo.dto;

public class ClaimStatusResponse {

    private final String claimNo;
    private final String policyNo;
    private final String claimStatus;
    private final String assignedAdjuster;
    private final String updatedAt;

    public ClaimStatusResponse(String claimNo,
                               String policyNo,
                               String claimStatus,
                               String assignedAdjuster,
                               String updatedAt) {
        this.claimNo = claimNo;
        this.policyNo = policyNo;
        this.claimStatus = claimStatus;
        this.assignedAdjuster = assignedAdjuster;
        this.updatedAt = updatedAt;
    }

    public String getClaimNo() {
        return claimNo;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public String getAssignedAdjuster() {
        return assignedAdjuster;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}