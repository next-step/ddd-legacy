package calculator;

import java.util.Arrays;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final Pattern pattern = Pattern.compile("^[0-9]+([,:][0-9]+)*$");
    public static final String EXPRESSION_DEFAULT_DELIMITER = "[,:]";

    public StringCalculator() {}

    public int calculate(String expression) {
        if (!pattern.matcher(expression).matches()) {
            throw new RuntimeException();
        }

        return Arrays.stream(expression.split(EXPRESSION_DEFAULT_DELIMITER))
            .mapToInt(Integer::parseInt)
            .sum();
    }
}
