package sn.adama.l2gl.app.model;

public record LigneRapport(
        String immat,
        String marque,
        EtatVehicule etat,
        int km
) {
    public LigneRapport {
        if (immat == null || immat.isBlank())
            throw new IllegalArgumentException("Immat non vide");
        if (marque == null || marque.isBlank())
            throw new IllegalArgumentException("Marque non vide");
        if (km < 0)
            throw new IllegalArgumentException("km >= 0");
    }
}