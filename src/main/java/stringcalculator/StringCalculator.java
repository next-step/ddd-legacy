package stringcalculator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

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
        String delimiter = REGULAR_DELIMITER_REGEX;
        var matcher = Pattern.compile("//(.*?)\n").matcher(userInput);
        if (matcher.find()) {
            delimiter = matcher.group(1);
            return Arrays.stream(userInput.split("\n")[1].split(delimiter))
                    .map(Integer::valueOf)
                    .toList();
        } else {
            delimiter = REGULAR_DELIMITER_REGEX;
            return Arrays.stream(userInput.split(delimiter))
                    .map(Integer::valueOf)
                    .toList();
        }
    }

    public int calculate() {
        if (numbers.isEmpty()) {
            return CALCULATION_RESULT_ZERO;
        }
        return numbers.stream().mapToInt(Integer::intValue).sum();
    }
}
