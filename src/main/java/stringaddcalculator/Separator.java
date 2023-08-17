package stringaddcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Separator {
    private static final String DEFAULT_DELIMITER = ",|:";
    private static final String CUSTOM_DELIMITER_PATTERN = "//(.)\n(.*)";
    private static final Pattern COMPILED_PATTERN_BY_CUSTOM_DELIMITER = Pattern.compile(CUSTOM_DELIMITER_PATTERN);
    private static final String NULL_OR_EMPTY_EXPRESSION_EXCEPTION_MESSAGE = "식은 빈 문자열 또는 null을 입력할 수 없습니다. 현재 값: ";

    public Operand[] separate(String expression) {
        validate(expression);
        String delimiter = DEFAULT_DELIMITER;

        Matcher matcher = COMPILED_PATTERN_BY_CUSTOM_DELIMITER.matcher(expression);
        if (matcher.find()) {
            delimiter = matcher.group(1);
            expression = matcher.group(2);
        }

        return split(expression, delimiter);
    }

    private void validate(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException(NULL_OR_EMPTY_EXPRESSION_EXCEPTION_MESSAGE + expression);
        }
    }

    private Operand[] split(String expression, String delimiter) {
        return Arrays.stream(expression.split(delimiter))
                .mapToInt(Integer::parseInt)
                .mapToObj(Operand::new)
                .toArray(Operand[]::new);
    }
}
