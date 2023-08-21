package calculator;

public class NumberPatternCalculateStrategy extends AbstractCalculateStrategy {

    private static final String NUMBER_PATTERN = "-?\\d+(\\.\\d+)?";

    @Override
    public boolean isTarget(final String text) {
        return text.matches(NUMBER_PATTERN);
    }

    @Override
    public int calculate(final String text) {
        return parseNonNegativeNumber(text);
    }

}
