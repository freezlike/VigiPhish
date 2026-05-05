# DSSI Phishing Awareness Platform

Internal application for defensive phishing awareness, campaign governance, training, reporting, and audit support for a DSSI team.

This project is strictly defensive, educational, internal, and RGPD-aware. It must not be changed into an offensive phishing tool, credential collection system, antispam evasion tool, external spoofing service, or clone of an existing phishing product.

## Stack

- Backend: Java 21, Spring Boot 4.x, Maven, Spring Security, Spring Data JPA, Flyway, PostgreSQL
- Frontend: Angular 21, TypeScript, standalone components, lazy routes, HTTP interceptors, guards
- Infrastructure: Docker Compose, PostgreSQL, Mailpit

## Local Startup

1. Create a local environment file:

```bash
cp .env.example .env
```

2. Start the full stack:

```bash
docker compose up --build
```

3. Open the application:

- Frontend: http://localhost:4200
- Backend health: http://localhost:8080/api/health
- Mailpit UI: http://localhost:8025

The frontend calls `/api/health` through its Nginx reverse proxy in Docker. During Angular local development, `proxy.conf.json` forwards `/api` to the backend.

## Verification Commands

Docker build and startup:

```bash
docker compose up --build
```

Backend tests:

```bash
docker compose run --rm backend-test
```

Frontend tests:

```bash
docker compose run --rm frontend-test
```

Local alternatives, when Java 21, Maven, Node.js, and a compatible browser are installed:

```bash
cd backend && mvn test
cd frontend && npm install && npm test
```

## Current Skeleton

- `backend/`: Spring Boot application with `/api/health`, security baseline, modular package layout, and Flyway migration.
- `frontend/`: Angular application showing the platform title and backend health status.
- `docs/`: CDC, architecture, security, RGPD, API, and runbook documentation.
- `docker-compose.yml`: PostgreSQL, Mailpit, backend, frontend, and test helper services.

## Security Guardrails

- Do not store real credentials or simulated form field contents.
- Simulated forms may only record events such as `SUBMITTED_FORM`.
- Tracking tokens must be long, random, unique, expirable, and stored only as hashes.
- Campaign launch must require validation and later four-eyes support.
- Admin actions must produce audit logs.
- Production email delivery must be limited to authorized internal domains.
- No antispam evasion, deceptive external domains, or phishing toolkit integration.
