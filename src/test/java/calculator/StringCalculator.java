package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    public static final String DEFAULT_SEPARATOR = "[,:]";
    public static final String PATTERN_ONLY_NUMBER = "^[0-9]*$";
    public static final String PATTERN_CUSTOM = "//(.)\n(.*)";
    private static final Pattern pattern = Pattern.compile(PATTERN_CUSTOM);
    private static int result = 0;

    public static int calculate(String text) throws RuntimeException {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        Matcher matcher = pattern.matcher(text);
        String separator = DEFAULT_SEPARATOR;
        if (matcher.find()) {
            separator = matcher.group(1);
            text = matcher.group(2);
        }

        String[] numbers = text.split(separator);
        checkOnlyNumber(numbers);

        for (String number : numbers) {
            add(number);
        }
        return result;
    }

    private static void checkOnlyNumber(String[] numbers) {
        boolean regex = Pattern.matches(PATTERN_ONLY_NUMBER, String.join("", numbers));
        if (!regex) {
            throw new RuntimeException("Not Numbers");
        }
    }

    private static void add(String number) {
        result += Integer.parseInt(number);
    }
}
