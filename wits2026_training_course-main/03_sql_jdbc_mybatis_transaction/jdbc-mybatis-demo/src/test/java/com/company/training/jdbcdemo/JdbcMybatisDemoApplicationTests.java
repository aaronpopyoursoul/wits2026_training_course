package com.company.training.jdbcdemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.company.training.jdbcdemo.model.PolicyFinancialSummary;
import com.company.training.jdbcdemo.repository.ClaimLedgerJdbcRepository;
import com.company.training.jdbcdemo.service.ClaimProcessingService;
import com.company.training.jdbcdemo.service.PolicyQueryService;

@SpringBootTest
class JdbcMybatisDemoApplicationTests {

    @Autowired
    private PolicyQueryService policyQueryService;

    @Autowired
    private ClaimProcessingService claimProcessingService;

    @Autowired
    private ClaimLedgerJdbcRepository claimLedgerJdbcRepository;

    @Test
    void shouldLoadPolicyFinancialSummary() {
        PolicyFinancialSummary summary = policyQueryService.getPolicyFinancialSummary("POL20260001");

        assertNotNull(summary);
        assertEquals("陳小美", summary.getPolicyHolderName());
        assertEquals(Integer.valueOf(2), summary.getOpenClaimCount());
    }

    @Test
    void shouldRollbackClaimApprovalWhenFailureOccurs() {
        assertThrows(IllegalStateException.class, () ->
            claimProcessingService.approveClaim("CLM20260511001", "trainer", true)
        );

        assertEquals("PENDING", claimLedgerJdbcRepository.findClaimStatus("CLM20260511001"));
        assertEquals(0, claimLedgerJdbcRepository.countLedgerEntries("CLM20260511001"));
    }
}