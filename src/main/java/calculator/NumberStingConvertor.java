package calculator;

public class NumberStingConvertor implements StingConvertor {

    private static final String PATTERN = "^(0|[-]?[1-9]\\d*)$";

    @Override
    public boolean isSupport(String text) {
        return text.matches(PATTERN);
    }

    @Override
    public PositiveNumbers calculate(String text) {
        return new PositiveNumbers(new PositiveNumber(text));
    }
}
