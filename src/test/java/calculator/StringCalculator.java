package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.thymeleaf.util.StringUtils;

public class StringCalculator {

    public static final String DELIMITER = ",|:";
    public static final Pattern pattern = Pattern.compile("//(.)\n(.*)");

    public int calculate(final String stringNumber) {
        if (StringUtils.isEmpty(stringNumber)) {
            return 0;
        }
        final Matcher matcher = pattern.matcher(stringNumber);
        if (matcher.find()) {
            final String customDelimiter = matcher.group(1);
            final String targetNumber = matcher.group(2);
            return calculate(targetNumber, DELIMITER + "|" + customDelimiter);
        }
        return calculate(stringNumber, DELIMITER);
    }

    private int calculate(final String stringNumber, final String delimiter) {
        final String[] tokens = stringNumber.split(delimiter);
        if (tokens.length == 1) {
            final int number = Integer.parseInt(tokens[0]);
            if (number < 0) {
                throw new RuntimeException("음수는 계산할 수 없습니다");
            }
        }
        return Arrays.stream(tokens)
            .mapToInt(Integer::parseInt)
            .sum();
    }
}
