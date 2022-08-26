package calculator;

public class PositiveNumber {

    private final int value;

    public PositiveNumber(String value) {
        this(Integer.parseInt(value));
    }

    public PositiveNumber(int value) {
        checkValidValue(value);
        this.value = value;
    }

    private void checkValidValue(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("양수만 입력 가능합니다.");
        }
    }

    public PositiveNumber sum(PositiveNumber other) {
        return new PositiveNumber(value + other.value);
    }

    public int getValue() {
        return value;
    }
}
