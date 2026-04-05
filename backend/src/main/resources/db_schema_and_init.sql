-- ==================================================================================
-- Database Schema Creation Script for Rental Management System
-- Tables: module, role_permission (and core tables if missing)
-- Schema: rental_management
-- Dialect: PostgreSQL
-- ==================================================================================

-- Ensure schema exists
CREATE SCHEMA IF NOT EXISTS rental_management;

-- 1. Create Role Table (if not exists)
CREATE TABLE IF NOT EXISTS rental_management.role (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 2. Create App User Table (if not exists)
CREATE TABLE IF NOT EXISTS rental_management.app_user (
    user_id SERIAL PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE,
    contact_number VARCHAR(20),
    password TEXT NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    role_id INTEGER REFERENCES rental_management.role(role_id),
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(100),
    updated_at TIMESTAMP
);

-- 3. Create Module Table
CREATE TABLE IF NOT EXISTS rental_management.module (
    module_id SERIAL PRIMARY KEY,
    module_name VARCHAR(100) NOT NULL,
    module_key VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 4. Create Role Permission Table
CREATE TABLE IF NOT EXISTS rental_management.role_permission (
    permission_id SERIAL PRIMARY KEY,
    role_id INTEGER NOT NULL REFERENCES rental_management.role(role_id) ON DELETE CASCADE,
    module_id INTEGER NOT NULL REFERENCES rental_management.module(module_id) ON DELETE CASCADE,
    can_create BOOLEAN DEFAULT FALSE,
    can_read BOOLEAN DEFAULT FALSE,
    can_update BOOLEAN DEFAULT FALSE,
    can_delete BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_role_module UNIQUE (role_id, module_id)
);

-- ==================================================================================
-- Data Initialization
-- ==================================================================================

-- 5. Insert Default Modules
INSERT INTO rental_management.module (module_name, module_key, description, created_at)
VALUES 
('Dashboard', 'DASHBOARD', 'Access to dashboard statistics and overview', NOW()),
('User Management', 'USER_MANAGEMENT', 'Manage system users', NOW()),
('Role Management', 'ROLE_MANAGEMENT', 'Manage user roles', NOW()),
('Role Access Management', 'ROLE_ACCESS_MANAGEMENT', 'Manage permissions for roles', NOW()),
('Module Management', 'MODULE_MANAGEMENT', 'Manage system modules', NOW()),
('Product Management', 'PRODUCT_MANAGEMENT', 'Manage rental products', NOW()),
('Customer Management', 'CUSTOMER_MANAGEMENT', 'Manage customers', NOW()),
('Booking Management', 'BOOKING_MANAGEMENT', 'Manage bookings and orders', NOW()),
('Transaction Management', 'TRANSACTION_MANAGEMENT', 'View and manage transactions', NOW()),
('Inventory Management', 'INVENTORY_MANAGEMENT', 'Manage product inventory', NOW()),
('Shop & Branch Management', 'SHOP_BRANCH_MANAGEMENT', 'Manage shop and branch masters', NOW())
ON CONFLICT (module_key) DO NOTHING;

-- 6. Ensure ADMIN Role Exists
INSERT INTO rental_management.role (role_name, description, created_at)
VALUES ('ADMIN', 'Administrator with full access', NOW())
ON CONFLICT (role_name) DO NOTHING;

-- 7. Grant Full Access to ADMIN for All Modules
DO $$
DECLARE
    admin_role_id INTEGER;
    mod_record RECORD;
BEGIN
    -- Get ADMIN role ID
    SELECT role_id INTO admin_role_id FROM rental_management.role WHERE role_name = 'ADMIN';

    IF admin_role_id IS NOT NULL THEN
        -- Loop through all modules
        FOR mod_record IN SELECT module_id FROM rental_management.module LOOP
            
            -- Insert permission if it doesn't exist
            IF NOT EXISTS (
                SELECT 1 FROM rental_management.role_permission 
                WHERE role_id = admin_role_id AND module_id = mod_record.module_id
            ) THEN
                INSERT INTO rental_management.role_permission 
                (role_id, module_id, can_create, can_read, can_update, can_delete, created_at)
                VALUES 
                (admin_role_id, mod_record.module_id, true, true, true, true, NOW());
            ELSE
                -- Update existing permission to true
                UPDATE rental_management.role_permission
                SET can_create = true, can_read = true, can_update = true, can_delete = true
                WHERE role_id = admin_role_id AND module_id = mod_record.module_id;
            END IF;
            
        END LOOP;
    END IF;
END $$;

-- 8. Insert USER Role
INSERT INTO rental_management.role (role_name, description, created_at)
VALUES ('USER', 'Standard user with limited access', NOW())
ON CONFLICT (role_name) DO NOTHING;
