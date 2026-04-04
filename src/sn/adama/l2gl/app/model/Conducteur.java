package sn.adama.l2gl.app.model;

public class Conducteur extends Entite {

    private final String nom;
    private final String permis;

    // Constructeur
    public Conducteur(Long id, String nom, String permis) {

        super(id); // appel du constructeur de Entite

        // Validations
        if (nom == null || nom.isBlank())
            throw new IllegalArgumentException("Nom non vide");

        if (permis == null || permis.isBlank())
            throw new IllegalArgumentException("Permis non vide");

        this.nom = nom;
        this.permis = permis;
    }

    // Getters
    public String getNom()    { return nom; }
    public String getPermis() { return permis; }

    // Implémentation obligatoire de afficher()
    @Override
    public String afficher() {
        return "Conducteur{" +
                "id=" + getId() +
                ", nom='" + nom + '\'' +
                ", permis='" + permis + '\'' +
                '}';
    }
}