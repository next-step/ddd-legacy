package StringCalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        String delimiter = DEFAULT_DELIMITER;
        String numbersStr = text;

        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.matches()) {
            delimiter = Pattern.quote(matcher.group(1));
            numbersStr = matcher.group(2);
        }

        return Arrays.stream(numbersStr.split(delimiter))
                .map(Number::new)
                .mapToInt(Number::getValue)
                .sum();
    }
}
