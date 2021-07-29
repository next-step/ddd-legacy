package calculator;

import java.util.Arrays;
import org.springframework.util.StringUtils;

public class StringCalculator {

    public static final int ZERO = 0;

    public int calculate(final String stringNumber) {
        if (isBlank(stringNumber)) {
            return ZERO;
        }
        return calculate(Seperator.of(stringNumber));
    }

    private int calculate(final Seperator seperator) {
        return Arrays.stream(seperator.getTargetNumber()
            .split(seperator.getDelimiter()))
            .map(PositiveNumber::new)
            .map(PositiveNumber::getValue)
            .mapToInt(i -> i)
            .sum();
    }

    private boolean isBlank(final String stringNumber) {
        return !StringUtils.hasText(stringNumber);
    }
}
