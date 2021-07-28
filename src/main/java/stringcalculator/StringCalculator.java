package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StringCalculator {
    private static final int TWO_LENGTH = 2;
    private static final String ERROR_MINUS_NUMBER_MESSAGE = "마이너스 숫자는 불가능합니다.";

    public int add(String text) {
        if (isEmpty(text)) {
            return 0;
        }

        if (isLessTwoLength(text)) {
            return stringToInt(text);
        }

        String[] tokens = textToTokens(text);
        return Stream.of(tokens)
                .mapToInt(this::stringToInt)
                .sum();
    }

    private String[] textToTokens(String text) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }
        return text.split(",|:");
    }

    private int stringToInt(String text) {
        int result = Integer.parseInt(text);

        if (result < 0) {
            throw new IllegalArgumentException(ERROR_MINUS_NUMBER_MESSAGE);
        }

        return result;
    }

    private boolean isLessTwoLength(String text) {
        return text.length() <= 2;
    }

    private boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }
}
