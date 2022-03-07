package stringcalculator;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final StringParser stringParser = new StringParser();
    private final Numbers numbers;
    private String[] token;

    public StringCalculator() {
        numbers = new Numbers();
    }

    private Boolean checkIfNull(String text) {
        if (StringUtils.hasText(text)) {
            return false;
        }
        return true;
    }

    private int calculate(Numbers numbers) {
        return numbers.getNumbers().stream()
                .map(Number::getValue)
                .reduce(0, Integer::sum);
    }

    public int add(String text) {
        if (checkIfNull(text)) {
            return 0;
        }
        token = stringParser.splitStringToToken(text);
        numbers.addNumbersFromToken(token);
        return calculate(numbers);
    }
}
