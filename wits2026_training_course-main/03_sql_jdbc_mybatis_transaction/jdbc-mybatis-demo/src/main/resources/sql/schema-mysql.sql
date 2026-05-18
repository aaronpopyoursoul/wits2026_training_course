create table if not exists policy_holder (
    id bigint primary key,
    holder_name varchar(50) not null,
    insured_id_masked varchar(20) not null
);

create table if not exists policy (
    policy_no varchar(20) primary key,
    holder_id bigint not null,
    policy_status varchar(30) not null,
    product_code varchar(50) not null,
    next_premium_due_date date not null,
    constraint fk_policy_holder foreign key (holder_id) references policy_holder(id)
);

create table if not exists payment_record (
    id bigint primary key,
    policy_no varchar(20) not null,
    payment_date date not null,
    amount decimal(12, 2) not null,
    constraint fk_payment_policy foreign key (policy_no) references policy(policy_no)
);

create table if not exists claim_case (
    claim_no varchar(20) primary key,
    policy_no varchar(20) not null,
    claim_status varchar(30) not null,
    approved_amount decimal(12, 2) not null,
    approved_by varchar(50),
    approved_at timestamp null,
    constraint fk_claim_policy foreign key (policy_no) references policy(policy_no)
);

create table if not exists claim_ledger (
    id bigint auto_increment primary key,
    claim_no varchar(20) not null,
    ledger_type varchar(40) not null,
    amount decimal(12, 2) not null,
    created_at timestamp not null,
    constraint fk_ledger_claim foreign key (claim_no) references claim_case(claim_no)
);