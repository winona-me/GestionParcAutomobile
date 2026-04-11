package sn.adama.l2gl.app.app;

import sn.adama.l2gl.app.model.*;
import sn.adama.l2gl.app.repo.InMemoryCrud;
import sn.adama.l2gl.app.service.ParcAutoService;

import java.time.LocalDate;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        // ============================================================
        // FLOTTE INITIALE — étapes 2 à 6
        // ============================================================

        Vehicule v1 = new Vehicule(1L, "AA-111-BB", "Toyota",  15000, EtatVehicule.DISPONIBLE,  2020);
        Vehicule v2 = new Vehicule(2L, "CC-222-DD", "Renault", 80000, EtatVehicule.EN_PANNE,    2015);
        Vehicule v3 = new Vehicule(3L, "EE-333-FF", "Peugeot", 45000, EtatVehicule.EN_LOCATION, 2018);
        Vehicule v4 = new Vehicule(4L, "GG-444-HH", "Honda",   95000, EtatVehicule.DISPONIBLE,  2012);

        Conducteur c1 = new Conducteur(1L, "Bah",        "B-12345");
        Conducteur c2 = new Conducteur(2L, "Zegbelemou", "A-67890");

        Entretien e1 = new Entretien(1L, v2, LocalDate.of(2025, 3, 10), "Vidange moteur",    25000);
        Entretien e2 = new Entretien(2L, v4, LocalDate.of(2026, 1, 20), "Changement freins", 40000);

        Location loc1 = new Location(1L, v3, c1, LocalDate.of(2025, 12, 1), 15000);
        loc1.terminer(LocalDate.of(2026, 2, 10));

        List<Vehicule> flotte = new ArrayList<>();
        flotte.add(v1);
        flotte.add(v2);
        flotte.add(v3);
        flotte.add(v4);

        ParcAutoService service = new ParcAutoService();

        // ============================================================
        // LES 14 LAMBDAS
        // ============================================================

        System.out.println("\n===== A — TESTS (Predicate) =====");

        Test<Vehicule> estDisponible = v -> v.getEtat() == EtatVehicule.DISPONIBLE;
        System.out.println("Vehicules disponibles :");
        service.filtrerVehicules(flotte, estDisponible)
                .forEach(v -> System.out.println("  " + v.getImmatriculation()));

        Test<Vehicule> estEnPanne = v -> v.getEtat() == EtatVehicule.EN_PANNE;
        System.out.println("Vehicules en panne :");
        service.filtrerVehicules(flotte, estEnPanne)
                .forEach(v -> System.out.println("  " + v.getImmatriculation()));

        int seuilKm = 50000;
        Test<Vehicule> kmEleve = v -> v.getKilometrage() > seuilKm;
        System.out.println("Vehicules avec km > " + seuilKm + " :");
        service.filtrerVehicules(flotte, kmEleve)
                .forEach(v -> System.out.println("  " + v.getImmatriculation()
                        + " - " + v.getKilometrage() + " km"));

        int seuilAnnee = 2016;
        Test<Vehicule> aReviser = v -> v.getKilometrage() > seuilKm || v.getAnnee() < seuilAnnee;
        System.out.println("Vehicules a reviser :");
        service.filtrerVehicules(flotte, aReviser)
                .forEach(v -> System.out.println("  " + v.getImmatriculation()));

        Test<Conducteur> estAutorise = c -> c.getPermis().startsWith("B");
        System.out.println("Conducteur c1 autorise ? " + estAutorise.tester(c1));
        System.out.println("Conducteur c2 autorise ? " + estAutorise.tester(c2));

        System.out.println("\n===== B — TRANSFORMATIONS (Function) =====");

        Transformation<Vehicule, String> resume = v -> v.afficher();
        System.out.println("Resumes :");
        service.mapperVehicules(flotte, resume)
                .forEach(s -> System.out.println("  " + s));

        Transformation<Vehicule, String> extraireImmat = v -> v.getImmatriculation();
        System.out.println("Immatriculations :");
        service.mapperVehicules(flotte, extraireImmat)
                .forEach(s -> System.out.println("  " + s));

        Transformation<Vehicule, String> age = v ->
                v.getImmatriculation() + " -> " + (LocalDate.now().getYear() - v.getAnnee()) + " ans";
        System.out.println("Ages des vehicules :");
        service.mapperVehicules(flotte, age)
                .forEach(s -> System.out.println("  " + s));

        int taxe = 5000;
        Transformation<Entretien, String> coutTotal = ent ->
                ent.getDescription() + " -> cout total : " + (ent.getCout() + taxe);
        System.out.println("Couts totaux entretiens :");
        System.out.println("  " + coutTotal.transformer(e1));
        System.out.println("  " + coutTotal.transformer(e2));

        System.out.println("\n===== C — ACTIONS (Consumer) =====");

        Action<Vehicule> marquerRevision = v -> v.setEtat(EtatVehicule.EN_REVISION);
        System.out.println("Avant action - v1 etat : " + v1.getEtat());
        marquerRevision.executer(v1);
        System.out.println("Apres action - v1 etat : " + v1.getEtat());

        int ajoutKm = 500;
        Action<Vehicule> augmenterKm = v -> v.setKilometrage(v.getKilometrage() + ajoutKm);
        System.out.println("Avant action - v1 km : " + v1.getKilometrage());
        augmenterKm.executer(v1);
        System.out.println("Apres action - v1 km : " + v1.getKilometrage());

        Action<Location> terminerLoc = loc -> loc.terminer(LocalDate.now());
        Location loc2 = new Location(2L, v1, c2, LocalDate.of(2024, 11, 1), 12000);
        System.out.println("Avant - loc2 dateFin : " + loc2.getDateFin());
        terminerLoc.executer(loc2);
        System.out.println("Apres - loc2 dateFin : " + loc2.getDateFin());

        System.out.println("\n===== D — COMPARAISONS (Comparator) =====");

        Comparaison<Vehicule> parKm = (v, w) -> v.getKilometrage() - w.getKilometrage();
        service.trierVehicules(flotte, parKm);
        System.out.println("Flotte triee par km :");
        flotte.forEach(v -> System.out.println("  " + v.getImmatriculation()
                + " - " + v.getKilometrage()));

        Comparaison<Vehicule> parImmat = (v, w) -> v.getImmatriculation().compareTo(w.getImmatriculation());
        service.trierVehicules(flotte, parImmat);
        System.out.println("Flotte triee par immatriculation :");
        flotte.forEach(v -> System.out.println("  " + v.getImmatriculation()));

        // ============================================================
        // ETAPE 7 — equals/hashCode
        // ============================================================
        System.out.println("\n===== ETAPE 7 — Unicite et equals/hashCode =====");

        ParcAutoService service7 = new ParcAutoService();
        Vehicule v7a = new Vehicule(1L, "AA-111-BB", "Toyota",  15000, EtatVehicule.DISPONIBLE, 2020);
        Vehicule v7b = new Vehicule(2L, "CC-222-DD", "Renault", 80000, EtatVehicule.EN_PANNE,   2015);
        Vehicule v7c = new Vehicule(3L, "AA-111-BB", "Honda",   5000,  EtatVehicule.DISPONIBLE, 2022);

        service7.ajouterVehicule(v7a);
        service7.ajouterVehicule(v7b);

        try {
            service7.ajouterVehicule(v7c);
        } catch (IllegalArgumentException e) {
            System.out.println("Doublon refuse : " + e.getMessage());
        }

        System.out.println("v7a.equals(v7c) ? " + v7a.equals(v7c));
        System.out.println("hashCode egaux ? " + (v7a.hashCode() == v7c.hashCode()));
        System.out.println("Vehicules uniques : " + service7.vehiculesUniques().size());

        Set<Vehicule> set7 = new HashSet<>();
        set7.add(v7a);
        set7.add(v7b);
        set7.add(v7c);
        System.out.println("Set size (attendu 2) : " + set7.size());

        // ============================================================
        // ETAPE 8 — Map<Long, List<Entretien>>
        // ============================================================
        System.out.println("\n===== ETAPE 8 — Map<Long, List<Entretien>> =====");

        ParcAutoService service8 = new ParcAutoService();
        Vehicule v8a = new Vehicule(1L, "AA-111-BB", "Toyota",  15000, EtatVehicule.DISPONIBLE, 2020);
        Vehicule v8b = new Vehicule(2L, "CC-222-DD", "Renault", 80000, EtatVehicule.EN_PANNE,   2015);

        service8.ajouterVehicule(v8a);
        service8.ajouterVehicule(v8b);

        Entretien e8a = new Entretien(1L, v8a, LocalDate.of(2025, 1, 10), "Vidange moteur",     25000);
        Entretien e8b = new Entretien(2L, v8a, LocalDate.of(2025, 6, 20), "Changement freins",  40000);
        Entretien e8c = new Entretien(3L, v8b, LocalDate.of(2025, 3, 5),  "Remplacement pneus", 30000);

        service8.ajouterEntretien(e8a);
        service8.ajouterEntretien(e8b);
        service8.ajouterEntretien(e8c);

        System.out.println("Entretiens de v8a :");
        service8.getEntretiens(v8a.getId())
                .forEach(e -> System.out.println("  " + e.afficher()));

        System.out.println("Entretiens de v8b :");
        service8.getEntretiens(v8b.getId())
                .forEach(e -> System.out.println("  " + e.afficher()));

        System.out.println("Entretiens id=99 (attendu vide) : "
                + service8.getEntretiens(99L).size());

        // ============================================================
        // ETAPE 9 — Streams
        // ============================================================
        System.out.println("\n===== ETAPE 9 — Streams =====");

        ParcAutoService service9 = new ParcAutoService();
        Vehicule v9a = new Vehicule(1L, "CC-333-AA", "Toyota",  15000, EtatVehicule.DISPONIBLE,  2020);
        Vehicule v9b = new Vehicule(2L, "AA-111-BB", "Renault", 80000, EtatVehicule.EN_PANNE,    2015);
        Vehicule v9c = new Vehicule(3L, "EE-555-CC", "Peugeot", 45000, EtatVehicule.DISPONIBLE,  2018);
        Vehicule v9d = new Vehicule(4L, "BB-222-DD", "Honda",   95000, EtatVehicule.EN_REVISION, 2012);
        Vehicule v9e = new Vehicule(5L, "DD-444-EE", "Kia",     60000, EtatVehicule.DISPONIBLE,  2019);

        service9.ajouterVehicule(v9a);
        service9.ajouterVehicule(v9b);
        service9.ajouterVehicule(v9c);
        service9.ajouterVehicule(v9d);
        service9.ajouterVehicule(v9e);

        System.out.println("Vehicules disponibles :");
        service9.vehiculesDisponibles()
                .forEach(v -> System.out.println("  " + v.getImmatriculation()));

        System.out.println("Immatriculations triees :");
        service9.immatriculationsTriees()
                .forEach(immat -> System.out.println("  " + immat));

        System.out.println("Top 3 kilometrage :");
        service9.top3ParKilometrage()
                .forEach(v -> System.out.println("  " + v.getImmatriculation()
                        + " - " + v.getKilometrage() + " km"));

        // ============================================================
        // ETAPE 10 — Statistiques
        // ============================================================
        System.out.println("\n===== ETAPE 10 — Statistiques et groupingBy =====");

        ParcAutoService service10 = new ParcAutoService();
        Vehicule v10a = new Vehicule(1L, "AA-111-BB", "Toyota",  15000, EtatVehicule.DISPONIBLE,  2020);
        Vehicule v10b = new Vehicule(2L, "CC-222-DD", "Renault", 80000, EtatVehicule.EN_PANNE,    2015);
        Vehicule v10c = new Vehicule(3L, "EE-333-FF", "Peugeot", 45000, EtatVehicule.DISPONIBLE,  2018);
        Vehicule v10d = new Vehicule(4L, "GG-444-HH", "Honda",   95000, EtatVehicule.EN_REVISION, 2012);
        Vehicule v10e = new Vehicule(5L, "II-555-JJ", "Kia",     60000, EtatVehicule.EN_LOCATION, 2019);

        service10.ajouterVehicule(v10a);
        service10.ajouterVehicule(v10b);
        service10.ajouterVehicule(v10c);
        service10.ajouterVehicule(v10d);
        service10.ajouterVehicule(v10e);

        service10.ajouterEntretien(new Entretien(1L, v10a, LocalDate.of(2025, 1, 10), "Vidange",  20000));
        service10.ajouterEntretien(new Entretien(2L, v10a, LocalDate.of(2025, 6, 20), "Freins",   35000));
        service10.ajouterEntretien(new Entretien(3L, v10b, LocalDate.of(2025, 3, 5),  "Pneus",    15000));
        service10.ajouterEntretien(new Entretien(4L, v10c, LocalDate.of(2025, 8, 12), "Courroie", 50000));

        System.out.printf("Kilometrage moyen : %.2f km%n", service10.kilometrageMoyen());

        System.out.println("Vehicules par etat :");
        service10.vehiculesParEtat()
                .forEach((etat, count) ->
                        System.out.println("  " + etat + " : " + count + " vehicule(s)"));

        System.out.println("Couts entretiens par vehicule :");
        service10.coutEntretiensParVehicule()
                .forEach((immat, total) ->
                        System.out.println("  " + immat + " : " + total + " FCFA"));

        // ============================================================
        // ETAPE 11 — Polymorphisme
        // ============================================================
        System.out.println("\n===== ETAPE 11 — Polymorphisme et afficher() =====");

        Vehicule v11a = new Vehicule(1L, "AA-111-BB", "Toyota",  15000, EtatVehicule.DISPONIBLE, 2020);
        Vehicule v11b = new Vehicule(2L, "CC-222-DD", "Renault", 80000, EtatVehicule.EN_PANNE,   2015);
        Conducteur c11a = new Conducteur(1L, "Bah",        "B-12345");
        Conducteur c11b = new Conducteur(2L, "Zegbelemou", "A-67890");
        Entretien e11a = new Entretien(1L, v11a, LocalDate.of(2025, 1, 10), "Vidange moteur",    25000);
        Entretien e11b = new Entretien(2L, v11b, LocalDate.of(2025, 6, 20), "Changement freins", 40000);
        Location loc11 = new Location(1L, v11a, c11a, LocalDate.of(2025, 12, 1), 15000);
        loc11.terminer(LocalDate.of(2026, 2, 10));

        List<Entite> toutesLesEntites = new ArrayList<>();
        toutesLesEntites.add(v11a);
        toutesLesEntites.add(v11b);
        toutesLesEntites.add(c11a);
        toutesLesEntites.add(c11b);
        toutesLesEntites.add(e11a);
        toutesLesEntites.add(e11b);
        toutesLesEntites.add(loc11);

        System.out.println("Toutes les entites du parc :");
        toutesLesEntites.forEach(e -> System.out.println("  " + e.afficher()));

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
            new Entite(null) {
                public String afficher() { return "test"; }
            };
        } catch (IllegalArgumentException e) {
            System.out.println("  " + e.getMessage());
        }

        // ============================================================
        // ETAPE 12 — CRUD Generique
        // ============================================================
        System.out.println("\n===== ETAPE 12 — CRUD Generique =====");

        InMemoryCrud<Vehicule> vehiculeRepo12   = new InMemoryCrud<>();
        InMemoryCrud<Conducteur> conducteurRepo12 = new InMemoryCrud<>();

        Vehicule v12a = new Vehicule(1L, "AA-111-BB", "Toyota",  15000, EtatVehicule.DISPONIBLE, 2020);
        Vehicule v12b = new Vehicule(2L, "CC-222-DD", "Renault", 80000, EtatVehicule.EN_PANNE,   2015);
        Vehicule v12c = new Vehicule(3L, "EE-333-FF", "Peugeot", 45000, EtatVehicule.DISPONIBLE, 2018);
        Conducteur c12a = new Conducteur(1L, "Bah",        "B-12345");
        Conducteur c12b = new Conducteur(2L, "Zegbelemou", "A-67890");

        System.out.println("--- CREATE ---");
        vehiculeRepo12.create(v12a);
        vehiculeRepo12.create(v12b);
        vehiculeRepo12.create(v12c);
        conducteurRepo12.create(c12a);
        conducteurRepo12.create(c12b);
        System.out.println("Vehicules crees : " + vehiculeRepo12.findAll().size());
        System.out.println("Conducteurs crees : " + conducteurRepo12.findAll().size());

        try {
            vehiculeRepo12.create(v12a);
        } catch (IllegalArgumentException e) {
            System.out.println("Doublon refuse : " + e.getMessage());
        }

        System.out.println("\n--- READ ---");
        vehiculeRepo12.read(1L).ifPresent(v -> System.out.println("Trouve : " + v.afficher()));
        System.out.println("Id 99 present ? " + vehiculeRepo12.read(99L).isPresent());

        System.out.println("\n--- UPDATE ---");
        Vehicule v12aModifie = new Vehicule(1L, "AA-111-BB", "Toyota", 20000, EtatVehicule.EN_REVISION, 2020);
        vehiculeRepo12.update(v12aModifie);
        vehiculeRepo12.read(1L).ifPresent(v -> System.out.println("Apres update : " + v.afficher()));

        try {
            vehiculeRepo12.update(new Vehicule(99L, "ZZ-999-ZZ", "Kia", 5000, EtatVehicule.DISPONIBLE, 2022));
        } catch (IllegalArgumentException e) {
            System.out.println("Update refuse : " + e.getMessage());
        }

        System.out.println("\n--- DELETE ---");
        System.out.println("Avant delete : " + vehiculeRepo12.findAll().size() + " vehicules");
        vehiculeRepo12.delete(2L);
        System.out.println("Apres delete : " + vehiculeRepo12.findAll().size() + " vehicules");

        try {
            vehiculeRepo12.delete(99L);
        } catch (IllegalArgumentException e) {
            System.out.println("Delete refuse : " + e.getMessage());
        }

        System.out.println("\n--- FIND ALL ---");
        vehiculeRepo12.findAll()
                .forEach(v -> System.out.println("  " + v.afficher()));

        // ============================================================
        // ETAPE 13 — Optional
        // ============================================================
        System.out.println("\n===== ETAPE 13 — Optional =====");

        ParcAutoService service13 = new ParcAutoService();
        Vehicule v13a = new Vehicule(1L, "AA-111-BB", "Toyota",  15000, EtatVehicule.DISPONIBLE, 2020);
        Vehicule v13b = new Vehicule(2L, "CC-222-DD", "Renault", 80000, EtatVehicule.EN_PANNE,   2015);

        service13.getVehiculeRepo().create(v13a);
        service13.getVehiculeRepo().create(v13b);

        System.out.println("--- orElse ---");
        System.out.println(service13.resumeVehicule(1L));
        System.out.println(service13.resumeVehicule(99L));

        System.out.println("\n--- orElseThrow ---");
        try {
            Vehicule v13trouve = service13.getVehiculeOuException(1L);
            System.out.println("Trouve : " + v13trouve.getImmatriculation());
            service13.getVehiculeOuException(99L);
        } catch (IllegalArgumentException e) {
            System.out.println("Exception : " + e.getMessage());
        }

        System.out.println("\n--- ifPresent ---");
        service13.marquerEnRevisionSiPresent(1L);
        service13.marquerEnRevisionSiPresent(99L);
        System.out.println("Etat v13a apres : " + v13a.getEtat());

        System.out.println("\n--- map + orElse ---");
        System.out.println("Immat id=2  : " + service13.getImmatriculationOuInconnu(2L));
        System.out.println("Immat id=99 : " + service13.getImmatriculationOuInconnu(99L));

        // ============================================================
        // ETAPE 14 — Record et Rapport
        // ============================================================
        System.out.println("\n===== ETAPE 14 — Record et Rapport =====");

        ParcAutoService service14 = new ParcAutoService();
        Vehicule v14a = new Vehicule(1L, "AA-111-BB", "Toyota",  15000, EtatVehicule.DISPONIBLE,  2020);
        Vehicule v14b = new Vehicule(2L, "CC-222-DD", "Renault", 80000, EtatVehicule.EN_PANNE,    2015);
        Vehicule v14c = new Vehicule(3L, "EE-333-FF", "Peugeot", 45000, EtatVehicule.EN_LOCATION, 2018);
        Vehicule v14d = new Vehicule(4L, "GG-444-HH", "Honda",   95000, EtatVehicule.EN_REVISION, 2012);

        service14.ajouterVehicule(v14a);
        service14.ajouterVehicule(v14b);
        service14.ajouterVehicule(v14c);
        service14.ajouterVehicule(v14d);

        List<LigneRapport> rapport14 = service14.genererRapport();

        System.out.println("=== RAPPORT DU PARC AUTOMOBILE ===");
        System.out.printf("%-15s %-12s %-15s %10s%n", "IMMAT", "MARQUE", "ETAT", "KM");
        System.out.println("-".repeat(55));
        rapport14.forEach(l -> System.out.printf("%-15s %-12s %-15s %10d km%n",
                l.immat(), l.marque(), l.etat(), l.km()));
        System.out.println("-".repeat(55));
        System.out.println("Total vehicules : " + rapport14.size());

        System.out.println("\n--- Stats sur le rapport ---");
        double moyenne14 = rapport14.stream()
                .mapToInt(LigneRapport::km)
                .average()
                .orElse(0);
        System.out.printf("Km moyen : %.2f km%n", moyenne14);

        long nbDispo14 = rapport14.stream()
                .filter(l -> l.etat() == EtatVehicule.DISPONIBLE)
                .count();
        System.out.println("Disponibles : " + nbDispo14);

        rapport14.stream()
                .max((l1, l2) -> l1.km() - l2.km())
                .ifPresent(l -> System.out.println(
                        "Plus grand km : " + l.immat() + " - " + l.km() + " km"));

        System.out.println("\ntoString() automatique :");
        rapport14.forEach(l -> System.out.println("  " + l));

        // ============================================================
        // ETAPE 15 — Scenario complet
        // ============================================================
        System.out.println("\n========================================");
        System.out.println("  SCENARIO COMPLET — PARC AUTOMOBILE  ");
        System.out.println("========================================");

        ParcAutoService parc = new ParcAutoService();

        System.out.println("\n--- 1. Creation des donnees ---");

        Vehicule v15a = new Vehicule(1L, "AA-111-BB", "Toyota",   15000, EtatVehicule.DISPONIBLE, 2020);
        Vehicule v15b = new Vehicule(2L, "CC-222-DD", "Renault",  80000, EtatVehicule.DISPONIBLE, 2014);
        Vehicule v15c = new Vehicule(3L, "EE-333-FF", "Peugeot",  45000, EtatVehicule.DISPONIBLE, 2018);
        Vehicule v15d = new Vehicule(4L, "GG-444-HH", "Honda",    95000, EtatVehicule.DISPONIBLE, 2011);
        Vehicule v15e = new Vehicule(5L, "II-555-JJ", "Kia",      30000, EtatVehicule.DISPONIBLE, 2022);
        Conducteur c15a = new Conducteur(1L, "Bah",        "B-12345");
        Conducteur c15b = new Conducteur(2L, "Zegbelemou", "A-67890");

        parc.ajouterVehicule(v15a);
        parc.ajouterVehicule(v15b);
        parc.ajouterVehicule(v15c);
        parc.ajouterVehicule(v15d);
        parc.ajouterVehicule(v15e);

        parc.getVehiculeRepo().create(v15a);
        parc.getVehiculeRepo().create(v15b);
        parc.getVehiculeRepo().create(v15c);
        parc.getVehiculeRepo().create(v15d);
        parc.getVehiculeRepo().create(v15e);

        parc.getConducteurRepo().create(c15a);
        parc.getConducteurRepo().create(c15b);

        System.out.println("Vehicules crees : " + parc.getVehicules().size());

        System.out.println("\n--- 2. Entretiens ---");

        parc.ajouterEntretien(new Entretien(1L, v15b, LocalDate.of(2025, 1, 10), "Vidange moteur",     20000));
        parc.ajouterEntretien(new Entretien(2L, v15b, LocalDate.of(2025, 6, 20), "Changement freins",  35000));
        parc.ajouterEntretien(new Entretien(3L, v15d, LocalDate.of(2025, 3, 5),  "Remplacement pneus", 15000));
        parc.ajouterEntretien(new Entretien(4L, v15d, LocalDate.of(2025, 9, 12), "Courroie",           50000));

        System.out.println("Entretiens v15b : " + parc.getEntretiens(v15b.getId()).size());
        System.out.println("Entretiens v15d : " + parc.getEntretiens(v15d.getId()).size());

        System.out.println("\n--- 3. Locations ---");

        Location loc15a = parc.demarrerLocation(1L, 1L, LocalDate.of(2026, 1, 1), 15000, 1L);
        Location loc15b = parc.demarrerLocation(2L, 2L, LocalDate.of(2026, 1, 5), 12000, 2L);

        System.out.println("Etat v15a apres demarrage : " + v15a.getEtat());
        System.out.println("Etat v15b apres demarrage : " + v15b.getEtat());

        parc.terminerLocation(1L, LocalDate.of(2026, 2, 1));
        System.out.println("Etat v15a apres fin : " + v15a.getEtat());
        System.out.println("Duree loc15b en cours : " + loc15b.dureeJours() + " jours");

        try {
            parc.demarrerLocation(2L, 1L, LocalDate.now(), 10000, 3L);
        } catch (IllegalArgumentException e) {
            System.out.println("Refuse : " + e.getMessage());
        }

        System.out.println("\n--- 4. Operations CRUD ---");

        System.out.println("resumeVehicule(1L)  : " + parc.resumeVehicule(1L));
        System.out.println("resumeVehicule(99L) : " + parc.resumeVehicule(99L));

        try {
            parc.getVehiculeOuException(99L);
        } catch (IllegalArgumentException e) {
            System.out.println("orElseThrow : " + e.getMessage());
        }

        parc.marquerEnRevisionSiPresent(3L);
        System.out.println("Etat v15c : " + v15c.getEtat());

        System.out.println("\n--- 5. Statistiques ---");

        System.out.printf("Km moyen : %.2f km%n", parc.kilometrageMoyen());

        System.out.println("Vehicules par etat :");
        parc.vehiculesParEtat()
                .forEach((etat, count) -> System.out.println("  " + etat + " : " + count));

        System.out.println("Couts entretiens par vehicule :");
        parc.coutEntretiensParVehicule()
                .forEach((immat, total) -> System.out.println("  " + immat + " : " + total + " FCFA"));

        System.out.println("\n--- 6. Vehicules a reviser ---");

        int seuil15Km    = 40000;
        int seuil15Annee = 2015;

        Test<Vehicule> regle15 = v ->
                v.getKilometrage() > seuil15Km
                        || v.getAnnee() < seuil15Annee
                        || v.getEtat() == EtatVehicule.EN_PANNE;

        List<Vehicule> aReviser15 = parc.vehiculesAReviser(regle15);
        System.out.println("Vehicules a reviser (" + aReviser15.size() + ") :");
        aReviser15.forEach(v -> System.out.println(
                "  " + v.getImmatriculation()
                        + " | " + v.getAnnee()
                        + " | " + v.getKilometrage() + " km"
                        + " | " + v.getEtat()));

        System.out.println("\n--- 7. Rapport final ---");

        List<LigneRapport> rapport15 = parc.genererRapport();

        System.out.printf("%-15s %-12s %-15s %10s%n", "IMMAT", "MARQUE", "ETAT", "KM");
        System.out.println("-".repeat(55));
        rapport15.forEach(l -> System.out.printf("%-15s %-12s %-15s %10d km%n",
                l.immat(), l.marque(), l.etat(), l.km()));
        System.out.println("-".repeat(55));

        rapport15.stream()
                .max((l1, l2) -> l1.km() - l2.km())
                .ifPresent(l -> System.out.println(
                        "Plus grand km : " + l.immat() + " - " + l.km() + " km"));

        long dispo15 = rapport15.stream()
                .filter(l -> l.etat() == EtatVehicule.DISPONIBLE)
                .count();
        System.out.println("Disponibles : " + dispo15);
        System.out.println("Total       : " + rapport15.size() + " vehicules");

        System.out.println("\n========================================");
        System.out.println("         FIN DU SCENARIO               ");
        System.out.println("========================================");
    }
}