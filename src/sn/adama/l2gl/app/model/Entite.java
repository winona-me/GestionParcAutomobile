package sn.adama.l2gl.app.model;

public abstract class Entite implements Identifiable {
    private final Long id;
    protected Entite(Long id){
        if (id == null) {
            throw new IllegalArgumentException("L'id ne peut pas être null");
        }
        this.id = id;
    };

    @Override
    public final Long getId() {
        return id;
    }

    // Chaque sous-classe DOIT implémenter cette méthode
    public abstract String afficher();
}
