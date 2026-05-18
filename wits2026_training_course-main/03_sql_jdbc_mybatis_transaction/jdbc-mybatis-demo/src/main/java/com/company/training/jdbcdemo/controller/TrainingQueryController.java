package com.company.training.jdbcdemo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.company.training.jdbcdemo.dto.ClaimApprovalResponse;
import com.company.training.jdbcdemo.dto.PolicyFinancialSummaryResponse;
import com.company.training.jdbcdemo.service.ClaimProcessingService;
import com.company.training.jdbcdemo.service.PolicyQueryService;

@RestController
@RequestMapping("/api")
public class TrainingQueryController {

    private final PolicyQueryService policyQueryService;
    private final ClaimProcessingService claimProcessingService;

    public TrainingQueryController(PolicyQueryService policyQueryService,
                                   ClaimProcessingService claimProcessingService) {
        this.policyQueryService = policyQueryService;
        this.claimProcessingService = claimProcessingService;
    }

    @GetMapping("/policies/{policyNo}/financial-summary")
    public PolicyFinancialSummaryResponse getPolicyFinancialSummary(@PathVariable String policyNo) {
        return new PolicyFinancialSummaryResponse(policyQueryService.getPolicyFinancialSummary(policyNo));
    }

    @PostMapping("/claims/{claimNo}/approve")
    @ResponseStatus(HttpStatus.OK)
    public ClaimApprovalResponse approveClaim(@PathVariable String claimNo,
                                              @RequestParam String approvedBy,
                                              @RequestParam(defaultValue = "false") boolean failAfterLedger) {
        return claimProcessingService.approveClaim(claimNo, approvedBy, failAfterLedger);
    }
}