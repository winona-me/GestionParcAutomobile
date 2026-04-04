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
