package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {
    private static final String ILLEGAL_NUMBERS_MESSAGE = "올바른 형식의 숫자를 입력해야 합니다.";
    private static final String DELIMITER = ",|:";
    private static final String CUSTOM_DELIMITER = "//(.)\\n(.*)";

    public int add(String text) {
        if (isEmpty(text)) {
            return 0;
        }
        String[] numbers = getNumbers(text);
        if (hasNegative(numbers)) {
            throw new IllegalArgumentException();
        }
        return Arrays.stream(numbers).mapToInt(Integer::parseInt).sum();
    }

    private boolean isEmpty(String numbers) {
        return numbers == null || numbers.isBlank();
    }

    private String[] getNumbers(String text) {
        Matcher matcher = Pattern.compile(CUSTOM_DELIMITER).matcher(text);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            return matcher.group(2).split(customDelimiter);
        }
        return text.split(DELIMITER);
    }

    private boolean hasNegative(String[] numbers) {
        try {
            return Arrays.stream(numbers).anyMatch(n -> Integer.parseInt(n) < 0);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(ILLEGAL_NUMBERS_MESSAGE);
        }
    }
}
