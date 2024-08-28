package utilities;

public abstract class ExceptableFunction<D, R, E extends Exception> {
    public abstract R apply(D d) throws E;
}
