package stringcalculator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public record Numbers(
        List<Integer> numbers
) {
    private static final int CALCULATION_RESULT_ZERO = 0;
    private static final String EMPTY_USER_INPUT = "";

    public Numbers(String userInput) {
        this(initNumbers(userInput));
    }

    private static List<Integer> initNumbers(String userInput) {
        if (isUserInputEmpty(userInput)) {
            return Collections.emptyList();
        }
        var delimiter = Delimiter.of(userInput);
        var userInputToSplit = getUserInputToSplit(userInput, delimiter);

        var results = userInputToSplit.split(delimiter.delimiter());

        validateUserInput(results);

        return Arrays.stream(results)
                .map(Integer::valueOf)
                .toList();
    }

    private static boolean isUserInputEmpty(String userInput) {
        return EMPTY_USER_INPUT.equals(userInput) || userInput == null;
    }

    private static void validateUserInput(String[] results) {
        Arrays.stream(results)
                .filter(s -> isNotNumber(s) || isNegativeNumber(s))
                .findFirst()
                .ifPresent(s -> {
                    throw new RuntimeException("0 혹은 양수만 입력 가능합니다.");
                });
    }

    private static boolean isNegativeNumber(String s) {
        return Integer.parseInt(s) < 0;
    }

    private static boolean isNotNumber(String s) {
        return !s.chars().allMatch(Character::isDigit);
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
