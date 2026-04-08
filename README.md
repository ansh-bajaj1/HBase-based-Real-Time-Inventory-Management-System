# HBase-based Real-Time Inventory Management System

A complete Big Data lab project that combines Spring Boot, Apache Kafka, Apache HBase, Hadoop HDFS, and React to deliver event-driven, real-time inventory management.

## 1. Project Overview

This system manages inventory updates using an event-first architecture:

- API requests create inventory events.
- Kafka transports events reliably.
- Kafka consumer applies events to HBase (`inventory` table).
- Every change is written to a time-series audit table (`inventory_logs`).
- React dashboard polls backend APIs for live inventory status and low stock alerts.

The platform is fully Dockerized and runs with one command using Docker Compose.

## 2. Tech Stack

- Backend: Java 17 + Spring Boot 3
- Big Data Storage: Apache HBase
- Distributed File System: Hadoop HDFS
- Stream Processing: Apache Kafka + Zookeeper
- Frontend: React + Vite + Tailwind CSS + Axios
- API: REST
- Containerization: Docker + Docker Compose
- Bonus: Spark analytics job scaffold + JUnit test

## 3. Architecture

```text
+---------------------+        +----------------------+        +----------------------+
| React Dashboard     |  HTTP  | Spring Boot Backend  | Event  | Kafka Topic           |
| (Tailwind + Axios)  +------->+ REST API + Producer  +------->+ inventory-updates     |
+----------+----------+        +----------+-----------+        +-----------+----------+
           ^                              |                                |
           |                              | consume                        v
           |                     +--------+---------+              +-------+----------+
           |                     | Kafka Consumer   |              | Zookeeper        |
           |                     | (Retry + Fallback)|             +------------------+
           |                     +--------+---------+
           |                              |
           |                              v
           |                     +--------+---------+
           |                     | Apache HBase     |
           |                     | inventory        |
           |                     | inventory_logs   |
           |                     +--------+---------+
           |                              |
           |                              v
           |                     +--------+---------+
           |                     | Hadoop HDFS      |
           |                     | CSV / analytics  |
           |                     +------------------+
```

## 4. HBase Schema Design

### Table 1: `inventory`

- RowKey: `item_id`
- Column Family: `details`
- Columns:
  - `name`
  - `category`
  - `price`
  - `stock_quantity`
  - `last_updated`

### Table 2: `inventory_logs`

- RowKey: `item_id_timestamp`
- Column Family: `logs`
- Columns:
  - `change_type` (`ADD`, `UPDATE`, `DELETE`)
  - `quantity_changed`
  - `timestamp`

HBase auto-creates both tables at backend startup using `TableInitializer`.

## 5. Kafka Design

- Topic: `inventory-updates`
- Producer: `InventoryEventProducer` publishes stock events.
- Consumer: `InventoryEventConsumer` consumes and updates HBase.
- Fault tolerance:
  - Kafka consumer retries (3 attempts with backoff).
  - Producer failure fallback applies direct HBase write.

## 6. Implemented Features

### Core Features

1. Add inventory item
2. Update stock in real time
3. Delete item
4. Get single item details
5. Low stock alert API
6. Live tracking dashboard (4-second polling)

### Advanced Features

7. Kafka producer for inventory updates
8. Kafka consumer for real-time HBase updates
9. Time-series stock tracking (`inventory_logs`)
10. Bulk CSV upload (`/bulk-upload`)
11. REST API request logging filter
12. Fault tolerance via retry and fallback logic

### Bonus Features

- Spark analytics starter job: `analytics/spark/top_selling_items.py`
- Unit test sample: `InventoryMapperTest`
- JWT authentication with register/login and protected inventory APIs

## 7. API Documentation

Base URL: `http://localhost:8080`

### Authentication

- Register: `POST /auth/register`
- Login: `POST /auth/login`
- All `/item/*` and `/items/*` endpoints require `Authorization: Bearer <token>`

Register example:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Login response:

```json
{
  "token": "<jwt-token>"
}
```

### 1) Add Item

- Method: `POST`
- Endpoint: `/item/add`
- Body:

```json
{
  "itemId": "ITM-2001",
  "name": "Gaming Headset",
  "category": "Electronics",
  "price": 3299.00,
  "stockQuantity": 25
}
```

### 2) Update Stock

- Method: `PUT`
- Endpoint: `/item/update/{id}`
- Body:

```json
{
  "stockQuantity": 8
}
```

### 3) Get Item

- Method: `GET`
- Endpoint: `/item/{id}`

### 4) Delete Item

- Method: `DELETE`
- Endpoint: `/item/{id}`

