package sn.adama.l2gl.app.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class Location extends Entite {

    private final Vehicule vehicule;
    private final Conducteur conducteur;
    private final LocalDate dateDebut;
    private Optional<LocalDate> dateFin;
    private final int prixJour;

    // Constructeur
    public Location(Long id, Vehicule vehicule, Conducteur conducteur,
                    LocalDate dateDebut, int prixJour) {

        super(id);

        // Validations
        if (vehicule == null)
            throw new IllegalArgumentException("Véhicule non null");

        if (conducteur == null)
            throw new IllegalArgumentException("Conducteur non null");

        if (dateDebut == null)
            throw new IllegalArgumentException("Date début non null");

        if (prixJour < 0)
            throw new IllegalArgumentException("Prix/jour >= 0");

        this.vehicule    = vehicule;
        this.conducteur  = conducteur;
        this.dateDebut   = dateDebut;
        this.dateFin     = Optional.empty(); // pas encore terminée
        this.prixJour    = prixJour;
    }

    // Getters
    public Vehicule getVehicule()               { return vehicule; }
    public Conducteur getConducteur()           { return conducteur; }
    public LocalDate getDateDebut()             { return dateDebut; }
    public Optional<LocalDate> getDateFin()     { return dateFin; }
    public int getPrixJour()                    { return prixJour; }

    // Terminer la location
    public void terminer(LocalDate fin) {
        if (fin == null)
            throw new IllegalArgumentException("Date fin non null");

        if (fin.isBefore(dateDebut))
            throw new IllegalArgumentException("Date fin doit être >= date début");

        this.dateFin = Optional.of(fin);
    }

    // Calculer la durée en jours
    public long dureeJours() {
        // Si dateFin présente → on calcule jusqu'à dateFin
        // Sinon → on calcule jusqu'à aujourd'hui
        LocalDate fin = dateFin.orElse(LocalDate.now());
        return ChronoUnit.DAYS.between(dateDebut, fin);
    }

    // Implémentation obligatoire de afficher()
    @Override
    public String afficher() {
        return "Location{" +
                "id=" + getId() +
                ", vehicule=" + vehicule.getImmatriculation() +
                ", conducteur=" + conducteur.getNom() +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin.map(LocalDate::toString).orElse("En cours") +
                ", prixJour=" + prixJour +
                ", duree=" + dureeJours() + " jours" +
                '}';
    }


}