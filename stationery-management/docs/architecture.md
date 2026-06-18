# System Architecture

The College University Stationery Management System uses a modular Spring Boot microservices architecture to manage authorization, inventory item tracking, and stationery requisitions.

## Services Layout

```mermaid
graph TD
    Client[React Frontend - Port 3000] -->|HTTP Request| Gateway[API Gateway - Port 8085]
    Gateway -->|Route /api/auth/**| Auth[Auth Service - Port 8081]
    Gateway -->|Route /api/inventory/**| Inventory[Inventory Service - Port 8082]
    Gateway -->|Route /api/requests/**| Request[Request Service - Port 8083]

    Auth -.->|Register| Discovery[Eureka Server - Port 8761]
    Inventory -.->|Register| Discovery
    Request -.->|Register| Discovery
    Gateway -.->|Register| Discovery

    Auth -->|DB Access| MySQL[(MySQL Container - Port 3306/3307)]
    Inventory -->|DB Access| MySQL
    Request -->|DB Access| MySQL

    Request -->|Feign Client| Inventory

    Config[Config Server - Port 8888] -.->|Distribute Properties| Auth
    Config -.->|Distribute Properties| Inventory
    Config -.->|Distribute Properties| Request
```
