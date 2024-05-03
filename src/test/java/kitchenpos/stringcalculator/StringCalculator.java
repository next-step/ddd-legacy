package kitchenpos.stringcalculator;

import java.util.Arrays;

public class StringCalculator {
    public int add(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        String[] numberStrings = InputParser.parse(input);

        return sumNumbers(numberStrings);
    }

    private int sumNumbers(String[] numberStrings) {
        return Arrays.stream(numberStrings)
                     .mapToInt(numberString -> new PositiveNumber(numberString).getValue())
                     .sum();
    }
}
