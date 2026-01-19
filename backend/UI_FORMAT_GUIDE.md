# UI Format Guide for Parent-Child CRUD Operations

This guide provides the data formats and UI structure recommendations for handling parent-child relationships in the Rental Management System.

## Table of Contents
1. [Product with Variants](#product-with-variants)
2. [Booking with Items](#booking-with-items)
3. [UI Component Structure](#ui-component-structure)
4. [Form Examples](#form-examples)

---

## Product with Variants

### Data Structure

**Product** contains multiple **ProductVariant** entries, each with:
- Size information
- Purchase price
- Rent price (if product is for rent)
- Sale price (if product is for sale)
- Inventory information

### Request Format (Create/Update Product)

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
    },
    {
      "sizeId": 2,
      "purchasePrice": 16000.00,
      "rentPrice": 5500.00,
      "rentEffectiveFrom": "2024-01-01",
      "rentEffectiveTo": null,
      "availableQuantity": 3,
      "availabilityStatus": "AVAILABLE"
    }
  ]
}
```

### Response Format

```json
{
  "success": true,
  "message": "Product created successfully",
  "data": {
    "productId": 1,
    "productName": "Wedding Dress",
    "category": "Clothing",
    "description": "Beautiful white wedding dress",
    "depositAmount": 5000.00,
    "isForSale": false,
    "isForRent": true,
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": null,
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
        "saleEffectiveFrom": null,
        "saleEffectiveTo": null,
        "inventoryId": 1,
        "availableQuantity": 5,
        "availabilityStatus": "AVAILABLE",
        "expectedRestoreDate": null,
        "nextAvailabilityDate": null,
        "createdAt": "2024-01-01T10:00:00",
        "updatedAt": null
      }
    ]
  }
}
```

### UI Form Structure

```
┌─────────────────────────────────────────────────────────┐
│ Product Form                                            │
├─────────────────────────────────────────────────────────┤
│ Product Name: [________________]                        │
│ Category: [________________]                            │
│ Description: [________________]                        │
│                                                         │
│ Deposit Amount: [________]                             │
│ ☑ For Rent  ☐ For Sale  ☑ Active                      │
│                                                         │
│ ┌───────────────────────────────────────────────────┐ │
│ │ Variants Section                                  │ │
│ ├───────────────────────────────────────────────────┤ │
│ │ [+ Add Variant]                                   │ │
│ │                                                   │ │
│ │ Variant 1:                                        │ │
│ │ ┌─────────────────────────────────────────────┐ │ │
│ │ │ Size: [Dropdown: S ▼]                      │ │ │
│ │ │ Purchase Price: [________]                 │ │ │
│ │ │                                               │ │ │
│ │ │ Rent Price:                                  │ │ │
│ │ │   Price: [________]                          │ │ │
│ │ │   Effective From: [Date Picker]            │ │ │
│ │ │   Effective To: [Date Picker] (Optional)    │ │ │
│ │ │                                               │ │ │
│ │ │ Sale Price:                                  │ │ │
│ │ │   Price: [________]                          │ │ │
│ │ │   Effective From: [Date Picker]            │ │ │
│ │ │   Effective To: [Date Picker] (Optional)    │ │ │
│ │ │                                               │ │ │
│ │ │ Inventory:                                   │ │ │
│ │ │   Available Quantity: [____]               │ │ │
│ │ │   Status: [Dropdown: AVAILABLE ▼]          │ │ │
│ │ │   Expected Restore Date: [Date Picker]     │ │ │
│ │ │   Next Availability Date: [Date Picker]    │ │ │
│ │ │                                               │ │ │
│ │ │ [Delete Variant]                            │ │ │
│ │ └─────────────────────────────────────────────┘ │ │
│ │                                                   │ │
│ │ Variant 2:                                        │ │
│ │ [Similar structure...]                          │ │
│ └───────────────────────────────────────────────────┘ │
│                                                         │
│ [Save Product] [Cancel]                                │
└─────────────────────────────────────────────────────────┘
```

### React Component Example

```jsx
import React, { useState } from 'react';

