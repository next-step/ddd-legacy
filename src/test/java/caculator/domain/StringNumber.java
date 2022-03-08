package caculator.domain;

import java.util.Objects;

public class StringNumber {

    private static final int MINIMUM_BOUND = 0;
    public static final StringNumber MINIMUM = new StringNumber(MINIMUM_BOUND);

    private final int value;

    private StringNumber(int value) {
        validatePositive(value);
        this.value = value;
    }

    public static StringNumber valueOf(String number) {
        return new StringNumber(toInteger(number));
    }

    private static int toInteger(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new StringNumberException(number);
        }
    }

    private static void validatePositive(int number) {
        if (isNegative(number)) {
            throw new StringNumberException(number);
        }
    }

    private static boolean isNegative(int number) {
        return number < MINIMUM_BOUND;
    }

    public StringNumber add(StringNumber other) {
        return new StringNumber(value + other.value());
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StringNumber that = (StringNumber) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
