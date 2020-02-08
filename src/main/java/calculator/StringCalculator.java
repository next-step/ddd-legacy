package calculator;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static calculator.Number.*;

/**
 * @author Geonguk Han
 * @since 2020-02-07
 */
public class StringCalculator {

    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");

    public int add(final String value) {
        if (StringUtils.isEmpty(value)) {
            return 0;
        }

        final Matcher matcher = CUSTOM_PATTERN.matcher(value);
        if (matcher.find()) {
            final String delimiter = matcher.group(1);
            final String[] split = matcher.group(2).split(delimiter);
            return calculate(split);
        }

        final String[] split = value.split(",|;");
        return calculate(split);
    }

    private int calculate(final String[] numbers) {
        validNegative(numbers);
        return calculateSum(numbers);
    }

    private int calculateSum(final String[] numbers) {
        return Arrays.stream(numbers)
                .mapToInt(Integer::parseInt)
                .sum();
    }

}
