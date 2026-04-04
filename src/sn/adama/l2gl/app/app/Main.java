package sn.adama.l2gl.app.app;

import sn.adama.l2gl.app.model.*;
import sn.adama.l2gl.app.service.ParcAutoService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // ============================================================
        // 1. CONSTRUCTION DE LA FLOTTE
        // ============================================================

        Vehicule v1 = new Vehicule(1L, "AA-111-BB", "Toyota",  15000, EtatVehicule.DISPONIBLE,  2020);
        Vehicule v2 = new Vehicule(2L, "CC-222-DD", "Renault", 80000, EtatVehicule.EN_PANNE,    2015);
        Vehicule v3 = new Vehicule(3L, "EE-333-FF", "Peugeot", 45000, EtatVehicule.EN_LOCATION, 2018);
        Vehicule v4 = new Vehicule(4L, "GG-444-HH", "Honda",   95000, EtatVehicule.DISPONIBLE,  2012);

        Conducteur c1 = new Conducteur(1L, "Bah" ,"B-12345");
        Conducteur c2 = new Conducteur(2L, "Zegbelemou","A-67890");

        Entretien e1 = new Entretien(1L, v2, LocalDate.of(2025, 3, 10), "Vidange moteur",    25000);
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
    }
}