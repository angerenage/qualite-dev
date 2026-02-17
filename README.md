Pour lancer :
```mvn "-Djava.security.auth.login.config=./jaas.conf" jetty:run```

Pour les tests :
```mvn test```

Script SQL pour la base de données :
`./src/main/resources/schema.sql`

# Architecture
Application web Java EE, mais maintenant en mode API REST + pages HTML simples.

- `JAX-RS` (Jersey) = couche controleur HTTP (`/api/...`).
- `Resource` = endpoints REST (`AuthResource`, `AnnonceResource`, etc.).
- `DTO` + `Mapper` = format JSON d'entree/sortie.
- `Service` = regles metier (validations, droits, transitions de statut).
- `Repository` (JPA/Hibernate) = acces aux donnees.
- `Entity` = modeles metier (`Annonce`, `User`, `Category`, `AnnonceStatus`).
- `HTML + JTX` = front leger qui consomme l'API.

Flux principal:

- Requete HTTP vers une resource REST (`/api/login`, `/api/annonces`, etc.).
- Validation des donnees via Bean Validation + verifs metier dans les services.
- En cas d'erreur: reponse JSON propre via les `ExceptionMapper` (`400`, `401`, `403`, `404`, `409`...).
- En cas de succes: reponse JSON + code HTTP adapte (`200`, `201`, `204`).

Sécurité:

- Login via JAAS (`MasterAnnonceLogin`) dans `AuthService`.
- Generation d'un token bearer en memoire avec expiration (`TokenService`).
- `AuthTokenFilter` valide le token via JAAS (`MasterAnnonceToken`) puis remplit le `SecurityContext`.
- Endpoints publics: login + lecture des annonces. Les ecritures demandent un token valide.

Persistance:

- `SessionFactory` cree les `EntityManager`.
- Les services ouvrent/ferment les transactions pour les ecritures.
- Pagination et requetes faites au niveau repository.

Tests:

- `mvn test` execute les tests unitaires/integration (service, resources REST, filter, repository).
- Les tests utilisent JUnit 5 + Mockito.

# Problèmes rencontrées
Comprendres dans un premier temps l'architecture et les responsabilités des différents éléments pour pouvoir les organiser correctement.
De la même façon, nommer certains composants à été compliqué et j'ai du regarder sur internet pour des références ou demander au professeur.
Finalement, utiliser efficacement des technologies que je ne maitrise pas completement a été compliqué et j'ai du les apprendre un peu sur le tas.
Par exemple JSTL, je n'utilisati pas forcément efficacement toutes les balises ou d'une façon optimal et le mélange entre le vieux et nouveau code m'a forcé a faire plusieurs gros refactor pour égaliser toute la base de code sur plusieurs aspects.
J'ai aussi eu un probleme avec JAAS au lancement: sans la config explicite, les domaines JAAS n'etaient pas trouves et l'auth ne demarrait pas.
Pour ce qui est front, je ne savais pas trop quoi faire et je ne savais même pas si on devais en faire un vraiment, j'ai finalement décidé d'en faire un pour me simplifier les tests.
Je ne savais pas non plus si je devais faire les routes pour les catégories étant donné que ce n'était pas demandé dans le sujet, j'ai finalement décidé de les faire pour garder quelque chose plus proche du code d'avant.

# Solutions
Les problèmes n'était pas vraiment technique mais plus logique j'ai pus me reposer sur différentes ressources.
Bien évidement en posant des questions au professeur mais aussi en utilisant l'IA ou le site de Jean-Michel Doudoux sur certains aspects.
J'ai du ajouter `-Djava.security.auth.login.config=./jaas.conf` dans la commande Maven pour lancer correctement afin qu'il trouve les domaines JAAS, information que j'avais zappé dans le sujet.
Pour le front j'ai donc fait générer la plupart du code HTML et JS par IA pour gagner du temps, le front n'étant pas l'objet principal du TP.
