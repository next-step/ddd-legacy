package calculator.domain;

public record Number(int value) {

    private static final int MIN_VALUE = 0;

    public Number {
        validateNegative(value);
    }

    private void validateNegative(int number) {
        if (number < MIN_VALUE) {
            throw new RuntimeException("음수는 입력할 수 없습니다.");
        }
    }

    Number add(Number number) {
        return new Number(this.value + number.value);
    }
}
