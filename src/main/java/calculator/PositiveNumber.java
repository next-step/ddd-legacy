package calculator;

public class PositiveNumber {

    private final int value;

    private PositiveNumber(int value) {
        if (value < 0) {
            throw new RuntimeException();
        }
        this.value = value;
    }

    public static PositiveNumber of(String value) {
        return new PositiveNumber(Integer.parseInt(value));
    }

    public int getValue() {
        return value;
    }
}
