package com.company.training.restjwtdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.training.restjwtdemo.dto.ClaimStatusResponse;
import com.company.training.restjwtdemo.dto.PolicySummaryResponse;
import com.company.training.restjwtdemo.service.InsuranceApiService;

@RestController
@RequestMapping("/api")
public class InsuranceApiController {

    private final InsuranceApiService insuranceApiService;

    public InsuranceApiController(InsuranceApiService insuranceApiService) {
        this.insuranceApiService = insuranceApiService;
    }

    @GetMapping("/policies/{policyNo}/summary")
    public PolicySummaryResponse getPolicySummary(@PathVariable String policyNo) {
        return insuranceApiService.getPolicySummary(policyNo);
    }

    @GetMapping("/claims/{claimNo}/status")
    public ClaimStatusResponse getClaimStatus(@PathVariable String claimNo) {
        return insuranceApiService.getClaimStatus(claimNo);
    }
}