const ProductForm = ({ product, onSubmit }) => {
  const [formData, setFormData] = useState({
    productName: product?.productName || '',
    category: product?.category || '',
    description: product?.description || '',
    depositAmount: product?.depositAmount || 0,
    isForSale: product?.isForSale || false,
    isForRent: product?.isForRent || true,
    isActive: product?.isActive || true,
    variants: product?.variants || []
  });

  const addVariant = () => {
    setFormData({
      ...formData,
      variants: [
        ...formData.variants,
        {
          sizeId: null,
          purchasePrice: 0,
          rentPrice: null,
          rentEffectiveFrom: new Date().toISOString().split('T')[0],
          rentEffectiveTo: null,
          salePrice: null,
          saleEffectiveFrom: null,
          saleEffectiveTo: null,
          availableQuantity: 0,
          availabilityStatus: 'AVAILABLE'
        }
      ]
    });
  };

  const removeVariant = (index) => {
    setFormData({
      ...formData,
      variants: formData.variants.filter((_, i) => i !== index)
    });
  };

  const updateVariant = (index, field, value) => {
    const updatedVariants = [...formData.variants];
    updatedVariants[index] = { ...updatedVariants[index], [field]: value };
    setFormData({ ...formData, variants: updatedVariants });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* Product Fields */}
      <div>
        <label>Product Name</label>
        <input
          value={formData.productName}
          onChange={(e) => setFormData({ ...formData, productName: e.target.value })}
          required
        />
      </div>

      {/* Variants Section */}
      <div>
        <h3>Variants</h3>
        <button type="button" onClick={addVariant}>+ Add Variant</button>
        
        {formData.variants.map((variant, index) => (
          <div key={index} className="variant-card">
            <h4>Variant {index + 1}</h4>
            
            <select
              value={variant.sizeId || ''}
              onChange={(e) => updateVariant(index, 'sizeId', parseInt(e.target.value))}
              required
            >
              <option value="">Select Size</option>
              {/* Populate from sizes API */}
            </select>

            <input
              type="number"
              placeholder="Purchase Price"
              value={variant.purchasePrice || ''}
              onChange={(e) => updateVariant(index, 'purchasePrice', parseFloat(e.target.value))}
            />

            {formData.isForRent && (
              <>
                <input
                  type="number"
                  placeholder="Rent Price"
                  value={variant.rentPrice || ''}
                  onChange={(e) => updateVariant(index, 'rentPrice', parseFloat(e.target.value))}
                />
                <input
                  type="date"
                  placeholder="Rent Effective From"
                  value={variant.rentEffectiveFrom || ''}
                  onChange={(e) => updateVariant(index, 'rentEffectiveFrom', e.target.value)}
                />
              </>
            )}

            {formData.isForSale && (
              <>
                <input
                  type="number"
                  placeholder="Sale Price"
                  value={variant.salePrice || ''}
                  onChange={(e) => updateVariant(index, 'salePrice', parseFloat(e.target.value))}
                />
                <input
                  type="date"
                  placeholder="Sale Effective From"
                  value={variant.saleEffectiveFrom || ''}
                  onChange={(e) => updateVariant(index, 'saleEffectiveFrom', e.target.value)}
                />
              </>
            )}

            <input
              type="number"
              placeholder="Available Quantity"
              value={variant.availableQuantity || ''}
              onChange={(e) => updateVariant(index, 'availableQuantity', parseInt(e.target.value))}
            />

            <button type="button" onClick={() => removeVariant(index)}>
              Delete Variant
            </button>
          </div>
        ))}
      </div>

      <button type="submit">Save Product</button>
    </form>
  );
};

