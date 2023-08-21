package stringCalculator;

import static stringCalculator.IntegerUtils.parsePositiveInt;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final int CUSTOM_DELIMITER_GROUP = 1;
    private static final int NUMBERS_GROUP = 2;
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");


    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        if (text.length() == 1) {
            return parsePositiveInt(text);
        }

        return Arrays.stream(extractNumbersUsingDelimiter(text))
            .mapToInt(IntegerUtils::parsePositiveInt)
            .sum();
    }

    private String[] extractNumbersUsingDelimiter(String text) {
        Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (hasCustomDelimiter(m)) {
            final String customDelimiter = m.group(CUSTOM_DELIMITER_GROUP);
            return m.group(NUMBERS_GROUP).split(customDelimiter);
        }
        return text.split(DEFAULT_DELIMITER);
    }

    private boolean hasCustomDelimiter(Matcher m) {
        return m.find();
    }

}
