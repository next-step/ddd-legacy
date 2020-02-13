package calculator;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Geonguk Han
 * @since 2020-02-07
 */
public class StringCalculator {

    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String DEFAULT_DELIMITER = ",|;";

    public int add(final String input) {
        if (StringUtils.isEmpty(input)) {
            return 0;
        }

        final Matcher matcher = CUSTOM_PATTERN.matcher(input);
        if (matcher.find()) {
            final String delimiter = matcher.group(1);
            final String[] split = matcher.group(2).split(delimiter);
            final Numbers numbers = parseToNumbers(split);
            return numbers.sum();
        }

        final String[] split = input.split(DEFAULT_DELIMITER);
        final Numbers numbers = parseToNumbers(split);
        return numbers.sum();
    }

    private Numbers parseToNumbers(final String[] numbers) {
        final List<Number> collect = Arrays.asList(numbers)
                .stream()
                .map(Number::new)
                .collect(Collectors.toList());

        return new Numbers(collect);
    }
}
