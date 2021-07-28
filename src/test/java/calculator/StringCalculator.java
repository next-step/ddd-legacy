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
        return Arrays.stream(stringNumber.split(delimiter))
            .mapToInt(Integer::parseInt)
            .sum();
    }
}
