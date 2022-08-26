package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorFactory {
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.*)\\n(.*)");
    private static final int CUSTOM_DELIMITER_POSITION = 1;
    private static final int TARGET_POSITION = 2;

    private final String rawStr;
    private final Matcher matcher;

    public CalculatorFactory(String rawStr) {
        this.rawStr = rawStr;
        this.matcher = CUSTOM_DELIMITER_PATTERN.matcher(rawStr);
    }

    public StringDelimiters buildDelimiters() {
        if(matcher.matches()) {
            String customDelimiter = matcher.group(CUSTOM_DELIMITER_POSITION);
            return new StringDelimiters(customDelimiter);
        }
        return new StringDelimiters();
    }

    public StringExpression buildExpression() {
        if(matcher.matches()) {
            return new StringExpression(matcher.group(TARGET_POSITION));
        }
        return new StringExpression(rawStr);
    }
}
