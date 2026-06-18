#### D. [audit-logging.md](file:///C:/Users/ARYA%20VAJPAI/OneDrive/Desktop/stationery-management/stationery-management/docs/audit-logging.md)

```markdown
# Audit Logging and Traceability

To maintain security, track item utilization, and assist system administrators, the stationery management application structures its backend logging using unified format standards.

## MDC Correlation and Tracing

When an HTTP request enters the `api-gateway`, a unique transaction correlation ID (e.g., `X-Correlation-Id`) is generated and added to the Mapped Diagnostic Context (MDC). This correlation ID is automatically propagated through downstream Feign calls, allowing engineers to trace a transaction's lifecycle across:

- `api-gateway` -> `request-service` -> `inventory-service`

## Log Format Standard

Logs are written to standard output (`stdout`) in the following format:
`[TIMESTAMP] [LEVEL] [MICROSERVICE-NAME] [CORRELATION-ID] [CLASS-NAME] : [LOG-MESSAGE]`

## Audited Operations

The following critical business operations trigger audit-level logs:

1. **Authentication Events**:
   - Successful user logins (logging email and role).
   - Failed logins (logging email and reason).
   - Admin account registration (logging verified secret code usage).
2. **Stock Modification**:
   - Stock deduction requests (logging request ID, item ID, quantity deducted, and resulting balance).
   - Low stock warnings (triggered when `available_quantity <= minimum_quantity`).
3. **Requisition Handling**:
   - New requests submitted by students.
   - Status transitions (approved/rejected) updated by administrators.
```
