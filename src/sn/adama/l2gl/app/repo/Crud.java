package sn.adama.l2gl.app.repo;

import sn.adama.l2gl.app.model.Identifiable;

import java.util.List;
import java.util.Optional;

public interface Crud<T extends Identifiable> {

    void create(T entity);

    Optional<T> read(Long id);

    void update(T entity);

    void delete(Long id);

    List<T> findAll();

    Optional<T> readOpt(Long id);
}