package be.flmr.secmon.core.util;

public class Tuple<T, U> {
    private final T val1;
    private final U val2;

    public Tuple(T val1, U val2) {
        this.val1 = val1;
        this.val2 = val2;
    }

    public Tuple(Tuple<T, U> tuple) {
        this.val1 = tuple.val1;
        this.val2 = tuple.val2;
    }

    public T getVal1() {
        return val1;
    }

    public U getVal2() {
        return val2;
    }
}
