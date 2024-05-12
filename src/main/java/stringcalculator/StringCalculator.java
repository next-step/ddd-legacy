package stringcalculator;

import java.util.List;

public class StringCalculator {
    private final static int EMPTY_TEXT_CALCULATE_RESULT = 0;

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return EMPTY_TEXT_CALCULATE_RESULT;
        }

        StringCalculatorTokenParser parser = new StringCalculatorTokenParser();
        List<NonNegativeInteger> nonNegativeIntegers = parser.getIntegerTokens(text);

        return NonNegativeInteger.sum(nonNegativeIntegers).getInteger();
    }
}
