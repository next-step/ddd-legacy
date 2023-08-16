package calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");

    public int add(final String text) {

        if (text == null || text.isEmpty()) {
            return 0;
        }

        String[] stringNumbers = parseStringToNumbers(text);
        if (stringNumbers.length == 0) {
            return 0;
        }

        return calculateSumOfNumbers(parseStringListToNumberList(stringNumbers));
    }

    private int calculateSumOfNumbers(List<Integer> numbers) {
        int total = 0;
        for (Integer number : numbers) {
            total += number;
        }
        return total;
    }

    private String[] parseStringToNumbers(final String text) {
        Matcher m = pattern.matcher(text);
        if (m.find()) {
            return m.group(2).trim().split(m.group(1));
        }

        return text.split(DEFAULT_DELIMITER);
    }

    private List<Integer> parseStringListToNumberList(final String[] stringNumbers) {

        List<Integer> numbers = new ArrayList<>();
        for (String stringNumber : stringNumbers) {
            if (isEmptyString(stringNumber)) {
                continue;
            }

            if (!isPositiveInteger(stringNumber)) {
                throw new RuntimeException("음수를 입력함");
            }

            numbers.add(parseNumber(stringNumber));
        }

        return numbers;
    }

    private static boolean isEmptyString(String s) {
        return s.length() == 0;
    }

    private int parseNumber(String s) {
        int number;
        try {
            number = Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자가 아님");
        }
        return number;
    }

    private static boolean isPositiveInteger(String s) {
        return !s.startsWith("-");
    }
}
