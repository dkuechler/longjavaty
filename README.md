# longjavaty

[![CI](https://github.com/dkuechler/longjavaty/actions/workflows/ci.yml/badge.svg)](https://github.com/dkuechler/longjavaty/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen?style=flat-square&logo=spring-boot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)

A Spring Boot REST API for tracking health metrics, including workouts, heart rate samples, and body measurements.

## Technical Stack

* **Backend:** Java 21, Spring Boot 3.5, Spring Security, Spring Data JPA
* **Database:** PostgreSQL 15
* **Auth:** JWT-based authentication via Keycloak
* **Infrastructure:** Terraform (AWS ECS Fargate, RDS, VPC)

## Local Setup

### Prerequesites
* Docker & Docker Compose

### Running the Stack
1. Create a `.env` file from `.env.example`.
2. Start the services:
   ```bash
   docker compose up --build
   ```
The API will be available at `http://localhost:8080`.

## AWS Infrastructure

The deployment is managed via Terraform in the `terraform/` directory.

* **Region:** `eu-central-1`
* **Network:** VPC with public subnets
* **Database:** RDS PostgreSQL (t3.micro)
* **Compute:** ECS Fargate

To provision development infrastructure:
```bash
cd terraform/environments/dev
terraform init
terraform apply
```

## Security & API

All endpoints require a valid JWT token issued by Keycloak. User data is synchronized into the local database upon the first authenticated request.

### Documentation
When running with the `dev` profile, Swagger UI is available at `/swagger-ui/index.html`.

### Endpoints
* `POST /workouts`: Bulk record workouts
* `GET /workouts`: List workouts (optional time filters)
* `POST /workouts/metrics/heart-rate`: Record heart rate data
* `POST /measurements`: Record body measurements
* `GET /users/me/data`: GDPR data export
* `DELETE /users/me/data`: User account deletion

## Testing

Run unit tests:
```bash
./mvnw test
```

Run integration tests:
```bash
./mvnw -Pintegration-tests verify
```