-- ==================================================================================
-- Database Initialization Script for Rental Management System
-- Modules and Role Permissions
-- Schema: rental_management
-- Dialect: PostgreSQL
-- ==================================================================================

-- 1. Insert Default Modules
-- These keys must match what is used in the frontend PermissionGuard (e.g., USER_MANAGEMENT)

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
('Inventory Management', 'INVENTORY_MANAGEMENT', 'Manage product inventory', NOW())
ON CONFLICT (module_key) DO NOTHING;

-- 2. Ensure ADMIN Role Exists (if not already present)
INSERT INTO rental_management.role (role_name, description, created_at)
VALUES ('ADMIN', 'Administrator with full access', NOW())
ON CONFLICT (role_name) DO NOTHING;

-- 3. Grant Full Access to ADMIN for All Modules
-- This script finds the ADMIN role ID and iterates through all modules to grant full permissions.

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

-- 4. Insert USER Role (Optional default role)
INSERT INTO rental_management.role (role_name, description, created_at)
VALUES ('USER', 'Standard user with limited access', NOW())
ON CONFLICT (role_name) DO NOTHING;

-- 5. Grant Basic Read Access to USER for Products (Example)
DO $$
DECLARE
    user_role_id INTEGER;
    product_module_id INTEGER;
BEGIN
    SELECT role_id INTO user_role_id FROM rental_management.role WHERE role_name = 'USER';
    SELECT module_id INTO product_module_id FROM rental_management.module WHERE module_key = 'PRODUCT_MANAGEMENT';

    IF user_role_id IS NOT NULL AND product_module_id IS NOT NULL THEN
        IF NOT EXISTS (
            SELECT 1 FROM rental_management.role_permission 
            WHERE role_id = user_role_id AND module_id = product_module_id
        ) THEN
            INSERT INTO rental_management.role_permission 
            (role_id, module_id, can_create, can_read, can_update, can_delete, created_at)
            VALUES 
            (user_role_id, product_module_id, false, true, false, false, NOW());
        END IF;
    END IF;
END $$;
