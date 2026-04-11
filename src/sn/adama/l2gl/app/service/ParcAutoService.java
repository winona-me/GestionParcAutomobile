package sn.adama.l2gl.app.service;

import sn.adama.l2gl.app.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import sn.adama.l2gl.app.repo.InMemoryCrud;
import java.util.Optional;

public class ParcAutoService {

    // Les deux structures de stockage
    private final List<Vehicule> vehicules = new ArrayList<>();
    private final Map<String, Vehicule> indexParImmat = new HashMap<>();
    private final Map<Long, List<Entretien>> entretiensParVehiculeId = new HashMap<>();


    // Les repos génériques
    private final InMemoryCrud<Vehicule> vehiculeRepo = new InMemoryCrud<>();
    private final InMemoryCrud<Conducteur> conducteurRepo = new InMemoryCrud<>();
    private final InMemoryCrud<Entretien> entretienRepo = new InMemoryCrud<>();
    private final InMemoryCrud<Location> locationRepo = new InMemoryCrud<>();


    // ============================================================
    // GESTION DES VEHICULES
    // ============================================================

    // Ajouter un véhicule
    public void ajouterVehicule(Vehicule v) {
        if (v == null)
            throw new IllegalArgumentException("Véhicule non null");

        if (indexParImmat.containsKey(v.getImmatriculation()))
            throw new IllegalArgumentException("Immatriculation déjà existante : " + v.getImmatriculation());

        // On maintient la cohérence des deux structures
        vehicules.add(v);
        indexParImmat.put(v.getImmatriculation(), v);
    }

    // Supprimer un véhicule par immatriculation
    public void supprimerVehicule(String immat) {
        if (immat == null || immat.isBlank())
            throw new IllegalArgumentException("Immatriculation non vide");

        Vehicule v = indexParImmat.get(immat);

        if (v == null)
            throw new IllegalArgumentException("Véhicule introuvable : " + immat);

        // On maintient la cohérence des deux structures
        vehicules.remove(v);
        indexParImmat.remove(immat);
    }

    // Rechercher un véhicule par immatriculation
    public Optional<Vehicule> rechercher(String immat) {
        if (immat == null || immat.isBlank())
            return Optional.empty();

        return Optional.ofNullable(indexParImmat.get(immat));
    }

    public InMemoryCrud<Vehicule> getVehiculeRepo() {
        return vehiculeRepo;
    }

    public InMemoryCrud<Conducteur> getConducteurRepo() {
        return conducteurRepo;
    }

    // Getter pour la liste complète
    public List<Vehicule> getVehicules() {
        return vehicules;
    }

    // ============================================================
    // MÉTHODES FONCTIONNELLES (de l'étape précédente)
    // ============================================================

    public List<Vehicule> filtrerVehicules(List<Vehicule> src, Test<Vehicule> regle) {
        List<Vehicule> resultat = new ArrayList<>();
        for (Vehicule v : src) {
            if (regle.tester(v)) resultat.add(v);
        }
        return resultat;
    }

    public List<String> mapperVehicules(List<Vehicule> src, Transformation<Vehicule, String> f) {
        List<String> resultat = new ArrayList<>();
        for (Vehicule v : src) resultat.add(f.transformer(v));
        return resultat;
    }

    public void appliquerSurVehicules(List<Vehicule> src, Action<Vehicule> action) {
        for (Vehicule v : src) action.executer(v);
    }

    public void trierVehicules(List<Vehicule> src, Comparaison<Vehicule> cmp) {
        int n = src.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (cmp.comparer(src.get(j), src.get(j + 1)) > 0) {
                    Vehicule temp = src.get(j);
                    src.set(j, src.get(j + 1));
                    src.set(j + 1, temp);
                }
            }
        }
    }

    public Set<Vehicule> vehiculesUniques() {
        return new HashSet<>(vehicules);
    }

    public void ajouterEntretien(Entretien e) {
        if (e == null)
            throw new IllegalArgumentException("Entretien non null");

        Long vehiculeId = e.getVehicule().getId();

        // Si ce véhicule n'a pas encore de liste , on en crée une
        if (!entretiensParVehiculeId.containsKey(vehiculeId)) {
            entretiensParVehiculeId.put(vehiculeId, new ArrayList<>());
        }

        entretiensParVehiculeId.get(vehiculeId).add(e);
    }

    // Récupérer les entretiens d'un véhicule
    public List<Entretien> getEntretiens(Long vehiculeId) {
        if (vehiculeId == null)
            throw new IllegalArgumentException("vehiculeId non null");

        // Si pas de clé , on retourne une liste vide (jamais null !)
        return entretiensParVehiculeId.getOrDefault(vehiculeId, new ArrayList<>());
    }

    // 1. Liste des véhicules disponibles (filter)
    public List<Vehicule> vehiculesDisponibles() {
        return vehicules.stream()
                .filter(v -> v.getEtat() == EtatVehicule.DISPONIBLE)
                .collect(Collectors.toList());
    }

    // 2. Liste des immatriculations triées alphabétiquement (map + sorted)
    public List<String> immatriculationsTriees() {
        return vehicules.stream()
                .map(v -> v.getImmatriculation())
                .sorted()
                .collect(Collectors.toList());
    }

    // 3. Les 3 véhicules avec le plus grand kilométrage (sorted + limit)
    public List<Vehicule> top3ParKilometrage() {
        return vehicules.stream()
                .sorted((v1, v2) -> v2.getKilometrage() - v1.getKilometrage()) // décroissant !
                .limit(3)
                .collect(Collectors.toList());
    }

    // ============================================================