export default ProductForm;
```

---

## Booking with Items, Transactions, and Damage Records

### Data Structure

**Booking** contains:
- Multiple **BookingItem** entries (what was booked)
- Multiple **InventoryTransaction** entries (rental/return/sale/damage transactions)
- **DamageRecord** entries (linked to transactions with type DAMAGE)

### Request Format (Create/Update Booking)

```json
{
  "customerId": 1,
  "bookingType": "RENT",
  "status": "PENDING",
  "totalAmount": 15000.00,
  "items": [
    {
      "variantId": 1,
      "quantity": 2,
      "unitPrice": 5000.00,
      "rentalStart": "2024-01-15",
      "rentalEnd": "2024-01-20",
      "subtotal": 10000.00
    },
    {
      "variantId": 2,
      "quantity": 1,
      "unitPrice": 5000.00,
      "rentalStart": "2024-01-15",
      "rentalEnd": "2024-01-20",
      "subtotal": 5000.00
    }
  ],
  "transactions": [
    {
      "variantId": 1,
      "transactionType": "RENT_OUT",
      "quantity": 2,
      "expectedReturnDate": "2024-01-20",
      "status": "ACTIVE",
      "notes": "Items rented out to customer"
    },
    {
      "variantId": 1,
      "transactionType": "DAMAGE",
      "quantity": 1,
      "status": "REPORTED",
      "notes": "Item damaged during rental",
      "damageRecord": {
        "description": "Tear in fabric",
        "repairCost": 500.00
      }
    }
  ]
}
```

### Response Format

```json
{
  "success": true,
  "message": "Booking created successfully",
  "data": {
    "bookingId": 1,
    "customerId": 1,
    "customerName": "John Doe",
    "bookingType": "RENT",
    "bookingDate": "2024-01-01T10:00:00",
    "status": "PENDING",
    "totalAmount": 15000.00,
    "createdAt": "2024-01-01T10:00:00",
    "items": [
      {
        "bookingItemId": 1,
        "variantId": 1,
        "productId": 1,
        "productName": "Wedding Dress",
        "sizeCode": "S",
        "quantity": 2,
        "unitPrice": 5000.00,
        "rentalStart": "2024-01-15",
        "rentalEnd": "2024-01-20",
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
        "actualReturnDate": null,
        "status": "ACTIVE",
        "notes": "Items rented out to customer",
        "createdAt": "2024-01-15T10:00:00",
        "damageRecords": []
      },
      {
        "transactionId": 2,
        "variantId": 1,
        "productId": 1,
        "productName": "Wedding Dress",
        "sizeCode": "S",
        "transactionType": "DAMAGE",
        "quantity": 1,
        "transactionDate": "2024-01-18T14:00:00",
        "status": "REPORTED",
        "notes": "Item damaged during rental",
        "createdAt": "2024-01-18T14:00:00",
        "damageRecords": [
          {
            "damageId": 1,
            "transactionId": 2,
            "description": "Tear in fabric",
            "repairCost": 500.00,
            "createdAt": "2024-01-18T14:00:00"
          }
        ]
      }
    ]
  }
}
```

### UI Form Structure

```
┌─────────────────────────────────────────────────────────┐
│ Booking Form                                            │
├─────────────────────────────────────────────────────────┤
│ Customer: [Dropdown: Select Customer ▼]                │
│ Booking Type: ○ RENT  ○ SALE                           │
│ Status: [Dropdown: PENDING ▼]                          │
│                                                         │
│ ┌───────────────────────────────────────────────────┐ │
│ │ Booking Items Section                            │ │
│ ├───────────────────────────────────────────────────┤ │
│ │ [+ Add Item]                                      │ │
│ │                                                   │ │
│ │ Item 1:                                           │ │
│ │ ┌─────────────────────────────────────────────┐ │ │
│ │ │ Product Variant: [Dropdown: Select ▼]      │ │ │
│ │ │ Quantity: [____]                            │ │ │
│ │ │ Unit Price: [________]                      │ │ │
│ │ │                                               │ │ │
│ │ │ Rental Period (if RENT):                     │ │ │
│ │ │   Start Date: [Date Picker]                 │ │ │
│ │ │   End Date: [Date Picker]                   │ │ │
│ │ │                                               │ │ │
│ │ │ Subtotal: [Auto-calculated: 10000.00]       │ │ │
│ │ │                                               │ │ │
│ │ │ [Delete Item]                                │ │ │
│ │ └─────────────────────────────────────────────┘ │ │
│ │                                                   │ │
│ │ ┌─────────────────────────────────────────────┐ │ │
│ │ │ Inventory Transactions Section              │ │ │
│ │ ├─────────────────────────────────────────────┤ │ │
│ │ │ [+ Add Transaction]                         │ │ │
│ │ │                                               │ │ │
│ │ │ Transaction 1:                               │ │ │
│ │ │ ┌─────────────────────────────────────────┐ │ │ │
│ │ │ │ Variant: [Dropdown: Select ▼]           │ │ │ │
│ │ │ │ Type: [RENT_OUT/RETURN/SALE/DAMAGE ▼]  │ │ │ │
│ │ │ │ Quantity: [____]                        │ │ │ │
│ │ │ │ Expected Return: [Date Picker]          │ │ │ │
│ │ │ │ Actual Return: [Date Picker]            │ │ │ │
│ │ │ │ Status: [Dropdown: ACTIVE ▼]           │ │ │ │
│ │ │ │ Notes: [Text Area]                      │ │ │ │
│ │ │ │                                           │ │ │ │
│ │ │ │ Damage Record (if DAMAGE):                │ │ │ │
│ │ │ │   Description: [Text Area]              │ │ │ │
│ │ │ │   Repair Cost: [________]               │ │ │ │
│ │ │ │                                           │ │ │ │
│ │ │ │ [Delete Transaction]                     │ │ │ │
│ │ │ └─────────────────────────────────────────┘ │ │ │
│ │ └─────────────────────────────────────────────┘ │ │
│ │                                                   │ │
│ │ Item 2:                                           │ │
│ │ [Similar structure...]                          │ │
│ │                                                   │ │
│ │ ─────────────────────────────────────────────── │ │
│ │ Total Amount: [Auto-calculated: 15000.00]       │ │
│ └───────────────────────────────────────────────────┘ │
│                                                         │
│ [Save Booking] [Cancel]                                │
└─────────────────────────────────────────────────────────┘
```

### React Component Example

```jsx
import React, { useState, useEffect } from 'react';

