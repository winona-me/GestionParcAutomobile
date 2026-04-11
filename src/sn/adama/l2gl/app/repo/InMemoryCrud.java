package sn.adama.l2gl.app.repo;

import sn.adama.l2gl.app.model.Identifiable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryCrud<T extends Identifiable> implements Crud<T> {

    private final Map<Long, T> storage = new HashMap<>();

    // CREATE — ajouter un élément
    @Override
    public void create(T entity) {
        if (entity == null)
            throw new IllegalArgumentException("L'entité ne peut pas être null");

        if (entity.getId() == null)
            throw new IllegalArgumentException("L'id de l'entité ne peut pas être null");

        if (storage.containsKey(entity.getId()))
            throw new IllegalArgumentException("Un élément avec l'id " + entity.getId() + " existe déjà");

        storage.put(entity.getId(), entity);
    }

    // READ — lire un élément par id
    @Override
    public Optional<T> read(Long id) {
        if (id == null)
            throw new IllegalArgumentException("L'id ne peut pas être null");

        return Optional.ofNullable(storage.get(id));
    }

    // UPDATE — mettre à jour un élément
    @Override
    public void update(T entity) {
        if (entity == null)
            throw new IllegalArgumentException("L'entité ne peut pas être null");

        if (entity.getId() == null)
            throw new IllegalArgumentException("L'id de l'entité ne peut pas être null");

        if (!storage.containsKey(entity.getId()))
            throw new IllegalArgumentException("Aucun élément trouvé avec l'id " + entity.getId());

        storage.put(entity.getId(), entity);
    }

    // DELETE — supprimer un élément par id
    @Override
    public void delete(Long id) {
        if (id == null)
            throw new IllegalArgumentException("L'id ne peut pas être null");

        if (!storage.containsKey(id))
            throw new IllegalArgumentException("Aucun élément trouvé avec l'id " + id);

        storage.remove(id);
    }

    // FIND ALL — récupérer tous les éléments
    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }
}