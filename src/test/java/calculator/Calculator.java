package calculator;

public interface Calculator<T, E> {
    T calculate(E input);
}