const BookingForm = ({ booking, onSubmit }) => {
  const [formData, setFormData] = useState({
    customerId: booking?.customerId || null,
    bookingType: booking?.bookingType || 'RENT',
    status: booking?.status || 'PENDING',
    totalAmount: booking?.totalAmount || 0,
    items: booking?.items || []
  });

  const addItem = () => {
    setFormData({
      ...formData,
      items: [
        ...formData.items,
        {
          variantId: null,
          quantity: 1,
          unitPrice: 0,
          rentalStart: null,
          rentalEnd: null,
          subtotal: 0
        }
      ]
    });
  };

  const removeItem = (index) => {
    const updatedItems = formData.items.filter((_, i) => i !== index);
    const totalAmount = updatedItems.reduce((sum, item) => 
      sum + (item.subtotal || 0), 0
    );
    setFormData({ ...formData, items: updatedItems, totalAmount });
  };

  const updateItem = (index, field, value) => {
    const updatedItems = [...formData.items];
    updatedItems[index] = { ...updatedItems[index], [field]: value };
    
    // Auto-calculate subtotal
    if (field === 'quantity' || field === 'unitPrice') {
      const item = updatedItems[index];
      if (item.quantity && item.unitPrice) {
        item.subtotal = item.quantity * item.unitPrice;
      }
    }
    
    // Calculate total amount
    const totalAmount = updatedItems.reduce((sum, item) => 
      sum + (item.subtotal || 0), 0
    );
    
    setFormData({ ...formData, items: updatedItems, totalAmount });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* Booking Fields */}
      <div>
        <label>Customer</label>
        <select
          value={formData.customerId || ''}
          onChange={(e) => setFormData({ ...formData, customerId: parseInt(e.target.value) })}
          required
        >
          <option value="">Select Customer</option>
          {/* Populate from customers API */}
        </select>
      </div>

      <div>
        <label>Booking Type</label>
        <input
          type="radio"
          value="RENT"
          checked={formData.bookingType === 'RENT'}
          onChange={(e) => setFormData({ ...formData, bookingType: e.target.value })}
        /> RENT
        <input
          type="radio"
          value="SALE"
          checked={formData.bookingType === 'SALE'}
          onChange={(e) => setFormData({ ...formData, bookingType: e.target.value })}
        /> SALE
      </div>

      {/* Items Section */}
      <div>
        <h3>Booking Items</h3>
        <button type="button" onClick={addItem}>+ Add Item</button>
        
        {formData.items.map((item, index) => (
          <div key={index} className="item-card">
            <h4>Item {index + 1}</h4>
            
            <select
              value={item.variantId || ''}
              onChange={(e) => {
                const variantId = parseInt(e.target.value);
                updateItem(index, 'variantId', variantId);
                // Fetch variant price and update unitPrice
              }}
              required
            >
              <option value="">Select Product Variant</option>
              {/* Populate from variants API */}
            </select>

            <input
              type="number"
              placeholder="Quantity"
              value={item.quantity || ''}
              onChange={(e) => updateItem(index, 'quantity', parseInt(e.target.value))}
              min="1"
              required
            />

            <input
              type="number"
              placeholder="Unit Price"
              value={item.unitPrice || ''}
              onChange={(e) => updateItem(index, 'unitPrice', parseFloat(e.target.value))}
              step="0.01"
            />

            {formData.bookingType === 'RENT' && (
              <>
                <input
                  type="date"
                  placeholder="Rental Start Date"
                  value={item.rentalStart || ''}
                  onChange={(e) => updateItem(index, 'rentalStart', e.target.value)}
                />
                <input
                  type="date"
                  placeholder="Rental End Date"
                  value={item.rentalEnd || ''}
                  onChange={(e) => updateItem(index, 'rentalEnd', e.target.value)}
                />
              </>
            )}

            <div>
              <strong>Subtotal: {item.subtotal || 0}</strong>
            </div>

            <button type="button" onClick={() => removeItem(index)}>
              Delete Item
            </button>
          </div>
        ))}

        <div>
          <h3>Total Amount: {formData.totalAmount}</h3>
        </div>
      </div>

      <button type="submit">Save Booking</button>
    </form>
  );
};

