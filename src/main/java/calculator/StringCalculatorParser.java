package calculator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculatorParser {
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");

    public PositiveNumbers toPositiveNumbers(String text) {
        List<Integer> numbers = parseStringToNumbers(text);
        return new PositiveNumbers(numbers);
    }

    private List<Integer> parseStringToNumbers(final String text) {

        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        Matcher matcher = pattern.matcher(text);
        String[] stringNumbers = getStringNumbers(text, matcher);

        return Arrays.stream(stringNumbers)
                .filter(stringNumber -> !stringNumber.isEmpty())
                .map(this::toNumber)
                .collect(Collectors.toUnmodifiableList());
    }

    private static String[] getStringNumbers(String text, Matcher matcher) {
        String[] stringNumbers;
        if (matcher.find()) {
            stringNumbers = matcher.group(2)
                    .trim()
                    .split(matcher.group(1));
        } else {
            stringNumbers = text.split(DEFAULT_DELIMITER);
        }
        return stringNumbers;
    }

    private int toNumber(String stringNumber) {
        int number;

        try {
            number = Integer.parseInt(stringNumber);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자가 아님");
        }

        if (number < 0) {
            throw new IllegalArgumentException("음수를 입력함");
        }

        return number;
    }
}
