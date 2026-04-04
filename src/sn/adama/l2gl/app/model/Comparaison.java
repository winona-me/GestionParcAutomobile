package sn.adama.l2gl.app.model;

@FunctionalInterface
public interface Comparaison<T> {
    int comparer(T element1, T element2);
}