export default BookingForm;
```

---

## UI Component Structure

### Recommended Component Hierarchy

```
App
├── ProductManagement
│   ├── ProductList
│   │   └── ProductCard (shows product + variant summary)
│   └── ProductForm
│       ├── ProductBasicInfo
│       └── VariantList
│           └── VariantForm (repeatable)
│               ├── VariantBasicInfo
│               ├── PriceInfo (Rent/Sale)
│               └── InventoryInfo
│
└── BookingManagement
    ├── BookingList
    │   └── BookingCard (shows booking + item summary)
    └── BookingForm
        ├── BookingBasicInfo
        └── ItemList
            └── ItemForm (repeatable)
                ├── VariantSelection
                ├── QuantityAndPrice
                └── RentalPeriod (if RENT)
```

### Key UI Patterns

1. **Accordion/Collapsible Sections**: Use for variants/items to keep forms manageable
2. **Inline Editing**: Allow editing variants/items inline in list view
3. **Auto-calculation**: Calculate subtotals and totals automatically
4. **Validation**: Show validation errors inline for each variant/item
5. **Drag & Drop**: Allow reordering variants/items (optional)
6. **Bulk Actions**: Select multiple variants/items for bulk operations

### Data Fetching Strategy

```javascript
// Fetch product with all variants
const fetchProduct = async (productId) => {
  const response = await fetch(`/api/products/${productId}`, {
    credentials: 'include'
  });
  const data = await response.json();
  return data.data; // Contains product + variants array
};

// Fetch available sizes for dropdown
const fetchSizes = async () => {
  const response = await fetch('/api/sizes', {
    credentials: 'include'
  });
  const data = await response.json();
  return data.data;
};

// Fetch available variants for booking
const fetchVariants = async (productId) => {
  const response = await fetch(`/api/products/${productId}`, {
    credentials: 'include'
  });
  const data = await response.json();
  return data.data.variants;
};
```

---

## Form Examples

### Product Form with Variants (Complete Example)

```jsx
// ProductForm.jsx
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

const ProductForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [sizes, setSizes] = useState([]);
  const [formData, setFormData] = useState({
    productName: '',
    category: '',
    description: '',
    depositAmount: 0,
    isForSale: false,
    isForRent: true,
    isActive: true,
    variants: []
  });

  useEffect(() => {
    // Fetch sizes
    fetchSizes();
    
    // If editing, fetch product
    if (id) {
      fetchProduct(id);
    }
  }, [id]);

  const fetchSizes = async () => {
    // Implement size fetching
  };

  const fetchProduct = async (productId) => {
    const response = await fetch(`/api/products/${productId}`, {
      credentials: 'include'
    });
    const data = await response.json();
    if (data.success) {
      setFormData(data.data);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const url = id ? `/api/products/${id}` : '/api/products';
    const method = id ? 'PUT' : 'POST';
    
    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(formData)
    });
    
    const result = await response.json();
    if (result.success) {
      navigate('/products');
    }
  };

  // ... rest of component code from above examples
};
```

---

## Best Practices

1. **Always send complete data**: Include all variants/items in create/update requests
2. **Handle empty arrays**: Allow products/bookings without variants/items initially
3. **Validate on client**: Validate before submission to improve UX
4. **Show loading states**: Display loading indicators during API calls
5. **Error handling**: Show specific errors for each variant/item
6. **Optimistic updates**: Update UI immediately, rollback on error
7. **Debounce calculations**: Debounce auto-calculations for better performance
8. **Save drafts**: Consider saving form state locally for recovery

---

## API Endpoints Reference

### Products
- `GET /api/products` - Get all products with variants
- `GET /api/products/{id}` - Get product with all variants
- `POST /api/products` - Create product with variants
- `PUT /api/products/{id}` - Update product and variants
- `DELETE /api/products/{id}` - Soft delete product (cascades to variants)

### Bookings
- `GET /api/bookings` - Get all bookings with items
- `GET /api/bookings/{id}` - Get booking with all items
- `POST /api/bookings` - Create booking with items
- `PUT /api/bookings/{id}` - Update booking and items

### Supporting Endpoints
- `GET /api/sizes` - Get all product sizes (for dropdowns)
- `GET /api/products/{id}/variants` - Get variants for a product (alternative)

---

This guide provides a comprehensive foundation for building UI components that handle parent-child relationships effectively.

