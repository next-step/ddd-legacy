package stringcalculator;

import org.junit.platform.commons.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String DEFAULT_DELIMITER_REGEX = ",|:";
    private static final int DELIMITER_GROUP_INDEX = 1;
    private static final int TEXT_GROUP_INDEX = 2;
    private static final int ZERO = 0;
    private static final int ONE = 1;

    public int add(String text) {

        if(StringUtils.isBlank(text)) {
            return ZERO;
        }

        String[] numbers = getNumbers(text);

        if(hasAnyNegativeNumber(numbers)) {
            throw new RuntimeException();
        }

        if(hasOnlyOneNumber(numbers)) {
            return Integer.parseInt(text);
        }

        return Arrays.stream(numbers)
                .map(Integer::parseInt)
                .reduce(ZERO, Integer::sum);
    }

    private String[] getNumbers(String text) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);

        if(hasCustomDelimiter(matcher)) {
            String customDelimiter = matcher.group(DELIMITER_GROUP_INDEX);
            return matcher.group(TEXT_GROUP_INDEX).split(customDelimiter);
        }

        return text.split(DEFAULT_DELIMITER_REGEX);
    }

    private boolean hasCustomDelimiter(Matcher matcher) {
        return matcher.find();
    }

    private boolean hasAnyNegativeNumber(String[] numbers) {
        return Arrays.stream(numbers).map(Integer::parseInt).anyMatch(n -> n < ZERO);
    }

    private boolean hasOnlyOneNumber(String[] numbers) {
        return numbers.length == ONE;
    }
}
