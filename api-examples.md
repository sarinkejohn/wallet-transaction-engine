# API Examples

## 1. Create a Customer
```bash
curl -X POST http://localhost:8080/api/customers \
-H "Content-Type: application/json" \
-d '{
    "name": "John Doe",
    "mobileNumber": "+255711223344",
    "initialBalance": 5000.00
}'
```

## 2. Configure a Charge Rule (Fixed for 0 - 100)
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

## 3. Configure a Charge Rule (Percentage for 100.01 - 100000)
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

## 4. Process a Transaction
```bash
curl -X POST http://localhost:8080/api/transactions \
-H "Content-Type: application/json" \
-d '{
    "customerId": 1,
    "amount": 250.00,
    "currency": "TSH,
    "channel": "MOBILE_APP",
    "receiverMobile": "+255799887766",
    "idempotencyKey": "unique-uuid-12345"
}'
```
