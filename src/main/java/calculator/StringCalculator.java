package calculator;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private final static String DEFAULT_DELIMITER = "[,:]";
    private final static Pattern PATTERN = Pattern.compile("//(.)\n(.*)");
    public static final int DEFAULT_NUMBER = 0;


    public int add(String text) {
        if (!StringUtils.hasText(text)) {
            return DEFAULT_NUMBER;
        }

        Matcher matcher = PATTERN.matcher(text);
        if (matcher.find()) {
            return calculate(matcher.group(1), matcher.group(2));
        }

        return calculate(DEFAULT_DELIMITER, text);
    }

    private static int calculate(String delimiter, String numbers) {
        String[] numberSplit = numbers.split(delimiter);

        validate(numberSplit);

        return Arrays.stream(numberSplit)
                     .mapToInt(Integer::parseInt)
                     .sum();
    }

    private static void validate(String[] numbers) {
        Arrays.stream(numbers)
              .mapToInt(Integer::parseInt)
              .filter(n -> n < 0)
              .findAny()
              .ifPresent(n -> {
                  throw new RuntimeException("음수가 포합되어있습니다. [" + n + "]");
              });
    }
}
