# RGPD

## Donnees Personnelles

Les données nominatives doivent rester limitées au besoin pédagogique et opérationnel:

- Email professionnel.
- Nom d'affichage.
- Rôle applicatif.
- Groupe interne.
- Evénements de sensibilisation.

## Minimisation

Les formulaires simulés ne doivent jamais conserver les champs saisis par l'utilisateur. L'événement autorisé est uniquement `SUBMITTED_FORM`, accompagné de métadonnées non sensibles si nécessaire.

## Acces

Les résultats nominatifs doivent être réservés aux rôles autorisés. Les vues reporting non opérationnelles doivent privilégier l'agrégation ou l'anonymisation.

## Conservation

Un module `retention` est prévu pour gérer les durées de conservation. Les durées exactes doivent être validées avec le DPO et la politique interne.

## Transparence

Les contenus pédagogiques doivent indiquer clairement le cadre interne de sensibilisation lorsqu'un utilisateur atteint une page de formation.

## Securite Des Donnees

- Configuration par variables d'environnement.
- Aucun secret en dur.
- Tokens de tracking hachés.
- Audit des actions administratives.
- Accès par rôles.
