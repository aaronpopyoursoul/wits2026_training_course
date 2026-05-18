package com.company.training.jdbcdemo.service;

import org.springframework.stereotype.Service;

import com.company.training.jdbcdemo.mapper.PolicyQueryMapper;
import com.company.training.jdbcdemo.model.PolicyFinancialSummary;

@Service
public class PolicyQueryService {

    private final PolicyQueryMapper policyQueryMapper;

    public PolicyQueryService(PolicyQueryMapper policyQueryMapper) {
        this.policyQueryMapper = policyQueryMapper;
    }

    public PolicyFinancialSummary getPolicyFinancialSummary(String policyNo) {
        return policyQueryMapper.findPolicyFinancialSummary(policyNo);
    }
}