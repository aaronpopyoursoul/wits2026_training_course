package com.company.training.auditdemo.dto;

public class UnderwritingReviewResponse {

    private final String applicationNo;
    private final String applicantNameMasked;
    private final String reviewDecision;
    private final String queueName;
    private final String lastReviewedAt;

    public UnderwritingReviewResponse(String applicationNo,
                                      String applicantNameMasked,
                                      String reviewDecision,
                                      String queueName,
                                      String lastReviewedAt) {
        this.applicationNo = applicationNo;
        this.applicantNameMasked = applicantNameMasked;
        this.reviewDecision = reviewDecision;
        this.queueName = queueName;
        this.lastReviewedAt = lastReviewedAt;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public String getApplicantNameMasked() {
        return applicantNameMasked;
    }

    public String getReviewDecision() {
        return reviewDecision;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getLastReviewedAt() {
        return lastReviewedAt;
    }
}