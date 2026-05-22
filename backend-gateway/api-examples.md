# API Examples — Via Gateway (Port 9090)

All examples below route through the **backend-gateway** at `http://localhost:9090`.
The gateway enforces:
- `x-api-key` header (default: `default-api-key-123`)
- `Channel` header (one of: `WEBP`, `SWIFTY`, `BANKAPP`, `MOBILEMONEY`, `SAMONEY`)
- `AppId`, `AppVersion`, `RequestId` headers (JSON schema validated)
- `Content-Type: application/json`
- Rate limiting per channel tier (BRONZE / SILVER / GOLD)

---

## Required Gateway Headers

Every request through the gateway **must** include these headers:

```
x-api-key: default-api-key-123
Content-Type: application/json
Channel: WEBP
AppId: WEBP
AppVersion: 1.0.0
RequestId: <unique-uuid-per-request, max 36 chars>
```

---

## 1. Customer Wallet APIs

### 1.1 Create a Customer

```bash
curl -X POST http://localhost:9090/api/customers \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json" \
  -H "Channel: WEBP" \
  -H "AppId: WEBP" \
  -H "AppVersion: 1.0.0" \
  -H "RequestId: $(uuidgen | cut -c1-36)" \
  -d '{
    "name": "John Doe",
    "mobileNumber": "+255711223344",
    "initialBalance": 5000.00
}'
```

### 1.2 Get Customer by ID

```bash
curl -X GET http://localhost:9090/api/customers/1 \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json" \
  -H "Channel: WEBP" \
  -H "AppId: WEBP" \
  -H "AppVersion: 1.0.0" \
  -H "RequestId: $(uuidgen | cut -c1-36)"
```

### 1.3 Get Customer Wallet

```bash
curl -X GET http://localhost:9090/api/customers/1/wallet \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json" \
  -H "Channel: WEBP" \
  -H "AppId: WEBP" \
  -H "AppVersion: 1.0.0" \
  -H "RequestId: $(uuidgen | cut -c1-36)"
```

---

## 2. Charge Rule APIs

### 2.1 Create a Fixed Charge Rule (0 – 100)

```bash
curl -X POST http://localhost:9090/api/charge-rules \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json" \
  -H "Channel: BANKAPP" \
  -H "AppId: BANKAPP" \
  -H "AppVersion: 2.0.0" \
  -H "RequestId: $(uuidgen | cut -c1-36)" \
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
curl -X POST http://localhost:9090/api/charge-rules \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json" \
  -H "Channel: BANKAPP" \
  -H "AppId: BANKAPP" \
  -H "AppVersion: 2.0.0" \
  -H "RequestId: $(uuidgen | cut -c1-36)" \
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
curl -X GET http://localhost:9090/api/charge-rules \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json" \
  -H "Channel: WEBP" \
  -H "AppId: WEBP" \
  -H "AppVersion: 1.0.0" \
  -H "RequestId: $(uuidgen | cut -c1-36)"
```

### 2.4 Get Charge Rules by Channel

```bash
curl -X GET http://localhost:9090/api/charge-rules/channel/MOBILE_APP \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json" \
  -H "Channel: WEBP" \
  -H "AppId: WEBP" \
  -H "AppVersion: 1.0.0" \
  -H "RequestId: $(uuidgen | cut -c1-36)"
```

---

## 3. Transaction API

### 3.1 Process a Transaction

```bash
curl -X POST http://localhost:9090/api/transactions \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json" \
  -H "Channel: MOBILEMONEY" \
  -H "AppId: SAMONEY" \
  -H "AppVersion: 1.0.0" \
  -H "RequestId: $(uuidgen | cut -c1-36)" \
  -d '{
    "customerId": 1,
    "transactionType": "TRANSFER",
    "amount": 50000,
    "currency": "TZS",
    "channel": "MOBILE_APP",
    "receiverMobile": "+255712345678",
    "idempotencyKey": "TXN-20260522-GW-0001"
}'
```

