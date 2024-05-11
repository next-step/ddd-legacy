package springcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Delimiter {

    private static final String BASIC_DELIMITER_REGEX = ",:";
    private static final String CUSTOM_DELIMITER_PATTERN = "//(.)\n(.*)";
    private static final int CUSTOM_DELIMITER_GROUP_INDEX = 1;
    private static final int INPUT_GROUP_INDEX = 2;
    private static final Pattern CUSTOM_DELIMITER_REGEX_PATTERN = Pattern.compile(CUSTOM_DELIMITER_PATTERN);

    private String input;
    private String delimiter;

    public Delimiter(String input) {
        this.input = input;
        extractDelimiter();
    }

    private void extractDelimiter() {
        Matcher matcher = CUSTOM_DELIMITER_REGEX_PATTERN.matcher(input);
        if (matcher.find()) {
            delimiter = matcher.group(CUSTOM_DELIMITER_GROUP_INDEX);
            input = matcher.group(INPUT_GROUP_INDEX);
            return;
        }

        delimiter = ",";
    }

    public String[] extractNumbers() {
        if (delimiter == null) {
            return new String[]{input};
        }
        return input.split("[" + Pattern.quote(BASIC_DELIMITER_REGEX) + Pattern.quote(delimiter) + "]");
    }
}
