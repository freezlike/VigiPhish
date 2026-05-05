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

Si `.env` surcharge les ports, utiliser les valeurs locales correspondantes. Exemple de ce poste: frontend `4205`, backend `8085`, Mailpit UI `9025`.

## Verification

Build et démarrage Docker:

```bash
docker compose up --build
```

Vérification santé backend:

```bash
curl -s http://127.0.0.1:${BACKEND_PORT:-8080}/api/health
```

Tests backend:

```bash
docker compose run --rm backend-test
```

Tests frontend:

```bash
docker compose run --rm frontend-test
```

Test manuel d'une page de sensibilisation:

1. Créer une page dans `Admin > Pages sensibilisation`.
2. Créer ou modifier une campagne et sélectionner cette page.
3. Ajouter une cible de campagne pour obtenir le token brut affiché une seule fois.
4. Ouvrir `http://<frontend-host>/public/awareness/<token>`.
5. Vérifier dans le rapport campagne les événements `LINK_CLICKED`, `TRAINING_VIEWED` et `TRAINING_COMPLETED`.

Après ajout ou modification de specs frontend, reconstruire l'image de test si nécessaire:

```bash
docker compose build frontend-test
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
- Si les migrations échouent, consulter les logs backend et l'état du volume PostgreSQL avec `docker compose logs backend`.
- Ne pas modifier une migration Flyway déjà appliquée; ajouter une nouvelle migration `Vx__...sql`.

## Contraintes Production

- Remplacer tous les secrets locaux par des secrets d'infrastructure.
- Restreindre les domaines email avec `APP_INTERNAL_EMAIL_DOMAINS`.
- Activer une authentification d'entreprise.
- Vérifier RBAC, audit logs, conservation et anonymisation reporting avant ouverture.
