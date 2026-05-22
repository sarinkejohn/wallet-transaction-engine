# API Examples — Direct Engine (Port 8080)

All examples below hit the Spring Boot engine directly at `http://localhost:8080`.
No gateway headers are required.

---

## 1. Customer Wallet APIs

### 1.1 Create a Customer

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "mobileNumber": "+255711223344",
    "initialBalance": 5000.00
}'
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "name": "John Doe",
  "mobileNumber": "+255711223344",
  "status": "ACTIVE",
  "createdAt": "2026-05-22T09:00:00"
}
```

### 1.2 Get Customer by ID

```bash
curl -X GET http://localhost:8080/api/customers/1
```

### 1.3 Get Customer Wallet

```bash
curl -X GET http://localhost:8080/api/customers/1/wallet
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "customerId": 1,
  "balance": 5000.00,
  "currency": "TZS"
}
```

---

## 2. Charge Rule APIs

### 2.1 Create a Fixed Charge Rule (0 – 100)

```bash
curl -X POST http://localhost:8080/api/charge-rules \
  -H "Content-Type: application/json" \
  -d '{
    "channel": "MOBILE_APP",
    "minAmount": 0.0,
    "maxAmount": 100.00,
    "chargeType": "FIXED",
    "chargeValue": 5.00
}'
```

### 2.2 Create a Percentage Charge Rule (100.01 – 100000)

```bash
curl -X POST http://localhost:8080/api/charge-rules \
  -H "Content-Type: application/json" \
  -d '{
    "channel": "MOBILE_APP",
    "minAmount": 100.01,
    "maxAmount": 100000.00,
    "chargeType": "PERCENTAGE",
    "chargeValue": 1.50
}'
```

### 2.3 Get All Charge Rules

```bash
curl -X GET http://localhost:8080/api/charge-rules
```

### 2.4 Get Charge Rules by Channel

```bash
curl -X GET http://localhost:8080/api/charge-rules/channel/MOBILE_APP
```

---

## 3. Transaction API

### 3.1 Process a Transaction

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "transactionType": "TRANSFER",
    "amount": 50000,
    "currency": "TZS",
    "channel": "MOBILE_APP",
    "receiverMobile": "+255712345678",
    "idempotencyKey": "TXN-20260521-0001"
}'
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "customerId": 1,
  "transactionType": "TRANSFER",
  "amount": 50000,
  "charge": 750.00,
  "totalDebit": 50750.00,
  "currency": "TZS",
  "channel": "MOBILE_APP",
  "receiverMobile": "+255712345678",
  "idempotencyKey": "TXN-20260521-0001",
  "status": "SUCCESS",
  "failureReason": null,
  "createdAt": "2026-05-22T09:00:00"
}
```

### 3.2 Duplicate Idempotency Key (same request again)

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "transactionType": "TRANSFER",
    "amount": 50000,
    "currency": "TZS",
    "channel": "MOBILE_APP",
    "receiverMobile": "+255712345678",
    "idempotencyKey": "TXN-20260521-0001"
}'
```

**Expected:** Returns the original transaction response without deducting the wallet again.

### 3.3 Get Transaction by ID

```bash
curl -X GET http://localhost:8080/api/transactions/1
```

### 3.4 Get All Transactions

```bash
curl -X GET http://localhost:8080/api/transactions
```

### 3.5 Get Transactions by Customer ID

```bash
curl -X GET http://localhost:8080/api/transactions/customer/1
```

---

## 4. Wallet API (Alternative Endpoint)

### 4.1 Get Wallet by Customer ID

```bash
curl -X GET http://localhost:8080/api/wallets/customer/1
```

---

## 5. Error Scenarios

### 5.1 Insufficient Balance

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "transactionType": "TRANSFER",
    "amount": 9999999,
    "currency": "TZS",
    "channel": "MOBILE_APP",
    "receiverMobile": "+255712345678",
    "idempotencyKey": "TXN-INSUFFICIENT-001"
}'
```

**Expected:** `400 Bad Request` with an error message about insufficient balance.

### 5.2 Customer Not Found

```bash
curl -X GET http://localhost:8080/api/customers/9999
```

**Expected:** `404 Not Found`

### 5.3 Blocked Customer

If a customer has `status: BLOCKED`, any transaction attempt should return an error.

### 5.4 Duplicate Mobile Number

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "mobileNumber": "+255711223344",
    "initialBalance": 1000.00
}'
```

**Expected:** `409 Conflict` — customer with this mobile number already exists.
