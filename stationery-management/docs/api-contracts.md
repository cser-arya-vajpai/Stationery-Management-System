---

Open [api-contracts.md](file:///C:/Users/ARYA%20VAJPAI/OneDrive/Desktop/stationery-management/stationery-management/docs/api-contracts.md), replace all its contents with this complete API contract specification:

````markdown
# API Contracts

All microservice APIs communicate via JSON and propagate authorization credentials using JWT bearer tokens in the `Authorization` header.

## 1. Authentication Service (`auth-service`)

### Register User

- **Method & URL**: `POST /api/auth/register`
- **Request Payload**:

```json
{
  "name": "Arya Vajpai",
  "email": "student@college.edu",
  "password": "securepassword123",
  "role": "STUDENT",
  "adminSecretCode": ""
}
```
````
