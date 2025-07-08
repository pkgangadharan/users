# Multi-Tenant User Manager

This is a Spring Boot + ReactJS application that supports managing users across multiple tenants using in-memory H2 databases.

---

## 🧩 Features

- Multi-tenancy with dynamic H2 in-memory DBs (via `x-tenant-id` header)
- REST API for CRUD operations
- Scheduled export to Apache Parquet files (every 5 min)
- React UI with tenant validation via `?tenant=xyz`
- Liquibase-managed schema
- TSID-like ID generation (long values)
- JUnit tests for service layer

---

## 📦 Backend Setup

```bash
cd backend
./mvnw spring-boot:run
