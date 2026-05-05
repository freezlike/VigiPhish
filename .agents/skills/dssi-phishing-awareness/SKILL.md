---
name: dssi-phishing-awareness
description: Use this skill when working on the internal DSSI phishing awareness platform to enforce defensive, educational, privacy-first, RGPD-compliant constraints.
metadata:
  short-description: DSSI phishing awareness guardrails
---

# DSSI Phishing Awareness Skill

## Description

Use this skill when working on the internal DSSI phishing awareness platform.

The goal is to build a defensive, educational, internal, privacy-conscious application for phishing awareness campaigns.

Do not create offensive phishing capabilities.

## Mandatory behavior

When implementing or reviewing code for this project:

1. Preserve the defensive and educational scope.
2. Never store real credentials.
3. Never implement password harvesting.
4. Never implement anti-detection, anti-spam evasion, domain spoofing, or external phishing delivery.
5. Enforce RBAC.
6. Enforce campaign validation before launch.
7. Hash tracking tokens before persistence.
8. Keep audit logs for administrative actions.
9. Use DTOs and validation for all API inputs.
10. Keep Docker Compose functional.
11. Keep documentation synchronized.

## Backend rules

Use:
- Java 21
- Spring Boot 4.x
- Maven
- Spring Security
- Spring Data JPA
- Flyway
- PostgreSQL
- JUnit 5
- Testcontainers

Backend package structure should be modular:

- auth
- users
- groups
- campaigns
- templates
- tracking
- landingpages
- quizzes
- reports
- audit
- settings
- retention
- mail

Each module should follow:
- controller
- service
- repository
- dto
- entity
- mapper when useful

## Frontend rules

Use:
- Angular 21
- TypeScript
- Reactive Forms
- Guards
- Interceptors
- Lazy-loaded routes
- Reusable components

Frontend pages:
- Dashboard
- Campaign list
- Campaign detail
- Campaign editor
- Email template management
- Landing page management
- Quiz management
- User import
- Reports
- Audit logs
- Public training page
- Public quiz page

## Docker rules

The project must run with:

```bash
docker compose up --build
