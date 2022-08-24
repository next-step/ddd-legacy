package calculator;

import java.util.Arrays;
import java.util.regex.Pattern;
import org.junit.platform.commons.util.StringUtils;

public class StringCalculator {

    private static final Pattern DEFAULT_EXPRESSION_PATTERN = Pattern.compile("^[0-9]+([,:][0-9]+)*$");
    private static final Pattern CUSTOM_EXPRESSION_PATTERN = Pattern.compile("^//[^0-9a-zA-Z.,:\\$]\\n[0-9]([^0-9a-zA-Z.,:\\$][0-9]+)*$");
    public static final String DEFAULT_DELIMITER = "[,:]";

    public StringCalculator() {}

    public int calculate(String expression) {
        if (StringUtils.isBlank(expression)) {
            return 0;
        }

        if (DEFAULT_EXPRESSION_PATTERN.matcher(expression).matches()) {
            return calculate(expression, DEFAULT_DELIMITER);
        }

        if (CUSTOM_EXPRESSION_PATTERN.matcher(expression).matches()) {
            int splitIndex = expression.indexOf("\n");
            String delimiter = expression.substring(0, splitIndex).replace("//", "").replace("\n", "");
            String newExpression = expression.substring(splitIndex + 1);
            return calculate(newExpression, delimiter);
        }

        throw new RuntimeException();
    }

    private int calculate(String expression, String delimiter) {
        return Arrays.stream(expression.split(delimiter))
            .mapToInt(Integer::parseInt)
            .sum();
    }
}
