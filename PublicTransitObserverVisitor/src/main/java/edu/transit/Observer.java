package edu.transit;

@FunctionalInterface public interface Observer<E> {
    void onEvent(E event);
}
