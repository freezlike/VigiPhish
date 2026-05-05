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

La configuration autorise sans authentification:

- `GET /api/health`
- `/api/public/**`

Tous les endpoints admin sont protégés et utilisent `@PreAuthorize` avec les rôles:

- `ROLE_DSSI_ADMIN`
- `ROLE_CAMPAIGN_MANAGER`
- `ROLE_CAMPAIGN_VALIDATOR`
- `ROLE_REPORT_VIEWER`
- `ROLE_AUDITOR`
- `ROLE_USER`

Le squelette ne stocke pas de mots de passe applicatifs. L'intégration d'authentification d'entreprise devra être ajoutée via configuration et fournisseur d'identité interne.

Le frontend applique aussi des guards par rôle pour l'ergonomie et la réduction d'exposition UI. Cette protection ne remplace pas les contrôles backend.

## Tracking

Les tokens de tracking sont:

- Longs.
- Aléatoires.
- Uniques.
- Non devinables.
- Expirables.
- Persistés uniquement sous forme de hash SHA-256.

La création d'une cible de campagne retourne le token brut une seule fois dans la réponse API. La base conserve uniquement `campaign_targets.token_hash` et `expires_at`.

Les endpoints publics de tracking n'acceptent que:

- `EMAIL_OPENED`
- `LINK_CLICKED`
- `SUBMITTED_FORM`
- `TRAINING_VIEWED`
- `QUIZ_COMPLETED`

`EMAIL_SENT` est rejeté sur les endpoints publics. Un token expiré ou inconnu ne révèle pas d'identité utilisateur.

Pour `SUBMITTED_FORM`, le backend force des métadonnées sûres et n'enregistre jamais les champs soumis.

Les pages publiques Angular `/public/training/:token` et `/public/quiz/:token` ne demandent aucun secret et ne transmettent que `TRAINING_VIEWED` ou `QUIZ_COMPLETED`.

## Audit

Toute mutation administrative implémentée produit un enregistrement dans `audit_logs` avec acteur, action, cible, date et détails minimaux.

## Campagnes

Les campagnes doivent être validées avant lancement. Le cycle implémenté impose:

```text
DRAFT -> PENDING_VALIDATION -> VALIDATED -> SCHEDULED -> RUNNING -> COMPLETED
```

Le passage à `VALIDATED` est réservé aux rôles `ROLE_DSSI_ADMIN` et `ROLE_CAMPAIGN_VALIDATOR`. `CANCELLED` est possible avant fin définitive.

## Emails

En production, les envois doivent être limités aux domaines internes autorisés via configuration d'environnement. Mailpit est réservé au développement local.
