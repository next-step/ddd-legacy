package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static calculator.CalculatorUtil.*;
import static java.util.Arrays.stream;

public class Calculator {
    private final static String DEFAULT_DELIMITER_REGEX = ",|:";
    private final static String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";
    private final static int ZERO_VALUE = 0;

    public int add(String text) {
        if (isNullOrEmpty(text)) {
            return ZERO_VALUE;
        }
        String[] numbers = getNumbersFromText(text);
        return stream(numbers)
                .filter(CalculatorUtil::isNumeric)
                .map(CalculatorUtil::toInt)
                .filter(this::isPositive)
                .reduce(ZERO_VALUE, Integer::sum);
    }

    private String[] getNumbersFromText(String text) {
        String[] numbers = text.split(DEFAULT_DELIMITER_REGEX);
        /* 커스텀 구분자가 지정된 경우*/
        Matcher matcher = Pattern.compile(CUSTOM_DELIMITER_REGEX).matcher(text);
        if (matcher.find()) {
            String delimiter = matcher.group(1);
            numbers = matcher.group(2).split(delimiter);
        }
        return numbers;
    }

    private boolean isPositive(Integer number) {
        System.out.println(number);
        if (number < ZERO_VALUE) {
            throw new RuntimeException("문자열 계산기에 음수는 입력될 수 없습니다.");
        }
        return true;
    }
}
