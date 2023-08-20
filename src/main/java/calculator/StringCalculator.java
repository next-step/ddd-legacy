package calculator;

import java.util.Arrays;

public class StringCalculator {

    private final Parser parser;

    public StringCalculator(Parser parser) {
        this.parser = parser;
    }

    public int add(String text) {

        final int ZERO = 0;

        if (text == null || text.isEmpty()) {
            return ZERO;
        }

        if(isNumber(text)){
            return Integer.parseInt(text);
        }

        String[] parse = parser.toNumbers(text);
        return Arrays.stream(parse).mapToInt(Integer::parseInt).sum();
    }


    private boolean isNumber(String text) {

        final int ZERO = 0;

        try {
            int parseInt = Integer.parseInt(text);
            if (parseInt < ZERO) {
                throw new RuntimeException();
            }
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
        return true;
    }

}
