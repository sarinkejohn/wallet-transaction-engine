# Mini Payment Charge & Wallet Transaction Engine

This repository contains the Mini Payment Charge & Wallet Transaction Engine, a resilient Spring Boot application designed to handle idempotent wallet transactions, dynamic charge calculations, and robust concurrency control via Redis locks.

## Prerequisites
* **Docker** (v20.10+)
* **Docker Compose** (v2.0+)
* **Java 21** (for local development)

## Quick Start (Docker Compose)

The easiest way to run the entire stack (Application, PostgreSQL, Redis, Prometheus, Grafana) is by using Docker Compose. 

1. **Verify Environment Variables**
   There is a `.env` file in the root directory that defines the database credentials. You can adjust this if needed:
   ```env
   DB_NAME=mini_payment_engine_wallet_db
   DB_USER=test
   DB_PASSWORD=password
   ```

2. **Build and Start the Services**
   Run the following command from the root directory (where `docker-compose.yml` is located):
   ```bash
   docker compose up -d --build
   ```
   This will:
   * Build a lightweight, multi-stage Alpine Docker image for the Spring Boot app.
   * Start PostgreSQL (DB), Redis (Locks), Prometheus (Metrics Scraping), and Grafana (Dashboards).

3. **Verify the Services are Running**
   You can check the logs of the application by running:
   ```bash
   docker compose logs -f app
   ```

## Exposed Services & Ports
Once the stack is up, you can access the following services:

| Service | Address | Description |
|---|---|---|
| **API Engine** | `http://localhost:8080` | The main Spring Boot REST API |
| **Backend Gateway** | `http://localhost:9090` | The API Gateway enforcing rate limits, schema validation, and `x-api-key` security |
| **Grafana** | `http://localhost:3000` | Metrics Dashboards (User: `admin`, Pass: `admin`) |
| **Prometheus**| `http://localhost:9091` | Raw metric scraping target |
| **PostgreSQL**| `localhost:5432` | The database instance |
| **Redis** | `localhost:6379` | Distributed locks cache |

## Assumptions & Design Logic

* **Transaction Processing**: The system uses `TransactionService` to handle core logic in a transactional boundary (`@Transactional`).
* **Charge Calculation**: `ChargeCalculationService` dynamically queries the nearest configured charge rule for the channel, falling back to 0 if none is defined.
* **Idempotency**: Handled using `RedisLockService`. An in-memory concurrent map is used as a fallback if Redis is unreachable. A unique `idempotencyKey` prevents duplicate transaction processing.
* **Database Design**: Uses PostgreSQL with separate tables for `Customer`, `Wallet`, `Transaction`, and `ChargeRule`. `Wallet` balance is updated atomically to prevent race conditions.
* **Error Handling**: A `@RestControllerAdvice` globally captures domain exceptions (e.g., `InsufficientBalanceException`) and maps them to appropriate HTTP status codes (e.g., 400 or 409).
* **Security**: Basic API key security is implemented in the `backend-gateway` via `ApiKeyFilter` (header `x-api-key`, default: `default-api-key-123`), which evaluates requests before schema validation or rate limiting. 

## Testing the API
To test the API endpoints, refer to the included `api-examples.md` document, which contains `curl` commands to:
1. Create a Customer/Wallet
2. Configure dynamic fixed/percentage Charge Rules
3. Process idempotent transactions

You can test them directly against the bare engine on `http://localhost:8080` or through the gateway on `http://localhost:9090` (requires headers like `x-api-key: default-api-key-123`, `Channel`, `AppId`, etc.).

## Architecture Details
For an in-depth look at the architecture, sequence flows, and Entity-Relationship Diagram (ERD), please read the `Architecture-HLD.md` file.

## Stopping the Application
To stop all services and preserve data in your volumes:
```bash
docker compose down
```

To stop all services and **delete all database/redis data** (wipe clean):
```bash
docker compose down -v
```
