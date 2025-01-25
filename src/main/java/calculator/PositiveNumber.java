package calculator;

public class PositiveNumber {

    private final int value;


    private PositiveNumber(int value) {
        this.value = value;
    }

    public static PositiveNumber valueOf(int value) {
        validatePositive(value);

        if (value < PositiveNumberCache.MAX_VALUE) {
            return PositiveNumberCache.cache[value];
        }
        return new PositiveNumber(value);
    }

    public static PositiveNumber valueOf(String stringValue) {
        return PositiveNumber.valueOf(Integer.parseInt(stringValue));
    }

    private static void validatePositive(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("음수는 입력할 수 없습니다.");
        }
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return value == ((PositiveNumber) obj).getValue();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    private static final class PositiveNumberCache {
        static final int MAX_VALUE = 255;

        static final PositiveNumber[] cache = new PositiveNumber[MAX_VALUE + 1];

        static {
            for (int i = 0; i < cache.length; i++) {
                cache[i] = new PositiveNumber(i);
            }
        }
    }

}
