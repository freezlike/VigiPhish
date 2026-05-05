# Security

## Principes

Cette application est strictement défensive, pédagogique et interne. Elle ne doit pas fournir de capacités offensives.

## Interdictions

- Stocker de vrais mots de passe.
- Demander un vrai mot de passe à un utilisateur.
- Enregistrer le contenu saisi dans un formulaire simulé.
- Implémenter de l'évasion antispam ou anti-détection.
- Utiliser des domaines externes trompeurs.
- Intégrer un outil phishing open source ou commercial.

## Authentification Et Autorisation

La configuration initiale autorise uniquement `GET /api/health` sans authentification. Les futurs endpoints doivent appliquer RBAC avec les rôles:

- `ROLE_DSSI_ADMIN`
- `ROLE_CAMPAIGN_MANAGER`
- `ROLE_CAMPAIGN_VALIDATOR`
- `ROLE_REPORT_VIEWER`
- `ROLE_AUDITOR`
- `ROLE_USER`

## Tracking

Les tokens de tracking doivent être:

- Longs.
- Aléatoires.
- Uniques.
- Non devinables.
- Expirables.
- Persistés uniquement sous forme de hash.

La migration initiale prévoit `tracking_tokens.token_hash` et ne contient pas de colonne pour stocker un token brut.

## Audit

Toute action administrative doit produire un enregistrement dans `audit_logs` avec acteur, action, cible, date et détails minimaux.

## Campagnes

Les campagnes doivent être validées avant lancement. Le modèle initial prévoit des statuts de validation et un validateur distinct pour préparer un mode four-eyes.

## Emails

En production, les envois doivent être limités aux domaines internes autorisés via configuration d'environnement. Mailpit est réservé au développement local.
