package calculator.domain;

public class AddNumber {

    private final int value;

    public AddNumber(int value) {
        this.value = value;
        validateNegative(this.value);
    }

    private void validateNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("덧셈 값은 음수일 수 없습니다.");
        }
    }

    public static AddNumber from(String value) {
        try {
            return new AddNumber(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("정수 외 값은 덧셈 값이 될 수 없습니다.");
        }
    }
}
