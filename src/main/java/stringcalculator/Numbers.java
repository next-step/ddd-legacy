package stringcalculator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record Numbers(
        List<Number> numbers
) {
    private static final int CALCULATION_RESULT_ZERO = 0;
    private static final String EMPTY_USER_INPUT = "";

    public Numbers(String userInput) {
        this(initNumbers(userInput));
    }

    private static List<Number> initNumbers(String userInput) {
        if (isUserInputEmpty(userInput)) {
            return Collections.emptyList();
        }
        var delimiter = Delimiter.of(userInput);
        var userInputToSplit = getUserInputToSplit(userInput, delimiter);

        var results = userInputToSplit.split(delimiter.delimiter());

        return Arrays.stream(results)
                .map(Number::of)
                .toList();
    }

    private static boolean isUserInputEmpty(String userInput) {
        return EMPTY_USER_INPUT.equals(userInput) || userInput == null;
    }

    private static String getUserInputToSplit(String userInput, Delimiter delimiter) {
        if (delimiter.isCustomized()) {
            return userInput.split(Delimiter.CUSTOM_DELIMITER_REGEX)[1];
        }
        return userInput;
    }

    public int sum() {
        if (numbers.isEmpty()) {
            return CALCULATION_RESULT_ZERO;
        }
        return this.numbers.stream()
                .mapToInt(Number::number)
                .sum();
    }
}
