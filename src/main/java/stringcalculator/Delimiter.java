package stringcalculator;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Delimiter {
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    public static final String DEFAULT_DELIMITER = "[,:]";

    private final String text;
    private final String customDelimiter;

    public Delimiter(String text) {
        this.text = text;
        this.customDelimiter = parseCustomDelimiter(text);
    }

    private String parseCustomDelimiter(String text) {
        Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public String[] splitText() {
        if (Objects.nonNull(customDelimiter)) {
            Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
            if (!matcher.find()) {
                throw new IllegalArgumentException("Invalid input format");
            }
            String numbersPart = matcher.group(2);
            return numbersPart.split(customDelimiter);
        } else {
            return text.split(DEFAULT_DELIMITER);
        }
    }
}
