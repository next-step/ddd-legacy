package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Syntax {
    private static final String REPLACE_CHARACTER = "";
    private static final String DEFAULT_SEPARATOR = "[,:]";
    private static final String SEPARATOR_START = "//";
    private static final String SEPARATOR_END = "\\\\n";
    private static final String REGEX = "^((" + SEPARATOR_START + ").(" + SEPARATOR_END + "))";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private static final int SEPARATOR_INDEX = 0;
    private static final int NUMBER_INDEX = 1;

    public static String[] parse(String value) {
        Matcher matcher = PATTERN.matcher(value);
        if (matcher.lookingAt()) {
            String[] values = value.split(SEPARATOR_END);
            String separator = values[SEPARATOR_INDEX]
                    .replace(SEPARATOR_START, REPLACE_CHARACTER);

            return values[NUMBER_INDEX].split(separator);
        }

        return value.split(DEFAULT_SEPARATOR);
    }
}
