package kitchenpos.calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StringCalculator {

    static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");
    static final String DEFAULT_DELIMITER = "[,:]";
    private int result;

    public void add(String expressions) {
        if (expressions == null || expressions.isBlank()) return;
        PositiveNumbers positiveNumbers = new PositiveNumbers(splitExpressions(expressions));
        result = positiveNumbers.sum();
    }

    private String[] splitExpressions(String expressions) {
        Matcher matcher = CUSTOM_PATTERN.matcher(expressions);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            return matcher.group(2).split(customDelimiter);
        } else {
            return expressions.split(DEFAULT_DELIMITER);
        }
    }

    public int getResult() {
        return result;
    }

    public void clearResult() {
        result = 0;
    }
}
