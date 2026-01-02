# longjavaty

[![CI](https://github.com/dkuechler/longjavaty/actions/workflows/ci.yml/badge.svg)](https://github.com/dkuechler/longjavaty/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen?style=flat-square&logo=spring-boot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)

A Spring Boot REST API for tracking health metrics, featuring JIT user synchronization with Keycloak and GDPR-compliant data management.

## Tech Stack

- **Backend:** Java 21, Spring Boot 3.5, Spring Security, JPA/Hibernate
- **Database:** PostgreSQL 15
- **Auth:** OIDC / JWT (Keycloak)
- **Infrastructure:** AWS (ECS Fargate, RDS, ECR)
- **IaC:** Terraform

## Local Setup

1. Copy `.env.example` to `.env` and fill in the values.
2. Spin up the stack:
   ```bash
   docker compose up --build
   ```
The API is available on port `8080`.

## Cloud Infrastructure

The infrastructure is defined in the [`terraform/`](./terraform) directory.

- **ECS Fargate:** Task execution using Spot instances for cost savings.
- **RDS:** Managed PostgreSQL instance.
- **Networking:** Custom VPC with multi-AZ public subnets.

## Security & GDPR

- **Auth:** Endpoints are protected via JWT. Users are automatically synced to the local database on their first login.
- **GDPR:** Includes endpoints for data portability (JSON export) and account erasure.
- **API Docs:** Swagger UI is available at `/swagger-ui/index.html` in the `dev` profile.

## Testing

- **Unit Tests:** `./mvnw test`
- **Integration Tests:** `./mvnw -Pintegration-tests verify`