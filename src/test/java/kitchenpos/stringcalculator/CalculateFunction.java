package kitchenpos.stringcalculator;

public interface CalculateFunction {
    int calculate(String text) throws RuntimeException;
    static int getValidatedNumber(int number) {
        if (number < 0) {
            throw new RuntimeException("음수 값");
        }

        return number;
    }

}
