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
        validateNumber(value);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private void validateNumber(final int value) {
        if (value < 0) {
            throw new RuntimeException();
        }
    }

    public Number sum(final Number number) {
        return new Number(value + number.value);
    }
}
