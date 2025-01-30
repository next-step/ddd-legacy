package calculator;

import java.util.Arrays;

public class NumbersParser {
    private static final String NEGATIVE_NUMBERS_MESSAGE = "음수는 허용되지 않습니다.";
    private static final String NON_NUMERIC_MESSAGE = "숫자가 아닌 값이 포함되어 있습니다.";

    public static int parse(String[] input) {
        try {
            return Arrays.stream(input)
                    .mapToInt(str -> {
                        validateNumeric(str);
                        int num = Integer.parseInt(str.trim());
                        validateNonNegative(num);
                        return num;
                    })
                    .sum();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(NON_NUMERIC_MESSAGE);
        }
    }

    private static void validateNumeric(String str) {
        if (!str.trim().matches("^-?\\d+$")) {
            throw new IllegalArgumentException(NON_NUMERIC_MESSAGE);
        }
    }

    private static void validateNonNegative(int num) {
        if (num < 0) {
            throw new RuntimeException(NEGATIVE_NUMBERS_MESSAGE);
        }
    }
}
