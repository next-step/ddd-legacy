package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static Parser parser;
    private static Calculator calculator;

    public StringCalculator() {
        this.parser = new Parser();
        this.calculator = new Calculator();
    }

    public int add(final String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        try {
            String[] numbers = parser.parse(text);
            return calculator.sumOfNumbers(numbers);
        } catch (RuntimeException e) {
            throw new RuntimeException("[Error] : ", e);
        }
    }

}

class PositiveNumber {
    private final int value;

    public int getValue() {
        return value;
    }

    private PositiveNumber(int value) {
        if (value < 0) {
            throw new RuntimeException("양의 정수를 입력하세요.");
        }
        this.value = value;
    }

    public static PositiveNumber toPositiveNumber(String number) {
        try {
            int parseInt = Integer.parseInt(number);
            return new PositiveNumber(parseInt);
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자를 입력하세요.");
        }
    }
}

class Parser {
    private static final String DEFAULT_DELIMITERS = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\\n(.*)");

    public String[] parse(String text) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.find()) {
            String customDelimiter = Pattern.quote(matcher.group(1));
            return matcher.group(2).split(customDelimiter);
        }
        return text.split(DEFAULT_DELIMITERS);
    }
}

class Calculator {

    public int sumOfNumbers(String[] numbers) {
        return Arrays.stream(numbers)
                .mapToInt(this::parseWithPositiveNumber)
                .sum();
    }

    private int parseWithPositiveNumber(String number) {
        PositiveNumber positiveNumber = PositiveNumber.toPositiveNumber(number);
        return positiveNumber.getValue();
    }
}