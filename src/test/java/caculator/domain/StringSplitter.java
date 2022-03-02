package caculator.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSplitter {

    public static final String DEFAULT_DELIMITER = "[,:]";
    public static final Pattern CUSTOM_DELIMITER = Pattern.compile("//(.)\n(.*)");
    public static final int CUSTOM_DELIMITER_INDEX = 1;
    public static final int CUSTOM_NUMBERS_INDEX = 2;

    private StringSplitter() {
        throw new AssertionError();
    }

    public static Numbers split(String stringNumbers) {
        Matcher matcher = CUSTOM_DELIMITER.matcher(stringNumbers);
        if (matcher.find()) {
            return splitByCustomDelimiter(matcher);
        }
        return splitByDefaultDelimiter(stringNumbers);
    }

    private static Numbers splitByCustomDelimiter(Matcher matcher) {
        String customDelimiter = matcher.group(CUSTOM_DELIMITER_INDEX);
        String numbers = matcher.group(CUSTOM_NUMBERS_INDEX);
        return toNumbers(customDelimiter, numbers);
    }

    private static Numbers splitByDefaultDelimiter(String input) {
        return toNumbers(DEFAULT_DELIMITER, input);
    }

    private static Numbers toNumbers(String delimiter, String numbers) {
        return Numbers.from(numbers.split(delimiter));
    }

}
