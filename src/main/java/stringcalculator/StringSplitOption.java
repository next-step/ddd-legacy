package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum StringSplitOption {

    CUSTOM_DELIMITER(Pattern.compile("//(.)\n(.*)")) {
        @Override
        String[] split(String numericString) {
            Matcher m = CUSTOM_DELIMITER.matchPattern.matcher(numericString);
            if (m.find()) {
                String customDelimiter = m.group(1);
                return m.group(2).split(customDelimiter);
            }
            return new String[0];
        }
    },
    DEFAULT_DELIMITER(Pattern.compile("[,:]")) {
        @Override
        String[] split(String numericString) {
            return DEFAULT_DELIMITER.matchPattern.split(numericString);
        }
    };

    private static final String OPERATOR_NOT_FOUND_MESSAGE = "계산 할 수 없는 형태의 입력값입니다. : %s";

    private final Pattern matchPattern;

    StringSplitOption(Pattern matchPattern) {
        this.matchPattern = matchPattern;
    }

    public static StringSplitOption find(String numeric) {
        return Arrays.stream(values())
                .filter(i -> i.matchPattern.matcher(numeric).find())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(OPERATOR_NOT_FOUND_MESSAGE, numeric)));
    }

    abstract String[] split(String numericString);
}
