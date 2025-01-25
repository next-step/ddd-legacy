package calculator.vo;

public record PositiveInt(int value) {

    private static final int MINIMUM_VALUE = 0;

    public static PositiveInt zero() {
        return new PositiveInt(MINIMUM_VALUE);
    }

    public PositiveInt(final String value) {
        this(Integer.parseInt(value));
    }

    public PositiveInt {
        if (value < MINIMUM_VALUE) {
            throw new IllegalArgumentException("[양의 정수] 0 이상의 정수만 가능합니다. value: %d".formatted(value));
        }
    }

    public PositiveInt add(final PositiveInt other) {
        return new PositiveInt(Math.addExact(value, other.value));
    }
}
