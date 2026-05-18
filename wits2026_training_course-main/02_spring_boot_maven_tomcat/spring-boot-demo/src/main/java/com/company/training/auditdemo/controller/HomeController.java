package com.company.training.auditdemo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.company.training.auditdemo.dto.ClaimStatusResponse;
import com.company.training.auditdemo.dto.HealthResponse;
import com.company.training.auditdemo.dto.PolicyInquiryResponse;
import com.company.training.auditdemo.dto.SystemMessageResponse;
import com.company.training.auditdemo.dto.UnderwritingReviewResponse;

@RestController
public class HomeController {

    @GetMapping("/")
    public SystemMessageResponse home() {
        return new SystemMessageResponse(
            "Insurance training app is running",
            "policy-inquiry / claims / underwriting",
            LocalDateTime.now().toString(),
            "audit-log-ready"
        );
    }

    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse("UP", "training-audit-demo", LocalDateTime.now().toString());
    }

    @GetMapping("/policies/{policyNo}/summary")
    public PolicyInquiryResponse queryPolicy(@PathVariable String policyNo) {
        return new PolicyInquiryResponse(
            policyNo,
            "陳小美",
            "A12*****89",
            "LIFE-20Y-ENDOWMENT",
            "ACTIVE",
            LocalDate.now().plusDays(12).toString()
        );
    }

    @GetMapping("/claims/{claimNo}/status")
    public ClaimStatusResponse queryClaim(@PathVariable String claimNo) {
        return new ClaimStatusResponse(
            claimNo,
            "POL20260001",
            "UNDER_REVIEW",
            "adjuster.lin",
            LocalDateTime.now().minusHours(3).toString()
        );
    }

    @GetMapping("/underwriting/applications/{applicationNo}/review")
    public UnderwritingReviewResponse queryUnderwriting(@PathVariable String applicationNo) {
        return new UnderwritingReviewResponse(
            applicationNo,
            "王O傑",
            "PENDING_SECOND_REVIEW",
            "underwriting-high-risk-queue",
            LocalDateTime.now().minusMinutes(45).toString()
        );
    }
}
