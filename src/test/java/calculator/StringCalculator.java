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
        final Matcher m = Pattern.compile("//(.)\\\\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            String[] tokens = m.group(2).split(customDelimiter);
            return Arrays.stream(tokens)
                    .mapToInt(Integer::parseInt)
                    .sum();
        }
        if (text.contains(",") || text.contains(":")) {
            return Arrays.stream(text.split(delimiter))
                    .mapToInt(Integer::parseInt)
                    .sum();
        }
        return Integer.parseInt(text);
    }
}
