# Architecture

## Vue D'Ensemble

L'application est séparée en trois couches:

- Frontend Angular 21 servi par Nginx.
- Backend Spring Boot 4.x exposant une API REST sous `/api`.
- PostgreSQL avec migrations Flyway.

Mailpit est utilisé uniquement pour les tests locaux d'email.

## Backend

Package racine: `fr.dssi.phishingawareness`.

Structure modulaire prévue:

- `auth`
- `users`
- `groups`
- `campaigns`
- `templates`
- `tracking`
- `landingpages`
- `quizzes`
- `reports`
- `audit`
- `settings`
- `retention`
- `mail`
- `shared`

Chaque module métier doit suivre la forme `controller -> service -> repository`, avec DTOs séparés des entités et mappers si nécessaire.

## Frontend

Le frontend utilise des composants standalone, le routage lazy-loaded, des services HTTP, des interceptors et des guards.

Structure initiale:

- `core/models`
- `core/services`
- `core/interceptors`
- `core/guards`
- `features/home`

## Infrastructure Docker

- `postgres`: base de données applicative.
- `mailpit`: SMTP et interface locale.
- `backend`: API Spring Boot.
- `frontend`: Angular compilé et servi par Nginx.
- `backend-test` et `frontend-test`: services de vérification.

Le reverse proxy Nginx du frontend transmet `/api/*` vers `backend:8080`.
