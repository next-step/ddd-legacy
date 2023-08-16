package string_additional_calculator;

import java.util.regex.Pattern;

class StringAdditionalCalculator {
    private static final String CUSTOM_SEPARATE_PREFIX = "//";
    private static final String CUSTOM_SEPARATE_SUFFIX = "\\\\n";
    private final Pattern defaultSeparatePattern = Pattern.compile("[,:]");
    private final Pattern customSeparatePattern = Pattern.compile(String.format("%s(.*)%s(.*)", CUSTOM_SEPARATE_PREFIX, CUSTOM_SEPARATE_SUFFIX));

    public int calculate(String expression) {
        String[] numbers = extractNumbers(expression);
        int result = 0;
        for (String number : numbers) {
            result += Integer.parseInt(number);
        }
        return result;
    }

    private String[] extractNumbers(String expression) {
        if (customSeparatePattern.matcher(expression).matches()) {
            String[] splitExpression = expression.split(CUSTOM_SEPARATE_SUFFIX);
            String separator = splitExpression[0].substring(CUSTOM_SEPARATE_PREFIX.length());
            if (separator.length() == 1) {
                return splitExpression[1].split(separator);
            }
            throw new IllegalArgumentException(String.format("커스텀 구분자는 1글자여야 합니다. expression: %s, separator: %s", expression, separator));
        }
        return defaultSeparatePattern.split(expression);
    }
}