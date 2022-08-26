package calculator.number;

import java.util.Objects;

public class PositiveNumber {
    private static final int ZERO = 0;

    private int value;

    public PositiveNumber(final String number) {
        this.validate(number);
        this.value = Integer.parseInt(number);
    }

    private void validate(final String number) {
        if (number.chars()
                      .noneMatch(character -> Character.isDigit(character))) {
            throw new IllegalArgumentException("숫자가 아닌 문자는 입력 불가능 합니다.");
        }

        if (Integer.parseInt(number) < ZERO) {
            throw new IllegalArgumentException("0보다 작은 숫자는 입력 불가능합니다.");
        }
    }

    public int value() {
        return this.value;
    }
}
