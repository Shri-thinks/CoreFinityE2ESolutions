-- Core Banking & Fintech Database Schema Initialization

CREATE TABLE IF NOT EXISTS customers (
    id VARCHAR(64) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    kyc_status VARCHAR(30) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS accounts (
    id VARCHAR(64) PRIMARY KEY,
    customer_id VARCHAR(64) REFERENCES customers(id) ON DELETE CASCADE,
    account_number VARCHAR(34) UNIQUE NOT NULL,
    account_type VARCHAR(30) DEFAULT 'CHECKING',
    available_balance NUMERIC(15, 2) DEFAULT 0.00,
    hold_balance NUMERIC(15, 2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'USD',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cards (
    id VARCHAR(64) PRIMARY KEY,
    account_id VARCHAR(64) REFERENCES accounts(id) ON DELETE CASCADE,
    card_number_masked VARCHAR(20) NOT NULL,
    card_token VARCHAR(128) UNIQUE NOT NULL,
    card_type VARCHAR(20) DEFAULT 'VIRTUAL', -- VIRTUAL or PHYSICAL
    status VARCHAR(30) DEFAULT 'PENDING_ACTIVATION', -- PENDING_ACTIVATION, ACTIVE, FROZEN, TERMINATED
    pin_hash VARCHAR(128),
    daily_limit NUMERIC(10, 2) DEFAULT 1000.00,
    expiry_date VARCHAR(7) NOT NULL, -- MM/YYYY
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS authorizations (
    id VARCHAR(64) PRIMARY KEY,
    card_id VARCHAR(64) REFERENCES cards(id),
    auth_code VARCHAR(32) NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    merchant_name VARCHAR(150) NOT NULL,
    status VARCHAR(30) NOT NULL, -- APPROVED, DECLINED, REVERSED
    response_code VARCHAR(10) DEFAULT '00',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS double_entry_ledger (
    id VARCHAR(64) PRIMARY KEY,
    transaction_id VARCHAR(64) NOT NULL,
    account_id VARCHAR(64) REFERENCES accounts(id),
    entry_type VARCHAR(10) NOT NULL, -- DEBIT or CREDIT
    amount NUMERIC(15, 2) NOT NULL,
    balance_after NUMERIC(15, 2) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) DEFAULT 'POSTED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed Initial Test Customer & Account
INSERT INTO customers (id, first_name, last_name, email, kyc_status)
VALUES ('CUST-DEMO-001', 'John', 'Doe', 'john.doe@fintechqa.com', 'VERIFIED')
ON CONFLICT (id) DO NOTHING;

INSERT INTO accounts (id, customer_id, account_number, account_type, available_balance, hold_balance, currency)
VALUES ('ACC-DEMO-001', 'CUST-DEMO-001', 'ACCT9876543210', 'CHECKING', 5000.00, 0.00, 'USD')
ON CONFLICT (id) DO NOTHING;
