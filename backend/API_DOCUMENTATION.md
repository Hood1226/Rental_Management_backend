# Rental Management System - API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication

The API uses JWT (JSON Web Token) authentication. The token is stored in an HTTP-only cookie named `authToken`.

### Login
**Endpoint:** `POST /auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:** 
- Status: `200 OK`
- Sets `authToken` cookie (HTTP-only, SameSite=Lax)
- Body contains user details (token is removed from body for security)

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": 1,
    "userName": "john_doe",
    "email": "user@example.com",
    "roleName": "CUSTOMER"
  }
}
```

### Register
**Endpoint:** `POST /auth/register`

**Request Body:**
```json
{
  "userName": "john_doe",
  "email": "user@example.com",
  "password": "password123",
  "contactNumber": "+1234567890",
  "roleName": "CUSTOMER"
}
```

**Response:** Same as login

### Logout
**Endpoint:** `POST /auth/logout`

**Response:** Clears the `authToken` cookie

---

## Products API

### Get All Products
**Endpoint:** `GET /products`

**Headers:**
- Cookie: `authToken=<jwt_token>` (automatically sent by browser)

**Response:**
```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": [
    {
      "productId": 1,
      "productName": "Wedding Dress",
      "category": "Clothing",
      "description": "Beautiful white wedding dress",
      "depositAmount": 5000.00,
      "isForSale": false,
      "isForRent": true,
      "isActive": true,
      "createdBy": "admin",
      "createdAt": "2024-01-01T10:00:00",
      "updatedBy": null,
      "updatedAt": null
    }
  ]
}
```

### Get Product by ID
**Endpoint:** `GET /products/{id}`

**Path Parameters:**
- `id` (integer): Product ID

**Response:**
```json
{
  "success": true,
  "message": "Product retrieved successfully",
  "data": {
    "productId": 1,
    "productName": "Wedding Dress",
    "category": "Clothing",
    "description": "Beautiful white wedding dress",
    "depositAmount": 5000.00,
    "isForSale": false,
    "isForRent": true,
    "isActive": true,
    "createdBy": "admin",
    "createdAt": "2024-01-01T10:00:00",
    "updatedBy": null,
    "updatedAt": null
  }
}
```

### Create Product
**Endpoint:** `POST /products`

**Request Body:**
```json
{
  "productName": "Wedding Dress",
  "category": "Clothing",
  "description": "Beautiful white wedding dress",
  "depositAmount": 5000.00,
  "isForSale": false,
  "isForRent": true,
  "isActive": true
}
```

**Validation Rules:**
- `productName`: Required, max 150 characters
- `category`: Optional, max 100 characters
- `description`: Optional
- `depositAmount`: Optional, decimal
- `isForSale`: Optional, boolean (default: false)
- `isForRent`: Optional, boolean (default: true)
- `isActive`: Optional, boolean (default: true)

**Response:** Returns created product with generated ID and timestamps

### Update Product
**Endpoint:** `PUT /products/{id}`

**Path Parameters:**
- `id` (integer): Product ID

**Request Body:** Same as Create Product

**Response:** Returns updated product

### Delete Product
**Endpoint:** `DELETE /products/{id}`

**Path Parameters:**
- `id` (integer): Product ID

**Response:**
```json
{
  "success": true,
  "message": "Product deleted successfully",
  "data": null
}
```

**Note:** This performs a soft delete (sets `isActive` to false)

---

## Customers API

### Get All Customers
**Endpoint:** `GET /customers`

**Response:**
```json
{
  "success": true,
  "message": "Customers retrieved successfully",
  "data": [
    {
      "customerId": 1,
      "userId": 1,
      "customerName": "John Doe",
      "mobileNumber": "+1234567890",
      "whatsappNumber": "+1234567890",
      "email": "john@example.com",
      "address": "123 Main St, City, Country",
      "idProofType": "Aadhar",
      "idProofNo": "1234-5678-9012",
      "idProofImg": "base64_or_url",
      "createdBy": "admin",
      "createdAt": "2024-01-01T10:00:00",
      "updatedBy": null,
      "updatedAt": null
    }
  ]
}
```

### Get Customer by ID
**Endpoint:** `GET /customers/{id}`

**Path Parameters:**
- `id` (integer): Customer ID

**Response:** Single customer object

### Create Customer
**Endpoint:** `POST /customers`

**Request Body:**
```json
{
  "userId": 1,
  "customerName": "John Doe",
  "mobileNumber": "+1234567890",
  "whatsappNumber": "+1234567890",
  "email": "john@example.com",
  "address": "123 Main St, City, Country",
  "idProofType": "Aadhar",
  "idProofNo": "1234-5678-9012",
  "idProofImg": "base64_or_url"
}
```

**Validation Rules:**
- `customerName`: Required, max 150 characters
- `userId`: Optional, links to AppUser if exists
- `mobileNumber`: Optional, max 20 characters
- `whatsappNumber`: Optional, max 20 characters
- `email`: Optional, valid email format, max 150 characters
- `address`: Optional
- `idProofType`: Optional, max 50 characters
- `idProofNo`: Optional, max 50 characters
- `idProofImg`: Optional (Base64 or URL)

**Response:** Returns created customer with generated ID and timestamps

### Update Customer
**Endpoint:** `PUT /customers/{id}`

**Path Parameters:**
- `id` (integer): Customer ID

**Request Body:** Same as Create Customer

**Response:** Returns updated customer

---

## Bookings API

### Get All Bookings
**Endpoint:** `GET /bookings`

