# Automatic Inventory Update Feature

## Overview

When a booking is created, the system automatically:
1. **Validates inventory availability** before creating the booking
2. **Reduces inventory quantity** based on booking items
3. **Updates availability status** based on booking type and remaining quantity
4. **Creates inventory transactions** automatically

## How It Works

### Booking Creation Flow

```
Create Booking Request
    ↓
Validate Customer
    ↓
Create Booking
    ↓
For each Booking Item:
    ├─ Create BookingItem
    ├─ Check Inventory Availability
    ├─ Reduce Available Quantity
    ├─ Update Availability Status
    └─ Create Inventory Transaction
```

### Inventory Status Logic

#### For RENT Bookings:
- **Quantity becomes 0**: Status → `RENTED`
- **Quantity > 0**: Status → `PARTIALLY_RENTED`
- Sets `expectedRestoreDate` and `nextAvailabilityDate` to rental end date

#### For SALE Bookings:
- **Quantity becomes 0**: Status → `SOLD`
- **Quantity > 0**: Status → `PARTIALLY_SOLD`

#### Default (if booking type not specified):
- **Quantity becomes 0**: Status → `UNAVAILABLE`
- **Quantity > 0**: Status → `AVAILABLE`

### Inventory Transaction Types

Automatically created transactions:
- **RENT bookings**: Transaction type = `RENT_OUT`
- **SALE bookings**: Transaction type = `SALE`
- Status = `ACTIVE`
- Includes expected return date for RENT bookings

## Example Scenarios

### Scenario 1: RENT Booking - Full Inventory

**Before:**
- Available Quantity: 5
- Status: AVAILABLE

**Booking Created:**
- Quantity Requested: 5
- Booking Type: RENT

**After:**
- Available Quantity: 0
- Status: RENTED
- Transaction Created: RENT_OUT (quantity: 5)
- Expected Restore Date: Set to rental end date

### Scenario 2: SALE Booking - Partial Inventory

**Before:**
- Available Quantity: 10
- Status: AVAILABLE

**Booking Created:**
- Quantity Requested: 3
- Booking Type: SALE

**After:**
- Available Quantity: 7
- Status: PARTIALLY_SOLD
- Transaction Created: SALE (quantity: 3)

### Scenario 3: Insufficient Inventory

**Before:**
- Available Quantity: 2
- Status: AVAILABLE

**Booking Request:**
- Quantity Requested: 5
- Booking Type: RENT

**Result:**
- **Error**: `InsufficientInventoryException`
- **Message**: "Insufficient inventory for product 'Wedding Dress' (Size: S). Available: 2, Requested: 5"
- Booking is **NOT created**
- Inventory remains unchanged

## API Usage

### Create Booking (Automatic Inventory Update)

```json
POST /api/bookings
{
  "customerId": 1,
  "bookingType": "RENT",
  "status": "PENDING",
  "items": [
    {
      "variantId": 1,
      "quantity": 2,
      "unitPrice": 5000.00,
      "rentalStart": "2024-01-15",
      "rentalEnd": "2024-01-20"
    }
  ]
}
```

**Response includes automatically created transaction:**
```json
{
  "success": true,
  "data": {
    "bookingId": 1,
    "items": [...],
    "transactions": [
      {
        "transactionId": 1,
        "variantId": 1,
        "transactionType": "RENT_OUT",
        "quantity": 2,
        "status": "ACTIVE",
        "expectedReturnDate": "2024-01-20",
        "notes": "Auto-generated transaction for booking ID: 1"
      }
    ]
  }
}
```

## Error Handling

### InsufficientInventoryException

**HTTP Status:** `400 Bad Request`

**Response:**
```json
{
  "success": false,
  "message": "Insufficient inventory for product 'Wedding Dress' (Size: S). Available: 2, Requested: 5",
  "data": null
}
```

## Inventory Status Values

- `AVAILABLE` - Items available for booking
- `PARTIALLY_RENTED` - Some items rented, some still available
- `PARTIALLY_SOLD` - Some items sold, some still available
- `RENTED` - All items rented out
- `SOLD` - All items sold
- `UNAVAILABLE` - No items available (default when quantity = 0)

## Notes

1. **Automatic Transactions**: Inventory transactions are created automatically - you don't need to include them in the request
2. **Manual Transactions**: You can still add additional transactions via the `transactions` array in the request
3. **Transaction Safety**: All operations are transactional - if inventory update fails, booking creation is rolled back
4. **Status Priority**: Manual status settings (RENTED/SOLD) take precedence over auto-update logic
5. **Return Handling**: When items are returned (via RETURN transaction), inventory should be updated separately

## Future Enhancements

- Return transaction handling to restore inventory
- Bulk booking support
- Inventory reservation system
- Low stock alerts

