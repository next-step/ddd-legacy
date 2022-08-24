package calculator;

public class PositiveNumber {
    private final int value;

    public PositiveNumber(String value) {
        this(Integer.parseInt(value));
    }

    public PositiveNumber(int value) {
        validate(value);
        this.value = value;
    }

    private void validate(int value) {
        if (value <= 0) {
            throw new RuntimeException();
        }
    }

    public int getValue() {
        return value;
    }
}
