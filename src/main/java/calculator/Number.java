package calculator;

/**
 * @author Geonguk Han
 * @since 2020-02-12
 */
public class Number {

    private final int value;

    public Number(String value) {
        this(Integer.parseInt(value));
    }

    public Number(int value) {
        this.value = value;
        validateNumber();
    }

    public int getValue() {
        return value;
    }

    private void validateNumber() {
        if (isNegative()) {
            throw new RuntimeException();
        }
    }

    private boolean isNegative() {
        return value < 0;
    }
}
