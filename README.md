Pour lancer :
```mvn spring-boot:run```

Pour lancer avec Docker :
```docker compose up --build```

Pour les tests :
```mvn test```

Pour verifier tout le projet (unitaires + integration) :
```mvn -B clean verify```

Script SQL pour la base de données :
`./src/main/resources/schema.sql`

# Architecture
Application web Spring Boot en mode API REST + pages HTML simples.

- `Controller` = endpoints HTTP (`AuthController`, `AnnonceController`, `CategoryController`, `MetaController`).
- `DTO` + `Mapper` (MapStruct) = format JSON d'entree/sortie.
- `Service` = regles metier (validations, droits, transitions de statut).
- `Repository` (Spring Data JPA) = acces aux donnees + pagination/tri.
- `Entity` = modeles metier (`Annonce`, `User`, `Category`, `AnnonceStatus`).
- `HTML + JS` = front leger qui consomme l'API.

Flux principal:

- Requete HTTP vers un endpoint REST (`/api/auth/login`, `/api/annonces`, etc.).
- Validation des donnees via Bean Validation + verifs metier dans les services.
- Mapping DTO <-> entités via MapStruct.
- En cas d'erreur: reponse JSON centralisée via `GlobalExceptionHandler`.
- En cas de succes: reponse JSON + code HTTP adapte (`200`, `201`, `204`).

Sécurité:

- Login via `POST /api/auth/login`.
- Generation d'un token JWT signe avec expiration.
- `JwtAuthenticationFilter` valide le bearer token et remplit le contexte Spring Security.
- Endpoints publics: login, Swagger, Actuator health/info.
- Endpoints proteges: `/api/**` (hors login), avec roles `ROLE_USER` et `ROLE_ADMIN`.

Persistance:

- Base principale : PostgreSQL (local ou Docker).
- Recherche annonces : filtres + pagination + tri avec Specifications.
- Endpoint meta : `/api/meta/annonces` pour exposer les champs utilisables (tri/filtres).

Tests:

- `mvn test` execute les tests unitaires.
- `mvn -B clean verify` exécute aussi les tests d'integration (`*IT`).
- Profil test sur H2 (`application-test.yml`) pour des tests rapides et isoles.

Observabilite et documentation:

- Swagger UI : `/swagger-ui`.
- Actuator : `/actuator/health` et `/actuator/info`.
- Logging transversal des services via AOP + correlation id.

Industrialisation:

- Docker : `Dockerfile` + `docker-compose.yml` (application + PostgreSQL).
- CI GitHub Actions sur `push` et `pull_request` vers `main`.
- Pipeline en Java 17/21 avec `mvn -B clean verify`.
- Artifact principal publie : `master-annonce-jar`.

# Problemes rencontres
Le point le plus delicat a ete la migration globale vers Spring Boot sans perdre la logique metier du TP precedent.
La partie securite (JWT + filtres + droits) a demande plusieurs ajustements pour garder un comportement clair.
La mise en place des filtres dynamiques, du tri et de la pagination a aussi demande du temps pour rester propre.
Enfin, aligner la CI, Docker et les tests d'integration sur une configuration stable a pris plusieurs iterations.

# Solutions
Le projet a ete garde en architecture en couches (controller, service, repository) pour separer clairement les responsabilites.
Les DTO + MapStruct ont ete utilises partout pour eviter d'exposer directement les entites.
La securite a ete centralisee dans Spring Security (config + filtre JWT + regles metier dans les services).
Pour les tests et la CI, l'usage de H2 en profil test permet d'avoir des executions simples, rapides et reproductibles.
