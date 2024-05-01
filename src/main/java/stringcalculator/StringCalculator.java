package stringcalculator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StringCalculator {
    public static final String REGULAR_DELIMITER_REGEX = "[,;]";
    public static final String EMPTY_USER_INPUT = "";
    public static final int CALCULATION_RESULT_ZERO = 0;
    private final List<Integer> numbers;

    public StringCalculator(String userInput) {
        this.numbers = initNumbers(userInput);
    }

    private static List<Integer> initNumbers(String userInput) {
        if (EMPTY_USER_INPUT.equals(userInput) || userInput == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(userInput.split(REGULAR_DELIMITER_REGEX))
                .map(Integer::valueOf)
                .toList();
    }

    public int calculate() {
        if (numbers.isEmpty()) {
            return CALCULATION_RESULT_ZERO;
        }
        return numbers.stream().mapToInt(Integer::intValue).sum();
    }
}
