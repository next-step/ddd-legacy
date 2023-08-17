package stringaddcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Separator {
    private static final Pattern COMPILED_PATTERN_BY_CUSTOM_DELIMITER = Pattern.compile(SeparatorConstants.CUSTOM_DELIMITER_PATTERN.getValue());
    private static final String NULL_OR_EMPTY_EXPRESSION_EXCEPTION_MESSAGE = "식은 빈 문자열 또는 null을 입력할 수 없습니다. 현재 값: ";

    private static final int FIRST_CAPTURING_GROUP_INDEX = 1;
    private static final int SECOND_CAPTURING_GROUP_INDEX = 2;

    public Operand[] separate(String expression) {
        validate(expression);
        String delimiter = SeparatorConstants.DEFAULT_DELIMITER.getValue();

        Matcher matcher = COMPILED_PATTERN_BY_CUSTOM_DELIMITER.matcher(expression);
        if (matcher.find()) {
            delimiter = matcher.group(FIRST_CAPTURING_GROUP_INDEX);
            expression = matcher.group(SECOND_CAPTURING_GROUP_INDEX);
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
