package calculator;

public class NoSeparatorStrategy implements CalculateStrategy {
    @Override
    public int calculate(String input) {
        return 0;
    }

    @Override
    public boolean canCalculate(String input) {
        return isEmpty(input);
    }

    private boolean isEmpty(String input) {
        return input == null || input.isBlank();
    }
}
