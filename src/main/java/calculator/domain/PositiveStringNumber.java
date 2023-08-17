package calculator.domain;

import java.util.Objects;

public class PositiveStringNumber {

    public static final int POSITIVE_BOUNDARY = 0;
    private final String value;

    public PositiveStringNumber(String value) {
        validatePositiveStringNumber(value);
        this.value = value;
    }

    private void validatePositiveStringNumber(String value) {
        try {
            if (Integer.parseInt(value) < POSITIVE_BOUNDARY) {
                throw new RuntimeException("덧셈에는 양수만 허용됩니다. value = " + value);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("덧셈에는 숫자만 허용됩니다. value = " + value);
        }
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
