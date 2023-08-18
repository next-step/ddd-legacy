package calculator.domain;

import java.util.Objects;

public class PositiveStringNumber {

    public static final PositiveStringNumber ZERO = new PositiveStringNumber(0);
    private static final int POSITIVE_BOUNDARY = 0;
    private final int value;

    private PositiveStringNumber(int value) {
        if (value < POSITIVE_BOUNDARY) {
            throw new RuntimeException("덧셈에는 양수만 허용됩니다. value = " + value);
        }
        this.value = value;
    }

    public static PositiveStringNumber of(String value) {
        try {
            return new PositiveStringNumber(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            throw new RuntimeException("덧셈에는 숫자만 허용됩니다. value = " + value);
        }
    }

    public PositiveStringNumber add(PositiveStringNumber other) {
        return new PositiveStringNumber(this.value + other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PositiveStringNumber)) {
            return false;
        }
        PositiveStringNumber that = (PositiveStringNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
