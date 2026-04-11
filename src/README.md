=== A — TESTS (Predicate : retournent true ou false) ===

1. "Véhicule disponible ?"
   → On vérifie si l'état du véhicule est DISPONIBLE.
   → Utile pour savoir si on peut louer ce véhicule.

2. "Véhicule en panne ?"
   → On vérifie si l'état du véhicule est EN_PANNE.
   → Utile pour filtrer les véhicules hors service.

3. "Kilométrage > seuil ?"
   → On compare le kilométrage du véhicule à une valeur limite.
   → Utile pour détecter les véhicules trop usés.

4. "Véhicule à réviser ?"
   → On vérifie si (km > seuil) OU (année < seuilAnnée).
   → Un véhicule vieux OU trop utilisé doit passer en révision.

5. "Conducteur autorisé ?"
   → On vérifie si le permis du conducteur correspond à un format.
   → Ex : permis qui commence par 'B' = autorisé à conduire une voiture.

=== B — TRANSFORMATIONS (Function : prennent un objet, retournent une valeur) ===

6. "Résumé véhicule" → String
   → On construit une phrase résumant les infos du véhicule.
   → Ex : "Toyota Corolla (2018) - 45000 km - DISPONIBLE"

7. "Extraire immatriculation" → String
   → On récupère uniquement l'immatriculation du véhicule.
   → Utile pour afficher ou comparer les plaques.

8. "Calculer l'âge du véhicule" → int
   → On soustrait l'année du véhicule à l'année actuelle.
   → Ex : 2025 - 2018 = 7 ans.

9. "Coût total d'un entretien" → int
   → On ajoute une taxe fixe au coût de l'entretien.
   → Ex : coût + 5000 FCFA de taxe.

=== C — ACTIONS (Consumer : font quelque chose, ne retournent rien) ===

10. "Marquer véhicule en révision"
    → On change l'état du véhicule à EN_REVISION.
    → Modifie l'objet directement, pas de retour.

11. "Augmenter kilométrage d'un véhicule de X"
    → On ajoute X au kilométrage actuel du véhicule.
    → Simule un trajet effectué.

12. "Terminer une location"
    → On renseigne la dateFin de la location.
    → La date de fin doit être >= à la date de début.

=== D — COMPARAISONS (Comparator : comparent 2 éléments) ===

13. "Comparer deux véhicules par kilométrage (ordre croissant)"
    → Le véhicule avec le moins de km passe en premier.
    → Utile pour trier une liste du moins usé au plus usé.

14. "Comparer deux véhicules par immatriculation (ordre alphabétique)"
    → On compare les plaques comme des chaînes de caractères.
    → Utile pour afficher une liste triée par immatriculation.

Mini-conclusion:
Séparer les comportements en test / transformation / action / comparaison
est essentiel car chaque catégorie correspond à une interface fonctionnelle
Java (Predicate, Function, Consumer, Comparator). Cela force à réfléchir à 
ce que fait réellement chaque comportement avant de coder, et permet de réutiliser 
ces comportements facilement avec les streams et lambdas plus tard.


* ================================================================
* ÉTAPE 2 — Comportements → Groupes → Signatures
* ================================================================
*
* # | Comportement                        | Groupe      | Signature
* --|-------------------------------------|-------------|---------------------------
* 1 | Véhicule disponible ?               | Predicate   | Predicate<Vehicule>
* 2 | Véhicule en panne ?                 | Predicate   | Predicate<Vehicule>
* 3 | Kilométrage > seuil ?               | Predicate   | Predicate<Vehicule>
* 4 | Véhicule à réviser ?                | Predicate   | Predicate<Vehicule>
* 5 | Conducteur autorisé ?               | Predicate   | Predicate<Conducteur>
* --|-------------------------------------|-------------|---------------------------
* 6 | Résumé véhicule                     | Function    | Function<Vehicule, String>
* 7 | Extraire immatriculation            | Function    | Function<Vehicule, String>
* 8 | Calculer âge du véhicule            | Function    | Function<Vehicule, Integer>
* 9 | Coût total d'un entretien           | Function    | Function<Entretien, Integer>
* --|-------------------------------------|-------------|---------------------------
  *10 | Marquer véhicule en révision        | Consumer    | Consumer<Vehicule>
  *11 | Augmenter kilométrage de X          | Consumer    | Consumer<Vehicule>
  *12 | Terminer une location               | Consumer    | Consumer<Location>
* --|-------------------------------------|-------------|---------------------------
  *13 | Comparer par kilométrage            | Comparator  | Comparator<Vehicule>
  *14 | Comparer par immatriculation        | Comparator  | Comparator<Vehicule>
* ================================================================

Pourquoi 14 comportements couverts par ~4 interfaces seulement ?
Parce qu'en Java fonctionnel, ce qui compte ce n'est pas ce que fait le comportement,
mais sa forme — c'est-à-dire ce qu'il prend en entrée et ce qu'il retourne. 
Un Predicate<Vehicule> peut vérifier si un véhicule est disponible, en panne, ou trop 
kilométré — c'est toujours la même forme : Vehicule → boolean. Java réutilise donc 
les mêmes interfaces pour des dizaines de comportements différents, ce qui rend le code 
générique, flexible et réutilisable.

