insert into policy_holder (id, holder_name, insured_id_masked) values
(1, '陳小美', 'A12*****89');

insert into policy (policy_no, holder_id, policy_status, product_code, next_premium_due_date) values
('POL20260001', 1, 'ACTIVE', 'LIFE-20Y-ENDOWMENT', '2026-06-15');

insert into payment_record (id, policy_no, payment_date, amount) values
(1, 'POL20260001', '2026-03-15', 3200.00),
(2, 'POL20260001', '2026-04-15', 3200.00),
(3, 'POL20260001', '2026-05-15', 3200.00);

insert into claim_case (claim_no, policy_no, claim_status, approved_amount, approved_by, approved_at) values
('CLM20260511001', 'POL20260001', 'PENDING', 2500.00, null, null),
('CLM20260511002', 'POL20260001', 'UNDER_REVIEW', 1800.00, null, null);