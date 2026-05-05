# AGENTS.md — Phishing Awareness Platform

## Mission du projet

Ce dépôt contient une application interne de sensibilisation au phishing destinée à une DSSI.

L’application doit rester strictement défensive, pédagogique, interne et conforme RGPD.

Il est interdit de transformer cette application en outil offensif, outil de collecte d’identifiants, outil d’évasion antispam, outil d’usurpation externe ou clone d’un produit phishing existant.

## Stack

Backend:
- Java 21
- Spring Boot 4.x
- Maven
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- JUnit 5
- Testcontainers

Frontend:
- Angular 21
- TypeScript
- Reactive Forms
- Guards
- Interceptors
- Interface admin responsive

Infrastructure:
- Docker Compose
- PostgreSQL
- Mailpit
- Pas de clé `version` dans docker-compose.yml
- Aucun secret en dur

## Règles sécurité impératives

- Ne jamais stocker de vrais mots de passe.
- Ne jamais demander à l’utilisateur de saisir un vrai mot de passe.
- Un formulaire simulé ne doit enregistrer que l’événement `SUBMITTED_FORM`, jamais le contenu saisi.
- Les tokens de tracking doivent être longs, aléatoires, uniques, non devinables, expirables et stockés sous forme de hash.
- Les résultats nominatifs doivent être accessibles uniquement aux rôles autorisés.
- Prévoir l’agrégation/anonymisation pour les rôles reporting si nécessaire.
- Toute action admin doit produire un audit log.
- Les campagnes doivent être validées avant lancement.
- Prévoir un mode four-eyes validation.
- En production, les emails doivent être limités aux domaines internes autorisés.
- Ne pas implémenter de mécanisme d’évasion antispam.
- Ne pas utiliser de domaines externes trompeurs.
- Ne pas intégrer d’outil phishing open source ou commercial.

## Rôles

- ROLE_DSSI_ADMIN
- ROLE_CAMPAIGN_MANAGER
- ROLE_CAMPAIGN_VALIDATOR
- ROLE_REPORT_VIEWER
- ROLE_AUDITOR
- ROLE_USER

## Qualité backend

- Architecture Controller -> Service -> Repository.
- DTOs séparés des entités.
- Validation via Bean Validation.
- Gestion d’erreurs centralisée.
- Pas de logique métier dans les contrôleurs.
- Tests unitaires sur les services.
- Tests d’intégration sur endpoints critiques.
- Migrations Flyway obligatoires.
- Configuration via variables d’environnement.

## Qualité frontend

- Angular standalone components si pertinent.
- Lazy loading.
- Guards par rôle.
- Interceptor auth.
- Interceptor erreurs.
- Reactive Forms.
- Composants réutilisables.
- Gestion loading/error/empty.
- Pas de logique métier lourde dans les composants.

## Documentation obligatoire

Maintenir à jour :
- README.md
- docs/CDC.md
- docs/ARCHITECTURE.md
- docs/SECURITY.md
- docs/RGPD.md
- docs/API.md
- docs/RUNBOOK.md

## Commandes attendues

Le projet doit démarrer avec :

```bash
docker compose up --build