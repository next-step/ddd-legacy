package kitchenpos.stringcalculator;

@FunctionalInterface
public interface CalculateFunction {
    int calculate(String text) throws RuntimeException;
}
