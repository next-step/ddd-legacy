package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;

public class StringCalculator {

    public static final String DELIMITER = ",|:";
    public static final String SEPARATOR = "|";
    public static final Pattern pattern = Pattern.compile("//(.)\n(.*)");
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;

    public int calculate(final String stringNumber) {
        if (isBlank(stringNumber)) {
            return ZERO;
        }

        final Matcher matcher = pattern.matcher(stringNumber);
        if (matcher.find()) {
            final String customDelimiter = matcher.group(ONE);
            final String targetNumber = matcher.group(TWO);
            return calculate(targetNumber, DELIMITER + SEPARATOR + customDelimiter);
        }

        return calculate(stringNumber, DELIMITER);
    }

    private int calculate(final String stringNumber, final String delimiter) {
        return Arrays.stream(stringNumber.split(delimiter))
            .mapToInt(Integer::parseInt)
//            .peek(this::checkNegativenumber)
            .sum();
    }

    private boolean isBlank(final String stringNumber) {
        return !StringUtils.hasText(stringNumber);
    }

//    private void checkNegativenumber(final int number) {
//        if (number < ZERO) {
//            throw new RuntimeException(NEGATIVE_EXCEPTION_MESSAGE);
//        }
//    }
}
