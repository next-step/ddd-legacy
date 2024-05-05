package stringcalculator;


import java.util.Arrays;
import java.util.List;

public class StringCalculator {
    private final int EMPTY_TEXT_CALCULATE_RESULT = 0;

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return EMPTY_TEXT_CALCULATE_RESULT;
        }

        StringCalculatorTokenParser parser = new StringCalculatorTokenParser();
        List<NonNegativeInteger> nonNegativeIntegers = parser.getIntegerTokens(text);

        return nonNegativeIntegers.stream()
                .mapToInt(NonNegativeInteger::getInteger)
                .sum();
    }
}
