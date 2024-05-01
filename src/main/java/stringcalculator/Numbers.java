package stringcalculator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record Numbers(
        List<Integer> numbers
) {
    private static final int CALCULATION_RESULT_ZERO = 0;
    private static final String EMPTY_USER_INPUT = "";

    public Numbers(String userInput) {
        this(initNumbers(userInput));
    }

    private static List<Integer> initNumbers(String userInput) {
        if (EMPTY_USER_INPUT.equals(userInput) || userInput == null) {
            return Collections.emptyList();
        }
        var delimiter = Delimiter.of(userInput);
        var userInputToSplit = getUserInputToSplit(userInput, delimiter);

        return Arrays.stream(userInputToSplit.split(delimiter.delimiter()))
                .map(Integer::valueOf)
                .toList();
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
        return this.numbers.stream().mapToInt(Integer::intValue).sum();
    }
}
