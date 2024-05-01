package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Delimiter(
        String delimiter,
        boolean isCustomized
){
    public static final String REGULAR_DELIMITER_REGEX = "[,;]";

    public static final String CUSTOM_DELIMITER_REGEX = "//(.*?)\n";
    public static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile(CUSTOM_DELIMITER_REGEX);

    public Delimiter(String delimiter, boolean isCustomized) {
        this.delimiter = delimiter;
        this.isCustomized = isCustomized;
    }

    public static Delimiter of(String userInput) {
        var matcher = CUSTOM_DELIMITER_PATTERN.matcher(userInput);
        if (hasCustomDelimiter(matcher)) {
            return new Delimiter(matcher.group(1), true);
        }
        return new Delimiter(REGULAR_DELIMITER_REGEX, false);
    }

    private static boolean hasCustomDelimiter(Matcher matcher) {
        return matcher.find();
    }
}
