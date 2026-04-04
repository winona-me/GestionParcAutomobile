package sn.adama.l2gl.app.model;

import java.time.LocalDate;

public class Entretien extends Entite {

    private final Vehicule vehicule;
    private final LocalDate date;
    private final String description;
    private final int cout;

    // Constructeur
    public Entretien(Long id, Vehicule vehicule, LocalDate date,
                     String description, int cout) {

        super(id);

        // Validations
        if (vehicule == null)
            throw new IllegalArgumentException("Véhicule non null");

        if (date == null)
            throw new IllegalArgumentException("Date non null");

        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Description non vide");

        if (cout < 0)
            throw new IllegalArgumentException("Coût >= 0");

        this.vehicule    = vehicule;
        this.date        = date;
        this.description = description;
        this.cout        = cout;
    }

    // Getters
    public Vehicule getVehicule()      { return vehicule; }
    public LocalDate getDate()         { return date; }
    public String getDescription()     { return description; }
    public int getCout()               { return cout; }

    // Implémentation obligatoire de afficher()
    @Override
    public String afficher() {
        return "Entretien{" +
                "id=" + getId() +
                ", vehicule=" + vehicule.getImmatriculation() +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", cout=" + cout +
                '}';
    }
}