package string_additional_calculator;

public class PositiveNumber {
    public static final PositiveNumber ZERO = new PositiveNumber(0);

    private final int value;

    private PositiveNumber(int value) {
        if (isNegativeNumber(value)) {
            throw new RuntimeException(String.format("문자열 계산기에 상수는 음수가 될 수 없습니다. number: %s", value));
        }
        this.value = value;
    }

    private static boolean isNegativeNumber(int value) {
        return value < 0;
    }

    public static PositiveNumber from(String stringNumber) {
        validate(stringNumber);
        return new PositiveNumber(Integer.parseInt(stringNumber));
    }

    private static void validate(String stringNumber) {
        try {
            Integer.parseInt(stringNumber);
        } catch (NumberFormatException e) {
            throw new RuntimeException(String.format("문자열 계산기에 상수는 숫자 이외의 값은 전달할 수 없습니다. number: %s", stringNumber));
        }
    }

    public PositiveNumber sum(PositiveNumber positiveNumber) {
        return new PositiveNumber(this.value + positiveNumber.value);
    }

    public int getValue() {
        return this.value;
    }
}
