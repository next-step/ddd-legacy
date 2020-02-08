package calculator;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Geonguk Han
 * @since 2020-02-07
 */
public class StringCalculator {
    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");

    public int add(final String stringValue) {
        if (StringUtils.isEmpty(stringValue)) {
            return 0;
        }

        final Matcher matcher = CUSTOM_PATTERN.matcher(stringValue);
        if (matcher.find()) {
            final String delimiter = matcher.group(1);
            final String[] split = matcher.group(2).split(delimiter);
            return calculateProcess(split);
        }

        final String[] split = stringValue.split(",|;");
        return calculateProcess(split);
    }

    private static int calculateProcess(final String[] stringNumbers) {
        if (isNegative(stringNumbers)) {
            throw new RuntimeException();
        }

        return calculateSum(stringNumbers);
    }

    private static boolean isNegative(final String[] stringNumbers) {
        return Arrays.stream(stringNumbers)
                .mapToInt(Integer::parseInt)
                .anyMatch(value -> value < 0);
    }

    private static int calculateSum(final String[] stringNumbers) {
        return Arrays.stream(stringNumbers)
                .mapToInt(Integer::parseInt)
                .sum();
    }


}
