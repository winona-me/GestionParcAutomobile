package sn.adama.l2gl.app.model;

@FunctionalInterface
public interface Transformation<T, R> {
    R transformer(T element);
}