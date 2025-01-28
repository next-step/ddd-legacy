package mission.step1;

public class PositiveNumber {
    private final int value;

    private PositiveNumber(int value) {
        validate(value);
        this.value = value;
    }

    public static PositiveNumber from(String value) {
        try {
            return new PositiveNumber(Integer.parseInt(value.trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자 형식이 아닙니다: " + value);
        }
    }

    private void validate(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("음수는 허용되지 않습니다: " + number);
        }
    }

    public int getValue() {
        return value;
    }
}