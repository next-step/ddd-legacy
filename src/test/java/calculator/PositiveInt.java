package calculator;

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
            throw new RuntimeException("음수는 입력할 수 없습니다.");
        }
    }

    public PositiveInt add(PositiveInt other) {
        return new PositiveInt(Math.addExact(value, other.value));
    }
}
