package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.thymeleaf.util.StringUtils;

public class StringCalculator {

    public static final String DELIMITER = ",|:";
    public static final String SEPARATOR = "|";
    public static final Pattern pattern = Pattern.compile("//(.)\n(.*)");
    public static final char NEGATIVE_CHAR = '-';
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;

    public int calculate(final String stringNumber) {
        if (StringUtils.isEmpty(stringNumber)) {
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
        if (stringNumber.charAt(ZERO) == NEGATIVE_CHAR) {
            throw new RuntimeException("음수는 계산할 수 없습니다");
        }
        return Arrays.stream(stringNumber.split(delimiter))
            .mapToInt(Integer::parseInt)
            .sum();
    }
}
