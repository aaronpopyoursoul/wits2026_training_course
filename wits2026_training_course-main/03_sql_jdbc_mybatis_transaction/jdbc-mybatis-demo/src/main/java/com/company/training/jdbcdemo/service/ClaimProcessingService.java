package com.company.training.jdbcdemo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.training.jdbcdemo.dto.ClaimApprovalResponse;
import com.company.training.jdbcdemo.repository.ClaimLedgerJdbcRepository;

@Service
public class ClaimProcessingService {

    private final ClaimLedgerJdbcRepository claimLedgerJdbcRepository;

    public ClaimProcessingService(ClaimLedgerJdbcRepository claimLedgerJdbcRepository) {
        this.claimLedgerJdbcRepository = claimLedgerJdbcRepository;
    }

    @Transactional
    public ClaimApprovalResponse approveClaim(String claimNo, String approvedBy, boolean failAfterLedger) {
        claimLedgerJdbcRepository.markClaimApproved(claimNo, approvedBy);
        claimLedgerJdbcRepository.insertLedger(claimNo, 2500.00D);

        if (failAfterLedger) {
            throw new IllegalStateException("Simulated failure after ledger insert for transaction rollback demo");
        }

        return new ClaimApprovalResponse(claimNo, "APPROVED", approvedBy, "COMMITTED");
    }
}