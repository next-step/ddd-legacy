package calculator;

@FunctionalInterface
public interface CalculateStrategy {
    int calculate(final String[] text);
}
