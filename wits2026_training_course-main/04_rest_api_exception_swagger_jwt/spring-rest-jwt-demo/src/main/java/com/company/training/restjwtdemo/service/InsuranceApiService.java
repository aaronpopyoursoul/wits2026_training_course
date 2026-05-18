package com.company.training.restjwtdemo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.company.training.restjwtdemo.dto.ClaimStatusResponse;
import com.company.training.restjwtdemo.dto.PolicySummaryResponse;
import com.company.training.restjwtdemo.exception.BusinessException;

@Service
public class InsuranceApiService {

    private final Map<String, PolicySummaryResponse> policies = new HashMap<String, PolicySummaryResponse>();
    private final Map<String, ClaimStatusResponse> claims = new HashMap<String, ClaimStatusResponse>();

    public InsuranceApiService() {
        policies.put("POL20260001", new PolicySummaryResponse("POL20260001", "陳小美", "LIFE-20Y-ENDOWMENT", "ACTIVE"));
        claims.put("CLM20260511001", new ClaimStatusResponse("CLM20260511001", "UNDER_REVIEW", "adjuster.lin"));
    }

    public PolicySummaryResponse getPolicySummary(String policyNo) {
        PolicySummaryResponse response = policies.get(policyNo);
        if (response == null) {
            throw new BusinessException("POLICY_NOT_FOUND", "Policy not found: " + policyNo);
        }
        return response;
    }

    public ClaimStatusResponse getClaimStatus(String claimNo) {
        ClaimStatusResponse response = claims.get(claimNo);
        if (response == null) {
            throw new BusinessException("CLAIM_NOT_FOUND", "Claim not found: " + claimNo);
        }
        return response;
    }
}