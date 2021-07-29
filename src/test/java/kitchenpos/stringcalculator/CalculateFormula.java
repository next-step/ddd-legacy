package kitchenpos.stringcalculator;

@FunctionalInterface
public interface CalculateFormula {
    int operate(String text) throws RuntimeException;
}
