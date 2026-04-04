package sn.adama.l2gl.app.model;

@FunctionalInterface
public interface Test<T> {
    boolean tester(T element);
}