**Response:**
```json
{
  "success": true,
  "message": "Bookings retrieved successfully",
  "data": [
    {
      "bookingId": 1,
      "customerId": 1,
      "customerName": "John Doe",
      "bookingType": "RENT",
      "bookingDate": "2024-01-01T10:00:00",
      "status": "PENDING",
      "totalAmount": 10000.00,
      "createdBy": "admin",
      "createdAt": "2024-01-01T10:00:00",
      "updatedBy": null,
      "updatedAt": null,
      "items": []
    }
  ]
}
```

### Get Booking by ID
**Endpoint:** `GET /bookings/{id}`

**Path Parameters:**
- `id` (integer): Booking ID

**Response:** Single booking object

### Create Booking
**Endpoint:** `POST /bookings`

**Request Body:**
```json
{
  "customerId": 1,
  "bookingType": "RENT",
  "status": "PENDING",
  "totalAmount": 10000.00,
  "items": [
    {
      "variantId": 1,
      "quantity": 2,
      "unitPrice": 5000.00,
      "rentalStart": "2024-01-15",
      "rentalEnd": "2024-01-20",
      "subtotal": 10000.00
    }
  ]
}
```

**Validation Rules:**
- `customerId`: Required, integer
- `bookingType`: Required, max 20 characters (typically "RENT" or "SALE")
- `status`: Optional, max 20 characters (default: "PENDING")
- `totalAmount`: Optional, decimal
- `items`: Optional array of booking items
  - `variantId`: Required, integer
  - `quantity`: Required, integer
  - `unitPrice`: Optional, decimal
  - `rentalStart`: Optional, date (YYYY-MM-DD)
  - `rentalEnd`: Optional, date (YYYY-MM-DD)
  - `subtotal`: Optional, decimal

**Response:** Returns created booking with generated ID and timestamps

### Update Booking
**Endpoint:** `PUT /bookings/{id}`

**Path Parameters:**
- `id` (integer): Booking ID

**Request Body:** Same as Create Booking (all fields optional for update)

**Response:** Returns updated booking

---

## Error Responses

All endpoints return errors in the following format:

```json
{
  "success": false,
  "message": "Error message",
  "data": null
}
```

### Common HTTP Status Codes:
- `200 OK`: Success
- `400 Bad Request`: Validation error or invalid input
- `401 Unauthorized`: Authentication required or invalid token
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

### Validation Error Example:
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "productName": "Product name is required",
    "email": "Invalid email format"
  }
}
```

---

## Swagger UI

Access the interactive API documentation at:
- **Swagger UI:** http://localhost:8080/api/swagger-ui/index.html
- **OpenAPI JSON:** http://localhost:8080/api/v1/api-docs

The Swagger UI provides:
- Interactive API testing
- Request/response examples
- Schema definitions
- Authentication testing

---

## Frontend Integration Guide

### 1. Login Flow
```javascript
// Login request
const response = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  credentials: 'include', // Important: Include cookies
  body: JSON.stringify({
    email: 'user@example.com',
    password: 'password123'
  })
});

const data = await response.json();
// Cookie is automatically set by browser
```

### 2. Authenticated Requests
```javascript
// All subsequent requests automatically include the cookie
const response = await fetch('http://localhost:8080/api/products', {
  method: 'GET',
  credentials: 'include', // Important: Include cookies
  headers: {
    'Content-Type': 'application/json',
  }
});
```

### 3. Logout Flow
```javascript
const response = await fetch('http://localhost:8080/api/auth/logout', {
  method: 'POST',
  credentials: 'include', // Important: Include cookies
});
// Cookie is automatically cleared by browser
```

### Important Notes for Frontend:
1. Always use `credentials: 'include'` in fetch requests to send/receive cookies
2. The JWT token is stored in an HTTP-only cookie, so it's not accessible via JavaScript
3. CORS is configured for `http://localhost:3000` and `http://localhost:3001`
4. Handle 401 errors by redirecting to login page

---

## Example Frontend Code (React)

```javascript
// api.js
const API_BASE_URL = 'http://localhost:8080/api';

export const api = {
  async login(email, password) {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ email, password })
    });
    return response.json();
  },

  async getProducts() {
    const response = await fetch(`${API_BASE_URL}/products`, {
      method: 'GET',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' }
    });
    if (response.status === 401) {
      // Redirect to login
      window.location.href = '/login';
      return;
    }
    return response.json();
  },

  async createProduct(productData) {
    const response = await fetch(`${API_BASE_URL}/products`, {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(productData)
    });
    return response.json();
  },

  async updateProduct(id, productData) {
    const response = await fetch(`${API_BASE_URL}/products/${id}`, {
      method: 'PUT',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(productData)
    });
    return response.json();
  },

  async deleteProduct(id) {
    const response = await fetch(`${API_BASE_URL}/products/${id}`, {
      method: 'DELETE',
      credentials: 'include'
    });
    return response.json();
  }
};
```

---

## Testing with Postman

1. **Login:**
   - POST `http://localhost:8080/api/auth/login`
   - Body: `{"email": "user@example.com", "password": "password123"}`
   - Check "Cookies" tab - `authToken` cookie should be set

2. **Authenticated Request:**
   - GET `http://localhost:8080/api/products`
   - Postman automatically includes cookies from previous requests

3. **Logout:**
   - POST `http://localhost:8080/api/auth/logout`
   - Cookie is cleared

---

## Support

For issues or questions, please contact the development team or refer to the Swagger UI documentation.

