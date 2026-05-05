# Runbook

## Demarrage Local

```bash
cp .env.example .env
docker compose up --build
```

Services:

- Frontend: http://localhost:4200
- Backend health: http://localhost:8080/api/health
- Mailpit: http://localhost:8025
- PostgreSQL: localhost:5432

## Verification

Build et démarrage Docker:

```bash
docker compose up --build
```

Tests backend:

```bash
docker compose run --rm backend-test
```

Tests frontend:

```bash
docker compose run --rm frontend-test
```

## Arret

```bash
docker compose down
```

Pour supprimer les volumes locaux:

```bash
docker compose down -v
```

## Depannage

- Si le backend ne démarre pas, vérifier que le service `postgres` est healthy.
- Si le frontend affiche le backend indisponible, vérifier `backend` et le proxy Nginx `/api`.
- Si les migrations échouent, consulter les logs backend et l'état du volume PostgreSQL.

## Contraintes Production

- Remplacer tous les secrets locaux par des secrets d'infrastructure.
- Restreindre les domaines email avec `APP_INTERNAL_EMAIL_DOMAINS`.
- Activer une authentification d'entreprise.
- Vérifier RBAC, audit logs, conservation et anonymisation reporting avant ouverture.
