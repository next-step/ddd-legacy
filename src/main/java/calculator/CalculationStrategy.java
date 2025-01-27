package calculator;

public interface CalculationStrategy {
    int calculate(String input);
    boolean canCalculate(String input);
}
