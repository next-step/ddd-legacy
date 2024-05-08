package springcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Delimiter {

    private static final String BASIC_DELIMITER_REGEX = ",:";
    private static final String CUSTOM_DELIMITER_PATTERN = "//(.)\n(.*)";
    private String input;
    private String delimiter;

    public Delimiter(String input) {
        this.input = input;
        extractDelimiter();
    }

    private void extractDelimiter() {
        Matcher matcher = Pattern.compile(CUSTOM_DELIMITER_PATTERN).matcher(input);
        if (matcher.find()) {
            delimiter = matcher.group(1);
            input = matcher.group(2);
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
