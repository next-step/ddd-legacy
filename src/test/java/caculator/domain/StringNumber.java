package caculator.domain;

import java.util.Objects;

public class StringNumber {

    private final int value;

    private StringNumber(int value) {
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
