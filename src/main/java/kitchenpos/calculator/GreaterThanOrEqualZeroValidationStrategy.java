package kitchenpos.calculator;

public class GreaterThanOrEqualZeroValidationStrategy implements ValidationStrategy {
    @Override
    public boolean isValid(int num) {
        return num >= 0;
    }
}
