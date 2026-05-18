package com.company.training.jdbcdemo.mapper;

import org.apache.ibatis.annotations.Param;

import com.company.training.jdbcdemo.model.PolicyFinancialSummary;

public interface PolicyQueryMapper {

    PolicyFinancialSummary findPolicyFinancialSummary(@Param("policyNo") String policyNo);
}