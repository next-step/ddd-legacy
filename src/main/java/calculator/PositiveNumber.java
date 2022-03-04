package calculator;

public class PositiveNumber {
    public static final PositiveNumber ZERO = new PositiveNumber(0);
    private final int value;

    public PositiveNumber(int value) {
        if (value < 0) {
            throw new RuntimeException("음수는 생성할 수 없습니다");
        }
        this.value = value;
    }

    public int value() {
        return value;
    }

    public PositiveNumber plus(PositiveNumber other) {
        return new PositiveNumber(this.value + other.value);
    }
}
