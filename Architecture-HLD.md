# Mini Payment Charge & Wallet Transaction Engine - High Level Design (HLD)

## 1. System Component Architecture
The system is built as a single Spring Boot microservice running on Java 21.

*   **API Gateway (Optional):** Routes traffic to the backend engine.
*   **Minipayment Engine (App):** Exposes REST APIs, handles business logic, and orchestrates transactions.
*   **PostgreSQL 16:** The primary persistent store for Customers, Wallets, Transactions, and Charge Rules.
*   **Redis 7.0:** Used for distributed locking to ensure idempotency.
*   **Prometheus & Grafana:** For metrics collection and visualization.
*   **OpenTelemetry:** Tracing and observability integrated via Logback appender.

## 2. Sequence Flow Diagram (Idempotent Transaction Processing)
```mermaid
sequenceDiagram
    participant Client
    participant App as Minipayment Engine
    participant Redis as Redis (Locks)
    participant DB as PostgreSQL
    
    Client->>App: POST /api/transactions (Amount, Channel, IdempotencyKey)
    App->>DB: Check if IdempotencyKey exists
    alt Exists
        DB-->>App: Return existing Transaction
        App-->>Client: 200 OK (Cached Response)
    else Not Exists
        App->>Redis: Acquire Lock (key: lock:idempotency:{IdempotencyKey})
        alt Lock Failed
            App-->>Client: 429 Too Many Requests (Concurrent Processing)
        else Lock Acquired
            App->>DB: Double check IdempotencyKey (in case of race condition)
            App->>DB: Validate Customer (ACTIVE)
            App->>DB: Fetch ChargeRule for Channel and Amount
            App->>DB: Check Wallet Balance (Balance >= Amount + Charge)
            App->>DB: Deduct Balance from Wallet
            App->>DB: Insert Transaction Record (Status=SUCCESS)
            App->>Redis: Release Lock
            App-->>Client: 200 OK (New Response)
        end
    end
```

## 3. Entity Relationship Diagram (ERD)
```mermaid
erDiagram
    CUSTOMER {
        Long id PK
        String name
        String mobile_number UK
        String status
        Timestamp created_at
    }
    
    WALLET {
        Long id PK
        Long customer_id FK, UK
        Decimal wallet_balance
    }
    
    TRANSACTION {
        Long id PK
        Long customer_id FK
        String transaction_type
        Decimal amount
        Decimal charge
        Decimal total_debit
        String currency
        String channel
        String receiver_mobile
        String idempotency_key UK
        String status
        String failure_reason
        Timestamp created_at
    }
    
    CHARGE_RULE {
        Long id PK
        String channel
        Decimal min_amount
        Decimal max_amount
        String charge_type
        Decimal charge_value
    }
    
    CUSTOMER ||--o| WALLET : "has one"
    CUSTOMER ||--o{ TRANSACTION : "makes"
```

## 4. Dynamic Charge Rule Logic
Charge rules are configured dynamically per channel and amount range.
*   `FIXED`: The `charge_value` is directly added to the amount.
*   `PERCENTAGE`: The `charge_value` is calculated as `(amount * charge_value) / 100`.
If no rule matches the given amount and channel, the transaction is rejected to prevent un-charged transactions.

## 5. Reversals & Monitoring
*   **Reversals:** For the MVP, reversals can be achieved via manual DB operations or implementing a `/reversal` endpoint that credits the wallet and creates a `REVERSAL` transaction type.
*   **Monitoring:** The app exposes `/actuator/prometheus` which Prometheus scrapes every 10 seconds.
