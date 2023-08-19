package calculator;

public class NumberCalculatorPolicy implements CalculatorPolicy {

    private static final String PATTERN = "^(0|[-]?[1-9]\\d*)$";

    @Override
    public boolean isSupport(String text) {
        return text.matches(PATTERN);
    }

    @Override
    public int calculate(String text) {
        return toPositive(text);
    }
}
