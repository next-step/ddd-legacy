package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String SEPARATOR = ",|:";
    private static final String DELIMITER = "//(.)\n(.*)";

    public int add(final String text) {
        if (text == null || text.isEmpty()) return 0;
        try {
            String[] numbers = parse(text);
            return sumOfNumbers(numbers);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private String[] parse(String text) {
        Matcher m = Pattern.compile(DELIMITER).matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(Pattern.quote(customDelimiter));
        } else {
            return text.split(SEPARATOR);
        }
    }

    private int sumOfNumbers(String[] numbers) {
        return Arrays.stream(numbers)
                .mapToInt(this::validate)
                .sum();
    }

    private int validate(String number) {
        try {
            int num = Integer.parseInt(number);
            if (num < 0) {
                throw new RuntimeException("양의 정수를 입력하세요.");
            }
            return num;
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자를 입력하세요.");
        }
    }

}
