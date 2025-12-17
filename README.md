# longjavaty-backend

[![CI](https://github.com/dkuechler/longjavaty/actions/workflows/ci.yml/badge.svg)](https://github.com/dkuechler/longjavaty/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen?style=flat-square&logo=spring-boot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=flat-square&logo=docker)

This is a Spring Boot REST API for storing health metrics (workouts + measurements) per user.
Auth is JWT-based (Keycloak).

## Highlights

* **Stack:** Java 21, Spring Boot 3.5, Spring Security (JWT), Spring Data JPA, PostgreSQL
* **Local infra:** Docker Compose (app + Postgres + Keycloak)
* **Domain:** workouts + heart rate samples + measurements
* **Privacy:** user data export + delete endpoints (GDPR style)

## Quickstart (Docker)

Prereqs: Docker Desktop.

Create a ".env" file (or export env vars) for Compose:

* POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD
* FRONTEND_ORIGINS (comma separated)


	docker compose up --build

* API: http://localhost:8080
* Keycloak (dev): http://localhost:8081

PostgreSQL is initialized from "src/main/resources/schema.sql" + "src/main/resources/data.sql".

## Deploy notes (AWS)

In production I run this as a container (ECS style) and connect it to a managed Postgres database (RDS).
I do not commit any AWS endpoints or secrets. Prod config is provided via environment variables.

Prod setup:

* Set SPRING_PROFILES_ACTIVE=rds (uses "src/main/resources/application-rds.properties")
* Provide RDS_DB_HOST, RDS_DB_PORT, RDS_DB_NAME, RDS_DB_USERNAME, RDS_DB_PASSWORD
* Provide KEYCLOAK_ISSUER_URI and KEYCLOAK_JWK_SET_URI
* Provide FRONTEND_ORIGINS

## Security

* Every request is authenticated.
* The backend validates bearer tokens via the configured Keycloak issuer.
* On authenticated requests, a filter synchronizes the current user into the local DB (creates the user row on first request).

Default local configuration points at a Keycloak realm named "longjavaty-realm".

## API

Base path: /api

* POST /workouts: record workouts (bulk)
* GET /workouts: list workouts (optional "from" / "to" ISO timestamps)
* POST /workouts/metrics/heart-rate: record heart rate samples for workouts
* GET /workouts/metrics/heart-rate?workoutId=...: list heart rate samples
* POST /measurements: record a measurement
* GET /measurements?measurementType=...: list measurements (optional "from" / "to")
* GET /users/me/data: export all user data
* DELETE /users/me/data: delete user + all stored data

## Run without Docker

You'll need a PostgreSQL database + a JWT issuer (Keycloak or equivalent).


	./mvnw spring-boot:run

Runtime configuration is primarily in "src/main/resources/application.properties".

## Tests


	./mvnw test

Integration tests (same as CI):

	./mvnw -Pintegration-tests verify

## License

MIT. See "LICENSE".
