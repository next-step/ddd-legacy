package calculator;


import calculator.utils.StringUtils;

public class StringCalculator {

    private static final int DEFAULT_VALUE_ZERO = 0;

    public int add(String text) {
        if (validation(text)) {
            return DEFAULT_VALUE_ZERO;
        }

        Numbers numbers = Numbers.create(StringUtils.separate(text));

        return numbers.sum();
    }

    private boolean validation(String text) {
        return text == null || text.isEmpty();
    }
}
