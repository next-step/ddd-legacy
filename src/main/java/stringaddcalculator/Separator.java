package stringaddcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Separator {
    private static final String DEFAULT_DELIMITER = ",|:";
    private static final String CUSTOM_DELIMITER_PATTERN = "//(.)\n(.*)";

    public Operand[] separate(String expression) {
        validate(expression);
        String delimiter = DEFAULT_DELIMITER;

        Matcher matcher = Pattern.compile(CUSTOM_DELIMITER_PATTERN).matcher(expression);
        if (matcher.find()) {
            delimiter = matcher.group(1);
            expression = matcher.group(2);
        }

        return split(expression, delimiter);
    }

    private void validate(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    private Operand[] split(String expression, String delimiter) {
        return Arrays.stream(expression.split(delimiter))
                .mapToInt(Integer::parseInt)
                .mapToObj(Operand::new)
                .toArray(Operand[]::new);
    }
}
