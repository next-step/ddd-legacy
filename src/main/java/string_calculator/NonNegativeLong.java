package string_calculator;

import java.util.Objects;

public class NonNegativeLong {

    private final long value;

    public NonNegativeLong(final long value) {
        if (value < 0) {
            throw new IllegalArgumentException("value는 음수일 수 없습니다");
        }
        this.value = value;
    }

    public NonNegativeLong(final String value) {
        this(Long.parseLong(value));
    }

    public long value() {
        return this.value;
    }

    @Override
    public String toString() {
        return "NonNegativeLong(" + this.value + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NonNegativeLong that = (NonNegativeLong) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