// STATISTIQUES
// ============================================================

    // 1. Kilométrage moyen de tous les véhicules
    public double kilometrageMoyen() {
        return vehicules.stream()
                .collect(Collectors.averagingInt(v -> v.getKilometrage()));
    }

    // 2. Nombre de véhicules par état
    public Map<EtatVehicule, Long> vehiculesParEtat() {
        return vehicules.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getEtat(),        // critère de regroupement
                        Collectors.counting()    // compte les éléments de chaque groupe
                ));
    }

    // 3. Total des coûts d'entretien par véhicule
    public Map<String, Integer> coutEntretiensParVehicule() {
        // On récupère tous les entretiens de tous les véhicules
        List<Entretien> tousLesEntretiens = entretiensParVehiculeId.values()
                .stream()
                .flatMap(liste -> liste.stream()) // aplatit List<List<Entretien>> en List<Entretien>
                .collect(Collectors.toList());

        return tousLesEntretiens.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getVehicule().getImmatriculation(), // groupe par immat
                        Collectors.summingInt(e -> e.getCout())    // additionne les coûts
                ));
    }

    // ============================================================
    // USAGE 1 — orElse
    // Retourne le véhicule trouvé OU un message par défaut
    // ============================================================
    public String resumeVehicule(Long id) {
        return vehiculeRepo.readOpt(id)
                .map(v -> v.afficher())              // si présent → afficher()
                .orElse("Aucun véhicule trouvé avec l'id : " + id);
    }

    // ============================================================
    // USAGE 2 — orElseThrow
    // Retourne le véhicule OU lance une exception métier
    // ============================================================
    public Vehicule getVehiculeOuException(Long id) {
        return vehiculeRepo.readOpt(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Vehicule introuvable avec l'id : " + id)
                );
    }

    // ============================================================
    // USAGE 3 — ifPresent
    // Marque le véhicule en révision seulement s'il existe
    // ============================================================
    public void marquerEnRevisionSiPresent(Long id) {
        vehiculeRepo.readOpt(id)
                .ifPresent(v -> {
                    v.setEtat(EtatVehicule.EN_REVISION);
                    System.out.println("Vehicule " + v.getImmatriculation() + " marque EN_REVISION");
                });
    }

    // ============================================================
    // USAGE BONUS — map + orElse
    // Retourne l'immatriculation ou "Inconnu" si absent
    // ============================================================
    public String getImmatriculationOuInconnu(Long id) {
        return vehiculeRepo.readOpt(id)
                .map(v -> v.getImmatriculation())
                .orElse("Inconnu");
    }

    public List<LigneRapport> genererRapport() {
        return vehicules.stream()
                .map(v -> new LigneRapport(
                        v.getImmatriculation(),
                        v.getMarque(),
                        v.getEtat(),
                        v.getKilometrage()
                ))
                .collect(Collectors.toList());
    }

    // ============================================================
    // GESTION DES LOCATIONS — ÉTAPE 15
    // ============================================================

    private final List<Location> locations = new ArrayList<>();

    // Démarrer une location
    public Location demarrerLocation(Long vehiculeId, Long conducteurId,
                                     LocalDate dateDebut, int prixJour,
                                     Long newLocationId) {

        // 1. Récupérer le véhicule — orElseThrow si absent
        Vehicule vehicule = vehiculeRepo.readOpt(vehiculeId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Vehicule introuvable : " + vehiculeId));

        // 2. Récupérer le conducteur — orElseThrow si absent
        Conducteur conducteur = conducteurRepo.readOpt(conducteurId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Conducteur introuvable : " + conducteurId));

        // 3. Vérifier que le véhicule est disponible
        if (vehicule.getEtat() != EtatVehicule.DISPONIBLE)
            throw new IllegalArgumentException(
                    "Vehicule " + vehicule.getImmatriculation() +
                            " non disponible, etat actuel : " + vehicule.getEtat());

        // 4. Changer l'état du véhicule
        vehicule.setEtat(EtatVehicule.EN_LOCATION);

        // 5. Créer et stocker la location
        Location location = new Location(newLocationId, vehicule,
                conducteur, dateDebut, prixJour);
        locations.add(location);
        locationRepo.create(location);

        System.out.println("Location demarree : " + vehicule.getImmatriculation()
                + " → " + conducteur.getNom());

        return location;
    }

    // Terminer une location
    public void terminerLocation(Long locationId, LocalDate dateFin) {

        // 1. Récupérer la location — orElseThrow si absente
        Location location = locationRepo.readOpt(locationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Location introuvable : " + locationId));

        // 2. Terminer la location
        location.terminer(dateFin);

        // 3. Remettre le véhicule disponible
        location.getVehicule().setEtat(EtatVehicule.DISPONIBLE);

        System.out.println("Location terminee : "
                + location.getVehicule().getImmatriculation()
                + " duree=" + location.dureeJours() + " jours");
    }

    // Liste des véhicules à réviser via lambda
    public List<Vehicule> vehiculesAReviser(Test<Vehicule> regle) {
        return vehicules.stream()
                .filter(v -> regle.tester(v))
                .collect(Collectors.toList());
    }

    // Getter locations
    public List<Location> getLocations() { return locations; }

}