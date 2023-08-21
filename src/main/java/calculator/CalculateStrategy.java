package calculator;

public interface CalculateStrategy {

    boolean isTarget(String text);
    int calculate(String text);

}
