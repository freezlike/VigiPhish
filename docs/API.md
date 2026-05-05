# API

Base path: `/api`

## Health

### `GET /api/health`

Endpoint public de supervision locale et de vérification frontend.

Response `200 OK`:

```json
{
  "status": "UP",
  "application": "dssi-phishing-awareness",
  "checkedAt": "2026-05-05T10:15:30Z"
}
```

## Futures Conventions

- Les entrées API doivent utiliser des DTOs validés par Bean Validation.
- Les erreurs doivent passer par une gestion centralisée.
- Les endpoints métier doivent appliquer RBAC.
- Les actions admin doivent produire un audit log.
- Les endpoints publics de formation ne doivent jamais collecter de contenu de formulaire.
