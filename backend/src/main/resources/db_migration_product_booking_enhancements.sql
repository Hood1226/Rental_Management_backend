-- Product, booking, shop/branch and sequence enhancements

CREATE TABLE IF NOT EXISTS rental_management.sequence_counter (
    counter_id SERIAL PRIMARY KEY,
    sequence_key VARCHAR(100) NOT NULL UNIQUE,
    last_number INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS rental_management.shop_master (
    shop_id SERIAL PRIMARY KEY,
    shop_name VARCHAR(150) NOT NULL,
    shop_code VARCHAR(30) NOT NULL UNIQUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS rental_management.branch_master (
    branch_id SERIAL PRIMARY KEY,
    shop_id INTEGER NOT NULL REFERENCES rental_management.shop_master(shop_id),
    branch_name VARCHAR(150) NOT NULL,
    branch_code VARCHAR(30) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT uk_branch_code_per_shop UNIQUE (shop_id, branch_code)
);

ALTER TABLE rental_management.product
    ADD COLUMN IF NOT EXISTS product_code VARCHAR(30),
    ADD COLUMN IF NOT EXISTS discount_percent NUMERIC(5,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS max_discount_percent NUMERIC(5,2) DEFAULT 0;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'uk_product_product_code'
    ) THEN
        ALTER TABLE rental_management.product
            ADD CONSTRAINT uk_product_product_code UNIQUE (product_code);
    END IF;
END $$;

ALTER TABLE rental_management.booking
    ADD COLUMN IF NOT EXISTS booking_no VARCHAR(30),
    ADD COLUMN IF NOT EXISTS shop_id INTEGER,
    ADD COLUMN IF NOT EXISTS branch_id INTEGER;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'uk_booking_booking_no'
    ) THEN
        ALTER TABLE rental_management.booking
            ADD CONSTRAINT uk_booking_booking_no UNIQUE (booking_no);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_booking_shop'
    ) THEN
        ALTER TABLE rental_management.booking
            ADD CONSTRAINT fk_booking_shop FOREIGN KEY (shop_id)
            REFERENCES rental_management.shop_master(shop_id);
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_booking_branch'
    ) THEN
        ALTER TABLE rental_management.booking
            ADD CONSTRAINT fk_booking_branch FOREIGN KEY (branch_id)
            REFERENCES rental_management.branch_master(branch_id);
    END IF;
END $$;

ALTER TABLE rental_management.booking_item
    ADD COLUMN IF NOT EXISTS discount_percent NUMERIC(5,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS discount_amount NUMERIC(12,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS final_unit_price NUMERIC(10,2) DEFAULT 0;

INSERT INTO rental_management.module (module_name, module_key, description, created_at)
VALUES ('Shop & Branch Management', 'SHOP_BRANCH_MANAGEMENT', 'Manage shop and branch masters', NOW())
ON CONFLICT (module_key) DO NOTHING;