**Expected Response (200 OK):**
```json
{
  "id": 2,
  "customerId": 1,
  "transactionType": "TRANSFER",
  "amount": 50000,
  "charge": 750.00,
  "totalDebit": 50750.00,
  "currency": "TZS",
  "channel": "MOBILE_APP",
  "receiverMobile": "+255712345678",
  "idempotencyKey": "TXN-20260522-GW-0001",
  "status": "SUCCESS",
  "failureReason": null,
  "createdAt": "2026-05-22T09:00:00"
}
```

### 3.2 Duplicate Idempotency Key (via Gateway)

```bash
curl -X POST http://localhost:9090/api/transactions \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json" \
  -H "Channel: MOBILEMONEY" \
  -H "AppId: SAMONEY" \
  -H "AppVersion: 1.0.0" \
  -H "RequestId: $(uuidgen | cut -c1-36)" \
  -d '{
    "customerId": 1,
    "transactionType": "TRANSFER",
    "amount": 50000,
    "currency": "TZS",
    "channel": "MOBILE_APP",
    "receiverMobile": "+255712345678",
    "idempotencyKey": "TXN-20260522-GW-0001"
}'
```

**Expected:** Returns the original transaction (no double debit).

### 3.3 Get Transaction by ID

```bash
curl -X GET http://localhost:9090/api/transactions/1 \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json" \
  -H "Channel: WEBP" \
  -H "AppId: WEBP" \
  -H "AppVersion: 1.0.0" \
  -H "RequestId: $(uuidgen | cut -c1-36)"
```

### 3.4 Get All Transactions

```bash
curl -X GET http://localhost:9090/api/transactions \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json" \
  -H "Channel: WEBP" \
  -H "AppId: WEBP" \
  -H "AppVersion: 1.0.0" \
  -H "RequestId: $(uuidgen | cut -c1-36)"
```

### 3.5 Get Transactions by Customer ID

```bash
curl -X GET http://localhost:9090/api/transactions/customer/1 \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json" \
  -H "Channel: WEBP" \
  -H "AppId: WEBP" \
  -H "AppVersion: 1.0.0" \
  -H "RequestId: $(uuidgen | cut -c1-36)"
```

---

## 4. Wallet API (Alternative Endpoint)

### 4.1 Get Wallet by Customer ID

```bash
curl -X GET http://localhost:9090/api/wallets/customer/1 \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json" \
  -H "Channel: WEBP" \
  -H "AppId: WEBP" \
  -H "AppVersion: 1.0.0" \
  -H "RequestId: $(uuidgen | cut -c1-36)"
```

---

## 5. Gateway-Specific Error Scenarios

### 5.1 Missing x-api-key

```bash
curl -X GET http://localhost:9090/api/customers/1 \
  -H "Content-Type: application/json" \
  -H "Channel: WEBP" \
  -H "AppId: WEBP" \
  -H "AppVersion: 1.0.0" \
  -H "RequestId: test-no-api-key"
```

**Expected:** `401 Unauthorized`

### 5.2 Invalid Channel

```bash
curl -X GET http://localhost:9090/api/customers/1 \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json" \
  -H "Channel: INVALID_CHANNEL" \
  -H "AppId: WEBP" \
  -H "AppVersion: 1.0.0" \
  -H "RequestId: test-invalid-channel"
```

**Expected:** `400 Bad Request` with `ERR_1000003` (invalid channel).

### 5.3 Missing Mandatory Headers

```bash
curl -X GET http://localhost:9090/api/customers/1 \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json"
```

**Expected:** `400 Bad Request` with `ERR_1000001` (missing mandatory parameters).

### 5.4 RequestId Too Long (> 36 chars)

```bash
curl -X GET http://localhost:9090/api/customers/1 \
  -H "x-api-key: default-api-key-123" \
  -H "Content-Type: application/json" \
  -H "Channel: WEBP" \
  -H "AppId: WEBP" \
  -H "AppVersion: 1.0.0" \
  -H "RequestId: this-request-id-is-way-too-long-and-exceeds-the-36-char-limit"
```

**Expected:** `400 Bad Request` with `ERR_1000002` (invalid parameter length).

---

## 6. Health Check

```bash
curl -X GET http://localhost:9090/actuator/health
```

**Expected:** `200 OK` with `{"status":"UP"}`