### 5) Get All Items

- Method: `GET`
- Endpoint: `/items/all`

### 6) Get Low Stock Alerts

- Method: `GET`
- Endpoint: `/items/low-stock`

### 7) Bulk Upload CSV

- Method: `POST`
- Endpoint: `/bulk-upload`
- Form-data key: `file`
- CSV format:

```csv
item_id,name,category,price,stock_quantity
ITM-1001,Wireless Mouse,Electronics,599.00,90
```

A ready Postman collection is included in `postman_collection.json`.

## 8. Project Structure

```text
root/
 ├── backend/
 │   ├── src/main/java/com/bda/inventory/
 │   │   ├── config/
 │   │   ├── controller/
 │   │   ├── dto/
 │   │   ├── exception/
 │   │   ├── kafka/
 │   │   ├── logging/
 │   │   ├── model/
 │   │   ├── repository/
 │   │   └── service/
 │   ├── src/main/resources/application.yml
 │   ├── src/test/java/com/bda/inventory/service/
 │   ├── Dockerfile
 │   └── pom.xml
 ├── frontend/
 │   ├── src/components/
 │   ├── src/pages/
 │   ├── src/services/
 │   ├── src/styles/
 │   ├── Dockerfile
 │   ├── nginx.conf
 │   └── package.json
 ├── kafka/
 │   └── README.md
 ├── hbase/
 │   └── schema.hbase
 ├── data/
 │   └── sample_inventory.csv
 ├── analytics/
 │   └── spark/top_selling_items.py
 ├── docker-compose.yml
 ├── postman_collection.json
 └── README.md
```

## 9. Docker Setup

### Prerequisites

- Docker Desktop (or Docker Engine + Compose plugin)
- Minimum recommended RAM: 8 GB

### Start all services

```bash
docker compose up --build -d
```

### Check running containers

```bash
docker compose ps
```

### Access URLs

- Frontend dashboard: `http://localhost:5173`
- Backend API: `http://localhost:8080`
- HBase Master UI: `http://localhost:16010`
- Hadoop NameNode UI: `http://localhost:9870`

### Stop services

```bash
docker compose down
```

### Stop and remove volumes (full reset)

```bash
docker compose down -v
```

## 10. HBase Validation Commands

Open HBase shell:

```bash
docker exec -it hbase hbase shell
```

Run:

```bash
list
scan 'inventory'
scan 'inventory_logs'
```

## 11. CSV Bulk Upload Example

```bash
curl -X POST http://localhost:8080/bulk-upload \
  -F "file=@data/sample_inventory.csv"
```

## 12. Sample End-to-End Flow

1. Add item via `/item/add`.
2. Backend publishes event to Kafka.
3. Kafka consumer processes event.
4. Consumer writes item to `inventory` and audit row to `inventory_logs`.
5. React dashboard polling reflects change in near real-time.

## 13. Screenshots Guide

Capture these after running project:

1. Dashboard home with stats and table.
2. Low stock alert panel showing below-threshold items.
3. HBase shell `scan 'inventory'` output.
4. Kafka console consumer showing `inventory-updates` events.

## 14. Fault Tolerance Details

- Retry on Kafka consumer processing with exponential backoff.
- Producer fallback writes directly to HBase if Kafka publish fails.
- Global exception handling with structured error payloads.
- Request logging filter for API traceability.

## 15. Security (JWT)

- Spring Security configured in stateless mode.
- JWT filter validates bearer token for each secured API request.
- Passwords are stored as BCrypt hashes.
- Auth endpoints are public, inventory endpoints are protected.
- Token secret and expiration are configurable via environment variables:
  - `JWT_SECRET`
  - `JWT_EXPIRATION_MS`

## 16. Future Enhancements

- JWT authentication and RBAC for API and dashboard.
- Redis caching for high-read endpoints.
- Grafana + Prometheus observability dashboards.
- WebSocket/SSE push updates instead of polling.
- Spark Structured Streaming for advanced real-time analytics.
- CI/CD pipeline with integration tests and container image scanning.

## 17. Useful Commands

### Build backend tests locally

```bash
cd backend
mvn test
```

### View Kafka topic events

```bash
docker exec -it kafka kafka-console-consumer.sh \
  --bootstrap-server kafka:9092 \
  --topic inventory-updates \
  --from-beginning
```

## 18. Notes for Lab Evaluation

- The project is modular and production-style with controller-service-repository layering.
- Uses event-driven architecture with Kafka and HBase persistence.
- Includes mandatory APIs, UI, Docker orchestration, sample data, and docs.
