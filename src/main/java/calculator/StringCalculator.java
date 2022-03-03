package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final int DEFAULT_VALUE = 0;
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final String CUSTOM_DELIMITER = "[,:%s]";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\\\\n(.*)");

    private StringCalculator() { }

    public static int add(String text) {
        if (isBlank(text)) {
            return DEFAULT_VALUE;
        }

        return Arrays.stream(split(text))
                     .map(PositiveNumber::from)
                     .reduce(PositiveNumber.ZERO, PositiveNumber::add)
                     .getValue();
    }

    private static boolean isBlank(String text) {
        return null == text || "".equals(text.trim());
    }

    private static String[] split(String text) {
        Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (m.find()) {
            String customDelimiter = String.format(CUSTOM_DELIMITER, m.group(1));
            return m.group(2).split(customDelimiter);
        }

        return text.split(DEFAULT_DELIMITER);
    }
}
