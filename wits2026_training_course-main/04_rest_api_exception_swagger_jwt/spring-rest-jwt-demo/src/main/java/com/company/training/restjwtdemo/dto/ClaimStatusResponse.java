package com.company.training.restjwtdemo.dto;

public class ClaimStatusResponse {

    private final String claimNo;
    private final String status;
    private final String assignedAdjuster;

    public ClaimStatusResponse(String claimNo, String status, String assignedAdjuster) {
        this.claimNo = claimNo;
        this.status = status;
        this.assignedAdjuster = assignedAdjuster;
    }

    public String getClaimNo() {
        return claimNo;
    }

    public String getStatus() {
        return status;
    }

    public String getAssignedAdjuster() {
        return assignedAdjuster;
    }
}