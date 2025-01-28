package calculator;

import java.util.Arrays;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        String[] numberString = StringParser.splitNumbers(text);
        return Arrays.stream(numberString).mapToInt(StringValidator::validateNumberAndPositive)
            .sum();
    }
}
