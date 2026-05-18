package com.company.training.jdbcdemo.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClaimLedgerJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public ClaimLedgerJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int markClaimApproved(String claimNo, String approvedBy) {
        return jdbcTemplate.update(
            "update claim_case set claim_status = ?, approved_by = ?, approved_at = ? where claim_no = ?",
            "APPROVED",
            approvedBy,
            Timestamp.valueOf(LocalDateTime.now()),
            claimNo
        );
    }

    public int insertLedger(String claimNo, double amount) {
        return jdbcTemplate.update(
            "insert into claim_ledger (claim_no, ledger_type, amount, created_at) values (?, ?, ?, ?)",
            claimNo,
            "CLAIM_APPROVAL",
            amount,
            Timestamp.valueOf(LocalDateTime.now())
        );
    }

    public String findClaimStatus(String claimNo) {
        return jdbcTemplate.queryForObject(
            "select claim_status from claim_case where claim_no = ?",
            String.class,
            claimNo
        );
    }

    public int countLedgerEntries(String claimNo) {
        Integer count = jdbcTemplate.queryForObject(
            "select count(*) from claim_ledger where claim_no = ?",
            Integer.class,
            claimNo
        );
        return count == null ? 0 : count.intValue();
    }
}