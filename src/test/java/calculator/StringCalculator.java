package calculator;

import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private final String delimiter = ",|:";

    public int add(final String text) {
        if (Strings.isBlank(text)) {
            return 0;
        }
        return Arrays.stream(getStrings(text))
                .mapToInt(Integer::parseInt)
                .sum();
    }

    private String[] getStrings(final String text) {
        final Pattern pattern = Pattern.compile("//(.)\\\\n(.*)");
        final Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return split(matcher.group(1), matcher.group(2));
        }
        return split(delimiter, text);
    }

    private static String[] split(final String delimiter, final String expression) {
        return expression.split(delimiter);
    }
}
