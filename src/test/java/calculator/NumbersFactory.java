package calculator;

import static calculator.DelimiterPattern.CUSTOM_REGEX;
import static calculator.DelimiterPattern.DEFAULT_REGEX;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumbersFactory {

    public int[] getNumbers(String input) {
        Matcher matcher = getCustomPatternMatcher(input);
        if (matcher.find()) {
            return splitNumbers(getTokenByCustomDelimiter(matcher));
        }
        return splitNumbers(getTokenByDefaultDelimiter(input));
    }

    private Matcher getCustomPatternMatcher(String input) {
        return Pattern.compile(CUSTOM_REGEX).matcher(input);
    }

    private String[] getTokenByCustomDelimiter(Matcher m) {
        String customDelimiter = m.group(1);
        return m.group(2).split(customDelimiter);
    }

    private String[] getTokenByDefaultDelimiter(String input) {
        return input.split(DEFAULT_REGEX);
    }

    private int[] splitNumbers(String[] tokens) {
        List<Integer> numbers = new ArrayList<>();
        for (String number : tokens) {
            if (!isNaturalNumber(number)) {
                throw new RuntimeException(ExceptionMessages.WRONG_INPUT_EXCEPTION);
            }
            numbers.add(Integer.parseInt(number));
        }
        return numbers.stream().mapToInt(v -> v).toArray();
    }

    private boolean isNaturalNumber(String param) {
        char[] characters = param.toCharArray();
        for (char character : characters) {
            if (character > '9' || character < '0') {
                return false;
            }
        }
        return true;
    }
}
