package calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String DEFAULT_DELIMITER = "[,:]";

    public int add(final String text) {

        if (text == null || text.length() == 0) {
            return 0;
        }

        String[] stringNumbers = parseStringToNumberList(text);
        if (stringNumbers.length == 0) {
            return 0;
        }

        return calculateSumOfNumbers(parseStringListToNumberList(stringNumbers));
    }

    private int calculateSumOfNumbers(List<Integer> numberList) {
        int total = 0;
        for (Integer number : numberList) {
            total += number;
        }
        return total;
    }

    private String[] parseStringToNumberList(final String text) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            return m.group(2).trim().split(m.group(1));
        }

        return text.split(DEFAULT_DELIMITER);
    }

    public List<Integer> parseStringListToNumberList(final String[] stringNumbers) {

        List<Integer> numbers = new ArrayList<>();
        for (String s : stringNumbers) {
            if (isEmptyString(s)) {
                continue;
            }

            if (isNegativeNumber(s)) {
                throw new RuntimeException("음수를 입력함");
            }

            numbers.add(parseNumber(s));
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

    private static boolean isNegativeNumber(String s) {
        return s.startsWith("-");
    }
}
