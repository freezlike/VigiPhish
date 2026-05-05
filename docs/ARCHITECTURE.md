# Architecture

## Vue D'Ensemble

L'application est séparée en trois couches:

- Frontend Angular 21 servi par Nginx.
- Backend Spring Boot 4.x exposant une API REST sous `/api`.
- PostgreSQL avec migrations Flyway.

Mailpit est utilisé uniquement pour les tests locaux d'email.

## Backend

Package racine: `fr.dssi.phishingawareness`.

Structure modulaire:

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

Les modules métier implémentés suivent `controller -> service -> repository`, avec DTOs séparés des entités. Les contrôleurs restent fins; les règles de cycle de vie, de token, d'audit et de sécurité publique sont dans les services.

Modules backend actifs:

- `users`: utilisateurs internes, rôles applicatifs, activation.
- `groups`: groupes d'utilisateurs internes.
- `campaigns`: campagnes et cycle `DRAFT -> PENDING_VALIDATION -> VALIDATED -> SCHEDULED -> RUNNING -> COMPLETED`, avec annulation contrôlée.
- `templates`: modèles email pédagogiques.
- `landingpages`: pages de sensibilisation internes, informatives, liées aux campagnes et servies par token sans fuite d'identité.
- `quizzes`: quiz de sensibilisation.
- `tracking`: cibles de campagne, génération de tokens, validation de token, événements publics autorisés.
- `reports`: rapports agrégés.
- `audit`: journalisation des mutations admin.
- `settings`: paramètres système non secrets.
- `shared`: santé, sécurité, CORS et erreurs centralisées.

## Frontend

Le frontend utilise des composants standalone, le routage lazy-loaded, des services HTTP, des interceptors et des guards.

Structure:

- `core/models`
- `core/services`
- `core/interceptors`
- `core/guards`
- `shared/components`
- `features/admin`
- `features/public`

Routes principales:

- `/admin`: tableau de bord.
- `/admin/campaigns`: liste des campagnes.
- `/admin/campaigns/new` et `/admin/campaigns/:id/edit`: création et édition.
- `/admin/campaigns/:id`: détail, timeline, actions de validation/lancement et cibles.
- `/admin/email-templates`, `/admin/email-templates/new`, `/admin/email-templates/:id/edit`, `/admin/email-templates/:id/preview`.
- `/admin/landing-pages`, `/admin/landing-pages/new`, `/admin/landing-pages/:id/edit`: pages de sensibilisation avec aperçu texte sécurisé.
- `/admin/quizzes`, `/admin/quizzes/new`, `/admin/quizzes/:id/edit`.
- `/admin/user-import`: import CSV sans mot de passe.
- `/admin/reports`: rapport campagne agrégé.
- `/admin/audit-logs`: journal d'audit.
- `/public/training/:token` et `/public/awareness/:token`: page interne de sensibilisation chargée depuis la campagne via token.
- `/public/quiz/:token`: quiz public pédagogique.

Composants réutilisables:

- `page-header`
- `status-badge`
- `confirm-dialog`
- `data-table`
- `empty-state`
- `loading-state`

Les guards frontend appliquent les rôles attendus pour masquer les routes non autorisées côté interface. Les interceptors préparent l'envoi d'un bearer token si l'authentification d'entreprise en fournit un et centralisent la journalisation des erreurs HTTP.

## Infrastructure Docker

- `postgres`: base de données applicative.
- `mailpit`: SMTP et interface locale.
- `backend`: API Spring Boot.
- `frontend`: Angular compilé et servi par Nginx.
- `backend-test` et `frontend-test`: services de vérification.

Le reverse proxy Nginx du frontend transmet `/api/*` vers `backend:8080`.

## Donnees Et Migrations

Flyway gère le schéma PostgreSQL:

- `V1__initial_schema.sql`: tables socles.
- `V2__business_modules_and_dev_seed.sql`: tables de groupes/cibles/paramètres et données locales de développement.
- `V3__align_token_hash_type.sql`: alignement du type `token_hash` attendu par Hibernate.
- `V4__system_settings_uuid_identifier.sql`: identifiant UUID primaire pour les paramètres système.
- `V5__campaign_awareness_pages.sql`: liaison campagne -> page de sensibilisation et événement `TRAINING_COMPLETED`.

Hibernate est configuré en validation de schéma; l'application ne crée pas implicitement les tables au runtime.
