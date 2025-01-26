package calculator;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelimiterParser {
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");

    private DelimiterParser() {}

    public static DelimiterParser getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final DelimiterParser INSTANCE = new DelimiterParser();
    }

    public NumberGroups parse(String input) {
        String[] tokens = parseInput(input);
        return new NumberGroups(tokens);
    }

    private String[] parseInput(String input) {
        if (isEmpty(input)) {
            return new String[0];
        }
        Matcher matcher = CUSTOM_PATTERN.matcher(input);
        return matcher.matches() ?
                matcher.group(2).split(matcher.group(1)) :
                input.split(DEFAULT_DELIMITER);
    }

    private boolean isEmpty(String input) {
        return input == null || input.isBlank();
    }
}
