package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringCalculator {

    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_IDX = 1;
    private static final int NUMBERS_IDX = 2;

    public int add(final String expression) {
        if (isBlank(expression)) {
            return 0;
        }
        return Arrays.stream(split(expression))
                .mapToInt(Integer::valueOf)
                .sum();
    }

    private boolean isBlank(String expression) {
        return expression == null || expression.trim().length() == 0;
    }

    private String[] split(String expression) {
        Matcher m = DEFAULT_PATTERN.matcher(expression);
        if (m.find()) {
            String delimiter = m.group(DELIMITER_IDX);
            return m.group(NUMBERS_IDX).split(delimiter);
        }
        return expression.split(DEFAULT_DELIMITER);
    }
}
