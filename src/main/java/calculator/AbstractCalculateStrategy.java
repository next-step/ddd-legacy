package calculator;

import java.util.Arrays;

public abstract class AbstractCalculateStrategy implements CalculateStrategy {

    protected int parseNonNegativeNumber(final String value) {
        final int number = Integer.parseInt(value);
        if (number < 0) {
            throw new IllegalArgumentException("문자열 계산기는 음수를 지원하지 않습니다.");
        }
        return number;
    }

    protected int calculateWithDelimiter(final String text, final String delimiter) {
        return Arrays.stream(text.split(delimiter))
                .mapToInt(this::parseNonNegativeNumber)
                .sum();
    }

}
