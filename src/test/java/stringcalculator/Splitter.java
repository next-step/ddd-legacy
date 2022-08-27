package stringcalculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Splitter {

    private static final String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";
    private static final String DEFAULT_DELIMITER = ",|:";
    private static final int CUSTOM_DELIMITER_INDEX = 1;
    private static final int NUMBER_VALUE_INDEX = 2;

    private static final Pattern pattern = Pattern.compile(CUSTOM_DELIMITER_REGEX);

    public Splitter() {
    }

    public List<String> split(String value) {
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            String customDelimiter = matcher.group(CUSTOM_DELIMITER_INDEX);
            String[] split = matcher.group(NUMBER_VALUE_INDEX).split(customDelimiter);
            return List.of(split);
        }
        return List.of(value.split(DEFAULT_DELIMITER));
    }
}
