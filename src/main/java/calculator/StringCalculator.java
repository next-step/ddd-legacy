package calculator;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private final static String DEFAULT_DELIMITER = "[,:]";
    private final static Pattern PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DEFAULT_NUMBER = 0;
    private static final int MATCHER_DELIMITER = 1;
    private static final int MATCHER_NUMBERS = 2;


    public int add(String text) {
        if (!StringUtils.hasText(text)) {
            return DEFAULT_NUMBER;
        }

        Matcher matcher = PATTERN.matcher(text);
        if (matcher.find()) {
            return calculate(getNumberSplit(matcher.group(MATCHER_DELIMITER), matcher.group(MATCHER_NUMBERS)));
        }

        return calculate(getNumberSplit(DEFAULT_DELIMITER, text));
    }

    private static String[] getNumberSplit(String delimiter, String numbers) {
        return numbers.split(delimiter);
    }

    private int calculate(String[] numberSplit1) {
        validate(numberSplit1);

        return Arrays.stream(numberSplit1)
                     .mapToInt(Integer::parseInt)
                     .sum();
    }

    private static void validate(String[] numbers) {
        Arrays.stream(numbers)
              .mapToInt(Integer::parseInt)
              .filter(n -> n < 0)
              .findAny()
              .ifPresent(n -> {
                  throw new RuntimeException("음수가 포함되어있습니다. [" + n + "]");
              });
    }
}
