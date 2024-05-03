package calculator.splitter;

@FunctionalInterface
public interface Splitter<V> {

    V[] split(V target);
}
