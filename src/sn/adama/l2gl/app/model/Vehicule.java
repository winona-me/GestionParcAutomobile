package sn.adama.l2gl.app.model;

public class Vehicule extends Entite {

    private final String immatriculation;
    private final String marque;
    private int kilometrage;
    private EtatVehicule etat;
    private final int annee;

    // Constructeur
    public Vehicule(Long id, String immatriculation, String marque,
                    int kilometrage, EtatVehicule etat, int annee) {

        super(id);

        // Validations
        if (immatriculation == null || immatriculation.isBlank())
            throw new IllegalArgumentException("Immatriculation non vide");

        if (marque == null || marque.isBlank())
            throw new IllegalArgumentException("Marque non vide");

        if (kilometrage < 0)
            throw new IllegalArgumentException("Kilométrage >= 0");

        if (annee < 1990)
            throw new IllegalArgumentException("Année >= 1990");

        this.immatriculation = immatriculation;
        this.marque = marque;
        this.kilometrage = kilometrage;
        this.etat = etat;
        this.annee = annee;
    }

    // Getters
    public String getImmatriculation() { return immatriculation; }
    public String getMarque()          { return marque; }
    public int getKilometrage()        { return kilometrage; }
    public EtatVehicule getEtat()      { return etat; }
    public int getAnnee()              { return annee; }

    // Setters (seulement pour les attributs modifiables)
    public void setKilometrage(int km) {
        if (km < 0) throw new IllegalArgumentException("Kilométrage >= 0");
        this.kilometrage = km;
    }

    public void setEtat(EtatVehicule etat) {
        if (etat == null) throw new IllegalArgumentException("Etat non null");
        this.etat = etat;
    }

    // Implémentation obligatoire de afficher()
    @Override
    public String afficher() {
        return "Vehicule{" +
                "id=" + getId() +
                ", immat='" + immatriculation + '\'' +
                ", marque='" + marque + '\'' +
                ", km=" + kilometrage +
                ", etat=" + etat +
                ", annee=" + annee +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        // 1. Même référence mémoire → forcément égaux
        if (this == o) return true;

        // 2. Null ou classe différente → pas égaux
        if (o == null || getClass() != o.getClass()) return false;

        // 3. On compare uniquement l'immatriculation (identité métier)
        Vehicule vehicule = (Vehicule) o;
        return immatriculation.equals(vehicule.immatriculation);
    }

    @Override
    public int hashCode() {
        // Le hashCode est basé uniquement sur l'immatriculation
        return immatriculation.hashCode();
    }
}