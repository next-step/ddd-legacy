package stringcalculator;

public class PositiveNumber {

    public static final int MINIMUM_VALUE = 0;
    private int value;

    public PositiveNumber(String value) {
        this(Integer.parseInt(value));
    }

    public PositiveNumber(int number) {
        if (number < MINIMUM_VALUE) {
            throw new RuntimeException("0보다 큰 값만 가능합니다.");
        }
        this.value = value;
    }
}
