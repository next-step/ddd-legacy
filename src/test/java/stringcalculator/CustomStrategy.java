package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomStrategy implements CalculatorStrategy {
    private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");
    private static final int CUSTOM_SEPARATOR_INDEX = 1;
    private static final int MATCHER_BODY_INDEX = 2;
    private Matcher matcher;

    @Override
    public boolean isCustom(final String input) {
        this.matcher = pattern.matcher(input);
        return this.matcher.matches();
    }

    @Override
    public String getSeparator() {
        return this.matcher.group(CUSTOM_SEPARATOR_INDEX);
    }

    public String getBody() {
        return this.matcher.group(MATCHER_BODY_INDEX);
    }
}