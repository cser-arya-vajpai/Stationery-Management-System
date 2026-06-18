---

Open [database-schema.md](file:///C:/Users/ARYA%20VAJPAI/OneDrive/Desktop/stationery-management/stationery-management/docs/database-schema.md), replace all its contents with this schema guide:

````markdown
# Database Schema Design

The system implements the database-per-service pattern, with three isolated MySQL databases hosted within the `stationery-mysql` container.

```mermaid
erDiagram
    USERS ||--o{ REQUESTS : "submits"
    STATIONERY-ITEMS ||--o{ REQUESTS : "requested in"

    USERS {
        bigint id PK
        varchar name
        varchar email UK
        varchar password
        varchar role
    }

    STATIONERY-ITEMS {
        bigint id PK
        varchar name
        varchar category
        varchar unit
        int available_quantity
        int minimum_quantity
        timestamp created_at
        timestamp updated_at
        varchar last_updated_by
    }

    REQUESTS {
        bigint id PK
        varchar request_id
        varchar student_email
        bigint item_id
        varchar item_name
        int requested_quantity
        varchar status
        varchar rejection_reason
        varchar remarks
        timestamp created_at
        timestamp updated_at
    }
```
````
