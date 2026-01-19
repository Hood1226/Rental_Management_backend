# Complete Data Structure Guide

## Entity Relationships

### Product Hierarchy
```
Product
├── ProductVariant (many)
│   ├── ProductSize (one)
│   ├── RentPrice (many - historical pricing)
│   ├── SalePrice (many - historical pricing)
│   └── Inventory (one-to-one)
```

### Booking Hierarchy
```
Booking
├── BookingItem (many)
│   └── ProductVariant (reference)
├── InventoryTransaction (many)
│   ├── ProductVariant (reference)
│   └── DamageRecord (many - if transaction type is DAMAGE)
```

## Complete API Request/Response Formats

### Product with Complete Structure

**Request:**
```json
{
  "productName": "Wedding Dress",
  "category": "Clothing",
  "description": "Beautiful white wedding dress",
  "depositAmount": 5000.00,
  "isForSale": false,
  "isForRent": true,
  "isActive": true,
  "variants": [
    {
      "sizeId": 1,
      "purchasePrice": 15000.00,
      "rentPrice": 5000.00,
      "rentEffectiveFrom": "2024-01-01",
      "rentEffectiveTo": null,
      "salePrice": null,
      "saleEffectiveFrom": null,
      "saleEffectiveTo": null,
      "availableQuantity": 5,
      "availabilityStatus": "AVAILABLE",
      "expectedRestoreDate": null,
      "nextAvailabilityDate": null
    }
  ]
}
```

**Response:**
```json
{
  "productId": 1,
  "productName": "Wedding Dress",
  "category": "Clothing",
  "variants": [
    {
      "variantId": 1,
      "sizeId": 1,
      "sizeCode": "S",
      "purchasePrice": 15000.00,
      "rentPrice": 5000.00,
      "rentEffectiveFrom": "2024-01-01",
      "rentEffectiveTo": null,
      "salePrice": null,
      "inventoryId": 1,
      "availableQuantity": 5,
      "availabilityStatus": "AVAILABLE"
    }
  ]
}
```

### Booking with Complete Structure

**Request:**
```json
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
  ],
  "transactions": [
    {
      "variantId": 1,
      "transactionType": "RENT_OUT",
      "quantity": 2,
      "expectedReturnDate": "2024-01-20",
      "status": "ACTIVE"
    }
  ]
}
```

**Response:**
```json
{
  "bookingId": 1,
  "customerId": 1,
  "customerName": "John Doe",
  "bookingType": "RENT",
  "status": "PENDING",
  "totalAmount": 10000.00,
  "items": [
    {
      "bookingItemId": 1,
      "variantId": 1,
      "productId": 1,
      "productName": "Wedding Dress",
      "sizeCode": "S",
      "quantity": 2,
      "unitPrice": 5000.00,
      "subtotal": 10000.00
    }
  ],
  "transactions": [
    {
      "transactionId": 1,
      "variantId": 1,
      "productId": 1,
      "productName": "Wedding Dress",
      "sizeCode": "S",
      "transactionType": "RENT_OUT",
      "quantity": 2,
      "transactionDate": "2024-01-15T10:00:00",
      "expectedReturnDate": "2024-01-20",
      "status": "ACTIVE",
      "damageRecords": []
    },
    {
      "transactionId": 2,
      "variantId": 1,
      "transactionType": "DAMAGE",
      "quantity": 1,
      "status": "REPORTED",
      "damageRecords": [
        {
          "damageId": 1,
          "transactionId": 2,
          "description": "Tear in fabric",
          "repairCost": 500.00
        }
      ]
    }
  ]
}
```

## Transaction Types

- **RENT_OUT**: Items are rented out to customer
- **RETURN**: Items are returned by customer
- **SALE**: Items are sold (for SALE bookings)
- **DAMAGE**: Items are reported as damaged (requires damage record)

## UI Component Recommendations

### Product Form
- Master-detail form with expandable variant sections
- Show size dropdown populated from `/api/sizes`
- Conditional fields based on `isForRent` and `isForSale`
- Real-time inventory status updates

### Booking Form
- Tabbed interface:
  - Tab 1: Booking Items
  - Tab 2: Inventory Transactions
  - Tab 3: Summary
- Transaction timeline view showing all transactions chronologically
- Damage records inline with DAMAGE transactions
- Status badges for visual status indication

### Booking Detail View
- Timeline view of all transactions
- Expandable damage records
- Return date tracking with alerts
- Inventory impact visualization

