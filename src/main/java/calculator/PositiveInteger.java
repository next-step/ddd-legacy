package calculator;

public class PositiveInteger {
    private final int value;

    public PositiveInteger(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("음수를 사용할 수 없습니다.");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public PositiveInteger sum(final PositiveInteger rightHand) {
        return new PositiveInteger(this.value + rightHand.value);
    }

    public static PositiveInteger zero() {
        return new PositiveInteger(0);
    }
}
