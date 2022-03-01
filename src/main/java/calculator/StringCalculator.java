package calculator;

import java.util.Arrays;

public class StringCalculator {

    private static final String DEFAULT_SEPARATOR = "[,:]";

    private StringCalculator() { }

    public static int add(String text) {
        if (isEmptyString(text)) {
            return 0;
        }

        return Arrays.stream(text.split(DEFAULT_SEPARATOR))
                     .mapToInt(Integer::parseInt)
                     .sum();
    }

    private static boolean isEmptyString(String text) {
        return null == text || "".equals(text.trim());
    }
}
