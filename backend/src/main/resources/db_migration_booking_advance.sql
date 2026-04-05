-- Add advance booking fields to booking table (run once)
-- Schema: rental_management (adjust if your schema name differs)

ALTER TABLE rental_management.booking
  ADD COLUMN IF NOT EXISTS is_advance_booking BOOLEAN DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS scheduled_date DATE,
  ADD COLUMN IF NOT EXISTS advance_payment_amount NUMERIC(12,2) DEFAULT 0;

COMMENT ON COLUMN rental_management.booking.is_advance_booking IS 'Whether this is an advance/scheduled booking';
COMMENT ON COLUMN rental_management.booking.scheduled_date IS 'Scheduled date for advance bookings';
COMMENT ON COLUMN rental_management.booking.advance_payment_amount IS 'Amount paid in advance for advance bookings';
