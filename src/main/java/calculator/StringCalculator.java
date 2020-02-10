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
    private static final String DEFAULT_DELIMITER = ",|;";

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

        final String[] split = value.split(DEFAULT_DELIMITER);
        return calculate(split);
    }

    private int calculate(final String[] numbers) {
        Numbers number = new Numbers(Arrays.asList(numbers));
        number.validateNumber();
        return number.sum();
    }
}
