package calculator;

import java.util.Objects;

/**
 * @author Geonguk Han
 * @since 2020-02-12
 */
public class Number {

    private final int value;

    public Number(final String value) {
        this(Integer.parseInt(value));
    }

    public Number(final int value) {
        Objects.requireNonNull(value, "can not be null");
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
