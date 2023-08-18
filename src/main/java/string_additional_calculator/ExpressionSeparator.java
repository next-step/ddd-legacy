package string_additional_calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionSeparator {
    private static final String CUSTOM_SEPARATE_PREFIX = "//";
    private static final String CUSTOM_SEPARATE_SUFFIX = "\n";
    private static final Pattern CUSTOM_PATTERN = Pattern.compile(String.format("%s(.*)%s(.*)", CUSTOM_SEPARATE_PREFIX, CUSTOM_SEPARATE_SUFFIX));
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("[,:]");
    private static final int CUSTOM_SEPARATOR_INDEX = 1;
    private static final int CUSTOM_SEPARATE_VALID_LENGTH = 1;
    private static final int CUSTOM_CONSTANTS_INDEX = 2;

    public String[] separate(String expression) {
        if (this.hasCustomSeparator(expression)) {
            return separateByCustomSeparator(expression);
        }
        return DEFAULT_PATTERN.split(expression);
    }

    private boolean hasCustomSeparator(String expression) {
        return CUSTOM_PATTERN.matcher(expression).matches();
    }

    private String[] separateByCustomSeparator(String expression) {
        Matcher expressionMatcher = CUSTOM_PATTERN.matcher(expression);
        expressionMatcher.find();
        String separator = expressionMatcher.group(CUSTOM_SEPARATOR_INDEX);
        this.validateCustomSeparator(separator);
        return expressionMatcher.group(CUSTOM_CONSTANTS_INDEX).split(separator);
    }

    private void validateCustomSeparator(String separator) {
        if (separator.length() != CUSTOM_SEPARATE_VALID_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("커스텀 구분자는 %d글자여야 합니다. separator: %s",
                            CUSTOM_SEPARATE_VALID_LENGTH,
                            separator
                    )
            );
        }
    }
}
