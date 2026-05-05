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

L'implémentation publique de tracking remplace les métadonnées d'un `SUBMITTED_FORM` par une information sûre indiquant que le contenu de formulaire n'a pas été stocké.

## Acces

Les résultats nominatifs doivent être réservés aux rôles autorisés. Les vues reporting non opérationnelles doivent privilégier l'agrégation ou l'anonymisation.

Les rapports administratifs implémentés exposent des agrégats par campagne et par type d'événement. Les endpoints publics ne retournent pas d'identité utilisateur.

Les pages internes de sensibilisation chargées par token retournent uniquement du contenu pédagogique lié à la campagne. Elles ne retournent ni email, ni nom, ni identifiant utilisateur, ni hash de token.

L'interface frontend évite d'afficher les tokens publics en dehors de la réponse de création d'une cible, où ils sont signalés comme affichés une seule fois.

## Conservation

Un module `retention` est prévu pour gérer les durées de conservation. Les durées exactes doivent être validées avec le DPO et la politique interne.

Les données locales de développement sont créées par Flyway avec des adresses `example.internal` et ne doivent pas être remplacées par des données réelles sans base légale et validation interne.

## Transparence

Les contenus pédagogiques doivent indiquer clairement le cadre interne de sensibilisation lorsqu'un utilisateur atteint une page de sensibilisation ou de formation.

## Securite Des Donnees

- Configuration par variables d'environnement.
- Aucun secret en dur.
- Tokens de tracking hachés avec expiration.
- Audit des actions administratives.
- Accès par rôles.
