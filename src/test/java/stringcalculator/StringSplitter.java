package stringcalculator;

import java.util.regex.Pattern;

public class StringSplitter {

    private static final String DEFAULT_DELIMITER_REGEX = "[,:]";
    private static final String  DEFAULT_CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";

    private final Pattern customDelimiterPattern;

    public StringSplitter() {
        this(DEFAULT_CUSTOM_DELIMITER_REGEX);
    }

    public StringSplitter(String regex) {
        this(Pattern.compile(regex));
    }

    public StringSplitter(Pattern pattern) {
        this.customDelimiterPattern = pattern;
    }

    public String[] split(String text) {
        var m = customDelimiterPattern.matcher(text);
        if (m.find()) {
            var delimiter = m.group(1);
            return m.group(2).split(delimiter);
        }
        return text.split(DEFAULT_DELIMITER_REGEX);
    }
}
