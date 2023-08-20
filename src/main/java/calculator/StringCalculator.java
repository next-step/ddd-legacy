package calculator;

import java.util.Arrays;

public class StringCalculator {

    private final Parser parser;
    private static final int ZERO = 0;


    public StringCalculator(Parser parser) {
        this.parser = parser;
    }

    public int add(String text) {

        if (isNullAndEmpty(text)) {
            return ZERO;
        }

        if (isNumber(text)) {
            return Integer.parseInt(text);
        }

        return parseToSum(text);
    }

    private int parseToSum(String text) {

        String[] result = {};

        if (text.contains(",") | text.contains(":")) {
            result = text.split(",|:");
            return Arrays.stream(result).mapToInt(Integer::parseInt).sum();
        }

        result = parser.parse(text);
        return Arrays.stream(result).mapToInt(Integer::parseInt).sum();
    }

    private boolean isNullAndEmpty(String text) {
        return text == null || text.isEmpty();
    }


    private boolean isNumber(String text) {
        try {
            int parseInt = Integer.parseInt(text);
            isNegative(parseInt);
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
        return true;
    }

    private void isNegative(int parseInt) {
        if (parseInt < ZERO) {
            throw new RuntimeException();
        }
    }

}
