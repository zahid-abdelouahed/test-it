## Nom et Prenom
Zahid Abdelouahed

## Bugs corrigés

1. `borrowBook` n'envoyait pas de mail au membre lors d'un emprunt : ajout de `mailService.sendMail(member, "Livre emprunté", ...)`.
2. `findByMemberAndStatus` utilisait `OR` au lieu de `AND` : la requête renvoyait tous les livres du membre et tous les livres empruntés par n'importe qui.
3. `BookController.createBook` mettait le titre à la place de l'auteur : `request.author` au lieu de `request.title`.
4. `POST /books/{id}/borrow` appelait `returnBook` au lieu de `borrowBook`.

## Bugs trouvés, non corrigés

1. `borrowBook` vérifie les livres `RENDU` au lieu des livres `EMPRUNTE` : un membre peut emprunter plusieurs livres en même temps.
2. `returnBook` envoie le mail du référent au membre, donc le référent ne reçoit rien.
3. `returnBook` ne vérifie pas que c'est bien l'emprunteur qui rend le livre.

## Corrections de configuration

L'application et les tests Spring ne démarraient pas à cause d'incohérences entre Liquibase et les entités :

- Ajout de `constraintName` sur les clés étrangères Liquibase (`fk_members_referent`, `fk_book_borrower`).
- `referent_id` passé de `uuid` à `bigint` (même type que `id`).
- Colonne `user` renommée `username` (nom du champ de l'entité, et `USER` est un mot réservé SQL).
- `@Table(name = "members")` sur `Member` (la table Liquibase s'appelle `members`).
- `GenerationType.IDENTITY` au lieu de `SEQUENCE` sur `Book` (la table est en auto-increment).
- `build.gradle` : ajout de l'agent Mockito (`mockitoAgent`) pour les tests.

## Tests

| Fichier | Type | Ce qui est testé |
|---|---|---|
| `BookServiceTest` | unitaire, Mockito, sans Spring ni base | emprunter un livre disponible : statut `EMPRUNTE`, emprunteur, mail envoyé |
| `BookRepositoryIT` | `@DataJpaTest`, vraie base H2 | `findByMemberAndStatus` avec deux membres (abdel, alexi) et quatre livres (Harry Potter saisons 1 et 2) |
| `BookServiceIT` | `@SpringBootTest`, contexte complet + H2 | emprunter puis rendre un livre, puis relire le résultat en base |
| `BookControllerIT` | `@SpringBootTest` + MockMvc, service mocké (`@MockitoBean`) | créer un livre puis l'emprunter, et vérifier que le controller appelle le service avec les bons arguments |

Dans `BookRepositoryIT`, chaque livre élimine une mauvaise réponse :
- saison 1 empruntée par abdel : seul résultat attendu,
- saison 2 rendue par abdel : même membre, autre statut,
- saison 1 empruntée par alexi : même statut, autre membre,
- saison 2 disponible : aucun emprunteur.

