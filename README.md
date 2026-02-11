Pour lancer :
```mvn jetty:run```

Pour les tests :
```mvn test```

Script SQL pour la base de données :
`./src/main/resources/schema.sql`

# Architecture
Application web Java EE classique en MVC simple.

- `Servlet` = couche controleur HTTP.
- `JSP` (avec JSTL) = couche vue, rendu des pages.
- `Service` = regles metier et orchestration.
- `Repository` (JPA/Hibernate) = acces aux donnees.
- `Entity` = modèles métiers (`Annonce`, `User`, `Category`, `AnnonceStatus`).

Flux principal:

- Requete HTTP vers une servlet (`/login`, `/annonces`, etc.).
- Validation serveur des formulaires (objet form + validateur).
- En cas d'erreur de validation: `forward` vers la même JSP avec erreurs champ/globales.
- En cas de succes POST: pattern PRG (`sendRedirect`).
- Messages one-shot via `FlashUtil` (session -> request -> suppression).

Sécurité:

- Authentification par session (`authUser`).
- `AuthFilter` protège les routes privées et laisse passer login/logout + statiques.

Persistance:

- `SessionFactory` cree les `EntityManager`.
- Les services ouvrent une transaction pour les ecritures.
- Requetes de liste paginees au niveau repository.

Tests:

- `mvn test` execute les tests unitaires (service, servlet, filter).
- Les tests utilisent JUnit 5 + Mockito.

# Problèmes rencontrées
Comprendres dans un premier temps l'architecture et les responsabilités des différents éléments pour pouvoir les organiser correctement.
De la même façon, nommer certains composants à été compliqué et j'ai du regarder sur internet pour des références ou demander au professeur.
Finalement, utiliser efficacement des technologies que je ne maitrise pas completement a été compliqué et j'ai du les apprendre un peu sur le tas.
Par exemple JSTL, je n'utilisati pas forcément efficacement toutes les balises ou d'une façon optimal et le mélange entre le vieux et nouveau code m'a forcé a faire plusieurs gros refactor pour égaliser toute la base de code sur plusieurs aspects.

# Solutions
Les problèmes n'était pas vraiment technique mais plus logique j'ai pus me reposer sur différentes ressources.
Bien évidement en posant des questions au professeur mais aussi en utilisant l'IA ou le site de Jean-Michel Doudoux sur certains aspects.
