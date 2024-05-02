package calculator;

import static calculator.DelimiterPattern.CUSTOM_REGEX;
import static calculator.DelimiterPattern.DEFAULT_REGEX;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumbersFactory {

    public List<Integer> getNumbers(String input) {
        Matcher matcher = getCustomPatternMatcher(input);
        if (matcher.find()) {
            return convertNumbersToInt(getTokenByCustomDelimiter(matcher));
        }
        return convertNumbersToInt(getTokenByDefaultDelimiter(input));
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

    private List<Integer> convertNumbersToInt(String[] tokens) {
        List<Integer> numbers = new ArrayList<>();
        for (String number : tokens) {
            validateNumber(number);
            numbers.add(Integer.parseInt(number));
        }
        return numbers;
    }

    private void validateNumber(String number) {
        if (!isNaturalNumber(number)) {
            throw new RuntimeException(ExceptionMessages.WRONG_INPUT_EXCEPTION);
        }
    }

    private boolean isNaturalNumber(String param) {
        char[] characters = param.toCharArray();
        List<Character> temp = new ArrayList<>();
        for (char character : characters) {
            temp.add(character);
        }
        return temp.stream().allMatch(this::isNumberCharacter);
    }

    private boolean isNumberCharacter(char character) {
        return character <= '9' && character >= '0';
    }
}
