package calculator.number;

public class PositiveNumber implements Number {

    private static final int VALUE_ZERO = 0;
    private static final String NEGATIVE_EXCEPTION_MESSAGE = "음수는 사용할 수 없습니다";
    private static final String NOT_NUMBER_EXCEPTION_MESSAGE = "숫자가 아닌 문자는 사용할 수 없습니다";

    public static final Number ZERO = new PositiveNumber(VALUE_ZERO);

    private final int number;

    public PositiveNumber(final String value) {
        final int number = parseInt(value);
        validateNegative(number);
        this.number = number;
    }

    public PositiveNumber(final int value) {
        validateNegative(value);
        this.number = value;
    }

    @Override
    public Number sum(final Number value) {
        return new PositiveNumber(number + value.getNumber());
    }

    @Override
    public int getNumber() {
        return this.number;
    }

    private int parseInt(final String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException(NOT_NUMBER_EXCEPTION_MESSAGE);
        }
    }

    private void validateNegative(final int number) {
        if (number < VALUE_ZERO) {
            throw new RuntimeException(NEGATIVE_EXCEPTION_MESSAGE);
        }
    }
}
