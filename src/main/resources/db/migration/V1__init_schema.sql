CREATE TABLE users(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(35) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE accounts(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) NOT NULL,
    account_no VARCHAR(50) NOT NULL UNIQUE,
    balance NUMERIC(19,4) NOT NULL DEFAULT 0 CHECK (balance >= 0),
    currency VARCHAR(35) NOT NULL DEFAULT 'EUR',
    version INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE transactions(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_account_id UUID REFERENCES users(id),
    to_account_id UUID REFERENCES users(id),
    amount NUMERIC(19,4) NOT NULL CHECK ( amount > 0 ),
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    reference VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL
);