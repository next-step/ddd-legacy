package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static calculator.CalculatorUtil.*;
import static java.util.Arrays.stream;

public class Calculator {
    private final static String DEFAULT_DELIMITER_REGEX = ",|:";
    private final static String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";

    public int add(String text) {
        if (isNullOrEmpty(text)) {
            return 0;
        }
        if (text.length() == 1 && isNumeric(text)) {
            return toInt(text);
        }
        String[] numbers = getNumbersFromText(text);
        return stream(numbers)
                .filter(CalculatorUtil::isNumeric)
                .map(CalculatorUtil::toInt)
                .filter(this::isPositive)
                .reduce(0, Integer::sum);
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
        if (number < 0) {
            throw new RuntimeException("문자열 계산기에 음수는 입력될 수 없습니다.");
        }
        return true;
    }
}