Qu'est-ce qu'une interface fonctionnelle "garantit" au compilateur ?
Elle garantit qu'il n'y a qu'une seule méthode abstraite, ce qui permet au 
compilateur d'accepter une expression lambda à la place d'un objet. Sans 
@FunctionalInterface, Java ne saurait pas à quelle méthode associer le lambda. 
C'est ce contrat unique qui rend possible l'écriture de v -> v.getEtat() == DISPONIBLE
au lieu de créer une classe anonyme entière.

Pourquoi ces méthodes rendent le code réutilisable ?
Parce qu'elles séparent le "quoi faire" (filtrer, trier, transformer) du "comment le 
faire" (quel test, quelle transformation). La méthode filtrerVehicules n'a jamais besoin 
d'être recopiée — on l'appelle avec des règles différentes à chaque fois. C'est le 
principe Open/Closed : le code est ouvert à l'extension (nouvelles règles) mais fermé à
la modification.

Quel problème résout Map que List ne résout pas bien ?
La List oblige à parcourir tous les éléments pour en trouver un — si tu 
as 10 000 véhicules, tu fais 10 000 comparaisons dans le pire cas. La Map résout 
ça avec un accès direct par clé en temps constant — peu importe si tu as 10 ou 
10 000 véhicules, la recherche est toujours aussi rapide. C'est exactement comme un index en 
base de données.

Pourquoi retourner une liste vide plutôt que null ?
Retourner null force chaque appelant à vérifier if (liste != null) 
avant de l'utiliser — si on oublie une seule fois c'est un NullPointerException garanti.
Une liste vide se comporte exactement comme une liste normale : size() retourne 0, forEach 
ne fait rien, pas de crash. C'est le principe "Never return null for collections" — une 
collection vide est toujours préférable à null.


Quel est l'intérêt de séparer "source → opérations → résultat" ?
Ce découpage rend chaque étape indépendante et lisible — on peut ajouter, retirer ou modifier 
une opération sans toucher aux autres. C'est aussi plus expressif : on décrit ce qu'on veut 
(filtrer les disponibles, trier, limiter) plutôt que comment le faire (boucles, conditions, 
indices). Enfin les Streams sont paresseux — les opérations intermédiaires ne s'exécutent que 
quand le résultat final est demandé, ce qui permet des optimisations automatiques.

Pourquoi groupingBy est plus lisible qu'une Map manuelle ?
Avec une Map manuelle on gère soi-même la création des clés, l'initialisation des valeurs 
et les mises à jour — c'est du code technique qui cache l'intention. Avec groupingBy on 
exprime directement ce qu'on veut : "groupe par état, compte les éléments". Le code devient 
une description du besoin plutôt qu'une série d'instructions techniques. C'est aussi beaucoup 
moins risqué — pas d'oubli d'initialisation, pas de NullPointerException.

Pourquoi afficher() est abstraite au lieu d'être générique dans Entite ?
Si afficher() était définie dans Entite, elle n'aurait accès qu'à id — elle ne pourrait pas
afficher immatriculation, nom, cout... car ces attributs appartiennent aux sous-classes. En
la rendant abstraite, on force chaque sous-classe à définir sa propre version avec ses propres
attributs. C'est le principe du polymorphisme : une interface commune (afficher()), des 
comportements différents selon le vrai type de l'objet.

Que permet le CRUD générique qu'un repo spécifique ne permet pas ?
Un repo spécifique (VehiculeRepo, ConducteurRepo...) oblige à réécrire exactement le même 
code pour chaque classe — même logique, même structure, juste le type qui change. Le CRUD 
générique InMemoryCrud<T> écrit cette logique une seule fois et fonctionne pour n'importe 
quel type qui a un getId(). Si demain on ajoute une classe Assurance, il suffit d'écrire new 
InMemoryCrud<Assurance>() — zéro code supplémentaire. C'est le principe DRY : Don't Repeat 
Yourself.

En quoi Optional force le développeur à traiter le cas absent ?
Avec null, rien n'empêche d'écrire vehicule.getMarque() sans vérifier — le compilateur ne dit 
rien et le crash arrive à l'exécution. Avec Optional, on ne peut pas accéder directement à la
valeur — on est obligé de passer par orElse, orElseThrow ou ifPresent, ce qui force à définir 
explicitement ce qui se passe si l'objet est absent. C'est une erreur impossible à ignorer par
inadvertance.

Pourquoi un record est adapté à un objet de rapport ?
Un rapport est par nature en lecture seule — on ne modifie pas les données d'un rapport après 
sa génération. Le record garantit cette immuabilité automatiquement sans effort. De plus il 
génère toString() automatiquement ce qui est parfait pour l'affichage, et equals/hashCode pour
comparer des lignes. C'est exactement ce dont on a besoin pour un DTO de reporting : léger, 
immuable, lisible


Quel choix de conception rend le projet le plus solide ?
La combinaison Optional + enum + interfaces fonctionnelles est ce qui rend le projet le plus 
robuste. Exemple concret : dans demarrerLocation(), on utilise Optional.orElseThrow() pour 
garantir que le véhicule existe, puis l'enum EtatVehicule pour vérifier qu'il est DISPONIBLE 
— impossible de louer un véhicule en panne par erreur. Enfin la règle de révision est injectée
via une lambda Test<Vehicule> — on peut changer la règle sans toucher au service. Ces trois 
mécanismes ensemble éliminent les null, les états invalides et le code dupliqué.