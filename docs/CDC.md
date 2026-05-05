# Cahier Des Charges

## Objectif

Créer une plateforme interne de sensibilisation au phishing pour une DSSI. L'application sert à organiser des campagnes pédagogiques, afficher des pages de formation, mesurer des événements de sensibilisation, produire des rapports, et conserver une piste d'audit.

## Perimetre Autorise

- Gestion des utilisateurs internes, groupes, campagnes, modèles pédagogiques, pages de formation, quiz, rapports et audit.
- Simulation défensive limitée aux domaines internes autorisés.
- Collecte d'événements de sensibilisation: envoi, ouverture, clic, soumission simulée, consultation de formation, quiz.
- Aucune collecte de contenu saisi dans les formulaires simulés.

## Hors Perimetre

- Collecte de mots de passe ou secrets.
- Evasion antispam, contournement de détection, usurpation externe.
- Domaines externes trompeurs.
- Intégration d'outils phishing open source ou commerciaux.

## Roles Cibles

- `ROLE_DSSI_ADMIN`
- `ROLE_CAMPAIGN_MANAGER`
- `ROLE_CAMPAIGN_VALIDATOR`
- `ROLE_REPORT_VIEWER`
- `ROLE_AUDITOR`
- `ROLE_USER`

## Fonctionnalites Initiales

- Endpoint backend `/api/health`.
- Page frontend d'accueil affichant le titre de l'application et l'état backend.
- Base PostgreSQL initialisée par Flyway.
- Mailpit pour les tests locaux d'email.
- Architecture prête pour RBAC, audit, validation de campagne et RGPD.

## Exigences De Lancement

Le projet doit démarrer localement avec:

```bash
docker compose up --build
```
