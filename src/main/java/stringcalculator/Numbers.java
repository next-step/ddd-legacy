package stringcalculator;

import java.util.Arrays;

public class Numbers {
    private static final String ERROR_MINUS_NUMBER_MESSAGE = "마이너스 숫자는 불가능합니다.";
    private static final int MINIMUM_RANGE = 0;

    public static int[] toNumbers(String[] text) {
        return Arrays.stream(text)
                .mapToInt(Numbers::stringToInt)
                .toArray();
    }

    private static int stringToInt(String text) {
        int result = Integer.parseInt(text);

        if (result < MINIMUM_RANGE) {
            throw new IllegalArgumentException(ERROR_MINUS_NUMBER_MESSAGE);
        }

        return result;
    }
}
