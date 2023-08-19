package stringcalculator;

public class Number {
    private static final int MIN_NUMBER_VALUE = 0;
    private final Integer value;

    private Number(Integer value) {
        validate(value);
        this.value = value;
    }

    public static Number of(int value) {
        return new Number(value);
    }

    private void validate(int number) {
        if (number < MIN_NUMBER_VALUE) {
            throw new RuntimeException("음수를 입력할 수 없습니다.");
        }
    }

    public Integer getValue() {
        return value;
    }
}
