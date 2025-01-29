package calculator;

public interface CalculateStrategy {
    int calculate(String input);
    boolean canCalculate(String input);
}
