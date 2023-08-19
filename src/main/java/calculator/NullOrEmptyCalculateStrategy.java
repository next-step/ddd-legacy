package calculator;

public class NullOrEmptyCalculateStrategy extends AbstractCalculateStrategy {

    private static final int NULL_OR_EMPTY_RETURN_VALUE = 0;

    @Override
    public boolean isTarget(final String text) {
        return (text == null) || (text.isBlank());
    }

    @Override
    public int calculate(final String text) {
        return NULL_OR_EMPTY_RETURN_VALUE;
    }

}
