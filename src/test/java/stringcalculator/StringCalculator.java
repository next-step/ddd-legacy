package stringcalculator;


import java.util.Arrays;

public class StringCalculator {
    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        StringCalculatorTokenParser parser = new StringCalculatorTokenParser(text);

        return Arrays.stream(parser.getIntegerTokens()).peek(NegativeIntegerValidation::checkForNegative).sum();
    }
}
