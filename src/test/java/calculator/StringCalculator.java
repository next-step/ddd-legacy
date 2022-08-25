package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class StringCalculator {

    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public int add(String text) {
        if (isBlank(text)) {
            return 0;
        }

        return Stream.of(toArrays(text))
                .map(Integer::parseInt)
                .reduce(0, Integer::sum);
    }

    private boolean isBlank(String text) {
        return text == null || text.isBlank();
    }

    private String[] toArrays(String text) {
        Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }

        return text.split("[,:]");
    }
}
