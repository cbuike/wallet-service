# Wallet Service – Spring Boot & PostgreSQL

A simple backend service that supports wallet operations such as creating wallets, credit/debit transactions, and atomic transfers between wallets.

This project was built as an interview coding task and demonstrates:
- Transactional integrity
- Idempotent operations
- Proper money handling (minor units)
- Clean layered architecture

## Tech Stack
- Java 17
- Docker
- Maven
- Spring Boot 3+
- PostgreSQL
- JPA / Hibernate
- OpenAPI (Swagger UI)
- Unit Tests
- JaCoCo for coverage reports

---

## Key Design Decisions

- **Money is stored in minor units** (e.g. kobo, cents) using `int`
- **Database transactions** ensure atomic updates
- **Idempotency** is enforced using a unique `idempotency_key`
- Transfers debit sender and credit receiver **atomically**
- PostgreSQL constraints provide an extra layer of safety
- 

## Project Structure
```
wallet-service
├── src
│   └── main
│       ├── java
│       │   └── com
│       │        └── walletservice
│       │               ├── controller
│       │               ├── service
│       │               ├── repository
│       │               ├── model
│       │               ├── exception
│       │               ├── config
│       │               └── dto
│       └── resources
│           ├── application.yml
└── README.md
```



---

## Prerequisites

Ensure you have the following installed:

- **Java 17**
  ```bash
  java -version

- Docker
- Maven
- PostgreSQL 14+


## How to run 
1. Start the PostgreSQL database using Docker (run inside project directory):
   ```bash
   docker compose up -d
2. Run: `mvn clean install` to package and run test cases.
3. Run: `mvn spring-boot:run` or `java -jar target/wallet-service-0.0.1-SNAPSHOT.jar` to start the application.
4. Access API documentation (Swagger UI) at: http://localhost:8083/swagger-ui.html.
5. Goto `target/site/jacoco/index.html` for test coverage details.

## APIs with Curl Examples

1. Create Wallet.
```
   curl -X POST http://localhost:8083/wallets
```
- returns created wallet UUID as id and balance

2. Credit Wallet.
```
curl -X 'POST' \
  'http://localhost:8083/transactions' \
  -H 'Content-Type: application/json' \
  -d '{
  "walletId": "UUID",
  "amount": 10000,
  "type": "CREDIT",
  "idempotencyKey": "ID_kEY_0922"
}'
```

3. Debit Wallet.
```
curl -X 'POST' \
  'http://localhost:8083/transactions' \
  -H 'Content-Type: application/json' \
  -d '{
  "walletId": "UUID",
  "amount": 150,
  "type": "DEBIT",
  "idempotencyKey": "ID_kEY_0923"
}'
```

4. Transfer between wallets.
```
curl -X POST http://localhost:8083/transactions/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "senderWalletId": "UUID1",
    "receiverWalletId": "UUID2",
    "amount": 150,
    "idempotencyKey": "transfer-1"
  }'
```

5. Get all wallets
```
   curl -X GET http://localhost:8083/wallets
```

6. Get wallet by ID
```
   curl -X GET http://localhost:8083/wallets/{UUID}
```

7. Get all transactions
```
   curl -X GET http://localhost:8083/transactions
```

