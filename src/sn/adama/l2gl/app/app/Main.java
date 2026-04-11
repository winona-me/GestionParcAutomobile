package sn.adama.l2gl.app.app;

import sn.adama.l2gl.app.model.*;
import sn.adama.l2gl.app.repo.InMemoryCrud;
import sn.adama.l2gl.app.service.ParcAutoService;

import java.time.LocalDate;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        // ============================================================
        // 1. CONSTRUCTION DE LA FLOTTE
        // ============================================================

        Vehicule v1 = new Vehicule(1L, "AA-111-BB", "Toyota", 15000, EtatVehicule.DISPONIBLE, 2020);
        Vehicule v2 = new Vehicule(2L, "CC-222-DD", "Renault", 80000, EtatVehicule.EN_PANNE, 2015);
        Vehicule v3 = new Vehicule(3L, "EE-333-FF", "Peugeot", 45000, EtatVehicule.EN_LOCATION, 2018);
        Vehicule v4 = new Vehicule(4L, "GG-444-HH", "Honda", 95000, EtatVehicule.DISPONIBLE, 2012);

        Conducteur c1 = new Conducteur(1L, "Bah", "B-12345");
        Conducteur c2 = new Conducteur(2L, "Zegbelemou", "A-67890");

        Entretien e1 = new Entretien(1L, v2, LocalDate.of(2025, 3, 10), "Vidange moteur", 25000);
        Entretien e2 = new Entretien(2L, v4, LocalDate.of(2026, 1, 20), "Changement freins", 40000);

        Location l1 = new Location(1L, v3, c1, LocalDate.of(2025, 12, 1), 15000);
        l1.terminer(LocalDate.of(2026, 2, 10));

        List<Vehicule> flotte = new ArrayList<>();
        flotte.add(v1);
        flotte.add(v2);
        flotte.add(v3);
        flotte.add(v4);

        ParcAutoService service = new ParcAutoService();

        // ============================================================
        // 2. LES 14 LAMBDAS
        // ============================================================

        System.out.println("\n===== A — TESTS (Predicate) =====");

        // 1. Véhicule disponible ?
        Test<Vehicule> estDisponible = v -> v.getEtat() == EtatVehicule.DISPONIBLE;
        System.out.println("Véhicules disponibles :");
        service.filtrerVehicules(flotte, estDisponible)
                .forEach(v -> System.out.println("  " + v.getImmatriculation()));

        // 2. Véhicule en panne ?
        Test<Vehicule> estEnPanne = v -> v.getEtat() == EtatVehicule.EN_PANNE;
        System.out.println("Véhicules en panne :");
        service.filtrerVehicules(flotte, estEnPanne)
                .forEach(v -> System.out.println("  " + v.getImmatriculation()));

        // 3. Kilométrage > seuil ?
        int seuilKm = 50000;
        Test<Vehicule> kmEleve = v -> v.getKilometrage() > seuilKm;
        System.out.println("Véhicules avec km > " + seuilKm + " :");
        service.filtrerVehicules(flotte, kmEleve)
                .forEach(v -> System.out.println("  " + v.getImmatriculation() + " — " + v.getKilometrage() + " km"));

        // 4. Véhicule à réviser ?
        int seuilAnnee = 2016;
        Test<Vehicule> aReviser = v -> v.getKilometrage() > seuilKm || v.getAnnee() < seuilAnnee;
        System.out.println("Véhicules à réviser :");
        service.filtrerVehicules(flotte, aReviser)
                .forEach(v -> System.out.println("  " + v.getImmatriculation()));

        // 5. Conducteur autorisé ?
        Test<Conducteur> estAutorise = c -> c.getPermis().startsWith("B");
        System.out.println("Conducteur c1 autorisé ? " + estAutorise.tester(c1));
        System.out.println("Conducteur c2 autorisé ? " + estAutorise.tester(c2));

        // ============================================================
        System.out.println("\n===== B — TRANSFORMATIONS (Function) =====");

        // 6. Résumé véhicule
        Transformation<Vehicule, String> resume = v -> v.afficher();
        System.out.println("Résumés :");
        service.mapperVehicules(flotte, resume)
                .forEach(s -> System.out.println("  " + s));

        // 7. Extraire immatriculation
        Transformation<Vehicule, String> extraireImmat = v -> v.getImmatriculation();
        System.out.println("Immatriculations :");
        service.mapperVehicules(flotte, extraireImmat)
                .forEach(s -> System.out.println("  " + s));

        // 8. Calculer âge du véhicule
        Transformation<Vehicule, String> age = v ->
                v.getImmatriculation() + " → " + (LocalDate.now().getYear() - v.getAnnee()) + " ans";
        System.out.println("Âges des véhicules :");
        service.mapperVehicules(flotte, age)
                .forEach(s -> System.out.println("  " + s));

        // 9. Coût total d'un entretien (coût + taxe fixe 5000)
        int taxe = 5000;
        Transformation<Entretien, String> coutTotal = ent ->
                ent.getDescription() + " → coût total : " + (ent.getCout() + taxe);
        System.out.println("Coûts totaux entretiens :");
        System.out.println("  " + coutTotal.transformer(e1));
        System.out.println("  " + coutTotal.transformer(e2));

        // ============================================================
        System.out.println("\n===== C — ACTIONS (Consumer) =====");

        // 10. Marquer véhicule en révision
        Action<Vehicule> marquerRevision = v -> v.setEtat(EtatVehicule.EN_REVISION);
        System.out.println("Avant action — v1 état : " + v1.getEtat());
        marquerRevision.executer(v1);
        System.out.println("Après action — v1 état : " + v1.getEtat());

        // 11. Augmenter kilométrage de 500
        int ajoutKm = 500;
        Action<Vehicule> augmenterKm = v -> v.setKilometrage(v.getKilometrage() + ajoutKm);
        System.out.println("Avant action — v1 km : " + v1.getKilometrage());
        augmenterKm.executer(v1);
        System.out.println("Après action — v1 km : " + v1.getKilometrage());

        // 12. Terminer une location
        Action<Location> terminerLocation = loc -> loc.terminer(LocalDate.now());
        Location l2 = new Location(2L, v1, c2, LocalDate.of(2024, 11, 1), 12000);
        System.out.println("Avant — l2 dateFin : " + l2.getDateFin());
        terminerLocation.executer(l2);
        System.out.println("Après — l2 dateFin : " + l2.getDateFin());

        // ============================================================
        System.out.println("\n===== D — COMPARAISONS (Comparator) =====");

        // 13. Trier par kilométrage croissant
        Comparaison<Vehicule> parKm = (v, w) -> v.getKilometrage() - w.getKilometrage();
        service.trierVehicules(flotte, parKm);
        System.out.println("Flotte triée par km :");
        flotte.forEach(v -> System.out.println("  " + v.getImmatriculation() + " — " + v.getKilometrage()));

        // 14. Trier par immatriculation alphabétique
        Comparaison<Vehicule> parImmat = (v, w) -> v.getImmatriculation().compareTo(w.getImmatriculation());
        service.trierVehicules(flotte, parImmat);
        System.out.println("Flotte triée par immatriculation :");
        flotte.forEach(v -> System.out.println("  " + v.getImmatriculation()));

        // ============================================================
        // ÉTAPE 7 — Unicité et equals/hashCode
        // ============================================================
        {
            ParcAutoService service7 = new ParcAutoService();

            Vehicule va = new Vehicule(1L, "AA-111-BB", "Toyota", 15000, EtatVehicule.DISPONIBLE, 2020);
            Vehicule vb = new Vehicule(2L, "CC-222-DD", "Renault", 80000, EtatVehicule.EN_PANNE, 2015);
            Vehicule vc = new Vehicule(3L, "AA-111-BB", "Honda", 5000, EtatVehicule.DISPONIBLE, 2022); // doublon !

            service7.ajouterVehicule(va);
            service7.ajouterVehicule(vb);

            try {
                service7.ajouterVehicule(vc);
            } catch (IllegalArgumentException e) {
                System.out.println("Doublon refusé : " + e.getMessage());
            }

            System.out.println("va.equals(vc) ? " + va.equals(vc));
            System.out.println("hashCode égaux ? " + (va.hashCode() == vc.hashCode()));
            System.out.println("Véhicules uniques : " + service7.vehiculesUniques().size());

            Set<Vehicule> set = new HashSet<>();
            set.add(va);
            set.add(vb);
            set.add(vc);
            System.out.println("Set size (attendu 2) : " + set.size());
        }

        System.out.println("\n===== ÉTAPE 8 — Map<Long, List<Entretien>> =====");

        {
            ParcAutoService service8 = new ParcAutoService();

            Vehicule va = new Vehicule(1L, "AA-111-BB", "Toyota", 15000, EtatVehicule.DISPONIBLE, 2020);
            Vehicule vb = new Vehicule(2L, "CC-222-DD", "Renault", 80000, EtatVehicule.EN_PANNE, 2015);

            service8.ajouterVehicule(va);
            service8.ajouterVehicule(vb);

            // 2 entretiens pour va
            Entretien e4 = new Entretien(1L, va, LocalDate.of(2025, 1, 10), "Vidange moteur",    25000);
            Entretien e5 = new Entretien(2L, va, LocalDate.of(2025, 6, 20), "Changement freins", 40000);

            // 1 entretien pour vb
            Entretien e6 = new Entretien(3L, vb, LocalDate.of(2025, 3, 5),  "Remplacement pneus", 30000);

            service8.ajouterEntretien(e4);
            service8.ajouterEntretien(e5);
            service8.ajouterEntretien(e6);

            // Entretiens de va (attendu : 2)
            System.out.println("Entretiens de va (" + va.getImmatriculation() + ") :");
            service8.getEntretiens(va.getId())
                    .forEach(e -> System.out.println("  " + e.afficher()));

            // Entretiens de vb (attendu : 1)
            System.out.println("Entretiens de vb (" + vb.getImmatriculation() + ") :");
            service8.getEntretiens(vb.getId())
                    .forEach(e -> System.out.println("  " + e.afficher()));

            // Véhicule sans entretien (attendu : liste vide, pas de crash !)
            System.out.println("Entretiens de id=99 (attendu vide) :");
            List<Entretien> vide = service8.getEntretiens(99L);
            System.out.println("  Taille : " + vide.size()); // 0
        }

        System.out.println("\n===== ÉTAPE 9 — Streams =====");

        {
            ParcAutoService service9 = new ParcAutoService();

            Vehicule va = new Vehicule(1L, "CC-333-AA", "Toyota",  15000, EtatVehicule.DISPONIBLE,  2020);
            Vehicule vb = new Vehicule(2L, "AA-111-BB", "Renault", 80000, EtatVehicule.EN_PANNE,    2015);
            Vehicule vc = new Vehicule(3L, "EE-555-CC", "Peugeot", 45000, EtatVehicule.DISPONIBLE,  2018);
            Vehicule vd = new Vehicule(4L, "BB-222-DD", "Honda",   95000, EtatVehicule.EN_REVISION, 2012);
            Vehicule ve = new Vehicule(5L, "DD-444-EE", "Kia",     60000, EtatVehicule.DISPONIBLE,  2019);

            service9.ajouterVehicule(va);
            service9.ajouterVehicule(vb);
            service9.ajouterVehicule(vc);
            service9.ajouterVehicule(vd);
            service9.ajouterVehicule(ve);

            // 1. Véhicules disponibles
            System.out.println("Véhicules disponibles :");
            service9.vehiculesDisponibles()
                    .forEach(v -> System.out.println("  " + v.getImmatriculation()));

            // 2. Immatriculations triées
            System.out.println("Immatriculations triées :");
            service9.immatriculationsTriees()
                    .forEach(immat -> System.out.println("  " + immat));

            // 3. Top 3 kilométrage
            System.out.println("Top 3 kilométrage :");
            service9.top3ParKilometrage()
                    .forEach(v -> System.out.println("  " + v.getImmatriculation()
                            + " — " + v.getKilometrage() + " km"));
        }

        System.out.println("\n===== ÉTAPE 10 — Statistiques et groupingBy =====");

        {
            ParcAutoService service10 = new ParcAutoService();

            Vehicule va = new Vehicule(1L, "AA-111-BB", "Toyota",  15000, EtatVehicule.DISPONIBLE,  2020);
            Vehicule vb = new Vehicule(2L, "CC-222-DD", "Renault", 80000, EtatVehicule.EN_PANNE,    2015);
            Vehicule vc = new Vehicule(3L, "EE-333-FF", "Peugeot", 45000, EtatVehicule.DISPONIBLE,  2018);
            Vehicule vd = new Vehicule(4L, "GG-444-HH", "Honda",   95000, EtatVehicule.EN_REVISION, 2012);
            Vehicule ve = new Vehicule(5L, "II-555-JJ", "Kia",     60000, EtatVehicule.EN_LOCATION, 2019);

            service10.ajouterVehicule(va);
            service10.ajouterVehicule(vb);
            service10.ajouterVehicule(vc);
            service10.ajouterVehicule(vd);
            service10.ajouterVehicule(ve);

            // Entretiens
            service10.ajouterEntretien(new Entretien(1L, va, LocalDate.of(2025, 1, 10), "Vidange",  20000));
            service10.ajouterEntretien(new Entretien(2L, va, LocalDate.of(2025, 6, 20), "Freins",   35000));
            service10.ajouterEntretien(new Entretien(3L, vb, LocalDate.of(2025, 3, 5),  "Pneus",    15000));
            service10.ajouterEntretien(new Entretien(4L, vc, LocalDate.of(2025, 8, 12), "Courroie", 50000));

            // 1. Kilométrage moyen
            System.out.printf("Kilométrage moyen : %.2f km%n", service10.kilometrageMoyen());

            // 2. Véhicules par état
            System.out.println("\nVéhicules par état :");
            service10.vehiculesParEtat()
                    .forEach((etat, count) ->
                            System.out.println("  " + etat + " : " + count + " véhicule(s)"));

            // 3. Coûts d'entretien par véhicule
            System.out.println("\nCoûts d'entretien par véhicule :");
            service10.coutEntretiensParVehicule()
                    .forEach((immat, total) ->
                            System.out.println("  " + immat + " : " + total + " FCFA"));
        }

        System.out.println("\n===== ETAPE 11 — Polymorphisme et afficher() =====");

        {
            // On met TOUS les objets dans une liste d'Entite
            List<Entite> toutesLesEntites = new ArrayList<>();

            Vehicule va = new Vehicule(1L, "AA-111-BB", "Toyota",  15000, EtatVehicule.DISPONIBLE, 2020);
            Vehicule vb = new Vehicule(2L, "CC-222-DD", "Renault", 80000, EtatVehicule.EN_PANNE,   2015);

            Conducteur ca = new Conducteur(1L, "Bah",         "B-12345");
            Conducteur cb = new Conducteur(2L, "Zegbelemou",  "A-67890");

            Entretien ea = new Entretien(1L, va, LocalDate.of(2025, 1, 10), "Vidange moteur",    25000);
            Entretien eb = new Entretien(2L, vb, LocalDate.of(2025, 6, 20), "Changement freins", 40000);

            Location la = new Location(1L, va, ca, LocalDate.of(2025, 12, 1), 15000);
            la.terminer(LocalDate.of(2026, 2, 10));

            // On ajoute tout dans la même liste !
            toutesLesEntites.add(va);
            toutesLesEntites.add(vb);
            toutesLesEntites.add(ca);
            toutesLesEntites.add(cb);
            toutesLesEntites.add(ea);
            toutesLesEntites.add(eb);
            toutesLesEntites.add(la);

            // Un seul appel afficher() → chaque objet sait comment s'afficher !
            System.out.println("Toutes les entites du parc :");
            toutesLesEntites.forEach(e -> System.out.println("  " + e.afficher()));

            // Démo validation — messages explicites
            System.out.println("\nTest validations :");
            try {
                new Vehicule(3L, "", "Toyota", 15000, EtatVehicule.DISPONIBLE, 2020);
            } catch (IllegalArgumentException e) {
                System.out.println("  " + e.getMessage());
            }

            try {
                new Vehicule(4L, "ZZ-999-AA", "Toyota", -500, EtatVehicule.DISPONIBLE, 2020);
            } catch (IllegalArgumentException e) {
                System.out.println("  " + e.getMessage());
            }

            try {
                new Entite(null) { // classe anonyme pour tester Entite directement
                    public String afficher() { return "test"; }
                };
            } catch (IllegalArgumentException e) {
                System.out.println("  " + e.getMessage());
            }
        }

        System.out.println("\n===== ETAPE 12 — CRUD Generique =====");

        {
            // Un seul InMemoryCrud pour les véhicules
            InMemoryCrud<Vehicule> vehiculeRepo = new InMemoryCrud<>();

            // Un seul InMemoryCrud pour les conducteurs
            InMemoryCrud<Conducteur> conducteurRepo = new InMemoryCrud<>();

            Vehicule va = new Vehicule(1L, "AA-111-BB", "Toyota",  15000, EtatVehicule.DISPONIBLE, 2020);
            Vehicule vb = new Vehicule(2L, "CC-222-DD", "Renault", 80000, EtatVehicule.EN_PANNE,   2015);
            Vehicule vc = new Vehicule(3L, "EE-333-FF", "Peugeot", 45000, EtatVehicule.DISPONIBLE, 2018);

            Conducteur ca = new Conducteur(1L, "Bah",        "B-12345");
            Conducteur cb = new Conducteur(2L, "Zegbelemou", "A-67890");

            // CREATE
            System.out.println("--- CREATE ---");
            vehiculeRepo.create(va);
            vehiculeRepo.create(vb);
            vehiculeRepo.create(vc);
            conducteurRepo.create(ca);
            conducteurRepo.create(cb);
            System.out.println("Vehicules crees : " + vehiculeRepo.findAll().size());
            System.out.println("Conducteurs crees : " + conducteurRepo.findAll().size());

            // CREATE — doublon
            try {
                vehiculeRepo.create(va); // id=1 existe deja !
            } catch (IllegalArgumentException e) {
                System.out.println("Doublon refuse : " + e.getMessage());
            }

            // READ
            System.out.println("\n--- READ ---");
            Optional<Vehicule> trouve = vehiculeRepo.read(1L);
            trouve.ifPresent(v -> System.out.println("Trouve : " + v.afficher()));

            Optional<Vehicule> absent = vehiculeRepo.read(99L);
            System.out.println("Id 99 present ? " + absent.isPresent()); // false

            // UPDATE
            System.out.println("\n--- UPDATE ---");
            Vehicule vaModifie = new Vehicule(1L, "AA-111-BB", "Toyota", 20000, EtatVehicule.EN_REVISION, 2020);
            vehiculeRepo.update(vaModifie);
            vehiculeRepo.read(1L).ifPresent(v -> System.out.println("Apres update : " + v.afficher()));

            // UPDATE — id inexistant
            try {
                vehiculeRepo.update(new Vehicule(99L, "ZZ-999-ZZ", "Kia", 5000, EtatVehicule.DISPONIBLE, 2022));
            } catch (IllegalArgumentException e) {
                System.out.println("Update refuse : " + e.getMessage());
            }

            // DELETE
            System.out.println("\n--- DELETE ---");
            System.out.println("Avant delete : " + vehiculeRepo.findAll().size() + " vehicules");
            vehiculeRepo.delete(2L);
            System.out.println("Apres delete : " + vehiculeRepo.findAll().size() + " vehicules");

            // DELETE — id inexistant
            try {
                vehiculeRepo.delete(99L);
            } catch (IllegalArgumentException e) {
                System.out.println("Delete refuse : " + e.getMessage());
            }

            // FIND ALL
            System.out.println("\n--- FIND ALL ---");
            System.out.println("Tous les vehicules :");
            vehiculeRepo.findAll()
                    .forEach(v -> System.out.println("  " + v.afficher()));
        }
    }


}