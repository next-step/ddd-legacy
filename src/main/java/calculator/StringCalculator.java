package calculator;

import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {
    private static final String DEFAULT_PATTERN = ",|:";
    private static final String PATTERN_APPEND_CONNECTION = "|";
    private static final Pattern DELIMITER_PATTERN = Pattern.compile("//(.*)\n(.*)");

    public int calculate(String input) {
        if (Strings.isEmpty(input)) {
            return 0;
        }
        List<String> inputs = splitInputs(input);
        Number number = new Number(inputs);
        return number.getValue();
    }

    private List<String> splitInputs(String input) {
        Matcher delimiterMatcher = DELIMITER_PATTERN.matcher(input);
        String delimiter = DEFAULT_PATTERN;
        if (delimiterMatcher.find()) {
            input = delimiterMatcher.group(2);
            delimiter = DEFAULT_PATTERN + PATTERN_APPEND_CONNECTION
                    + Pattern.quote(delimiterMatcher.group(1));
        }
        return Arrays.stream(input.trim()
                .split(delimiter))
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toList());
    }
}
