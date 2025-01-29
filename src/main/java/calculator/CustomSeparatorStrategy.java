package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomSeparatorStrategy implements CalculateStrategy {
    private static final Pattern CUSTOM_SEPARATOR_PATTERN = Pattern.compile("//(.)\\\\n(.*)");

    @Override
    public int calculate(String input) {
        Matcher m = CUSTOM_SEPARATOR_PATTERN.matcher(input);

        if (m.find()) { // find를 공통을 빼는 것?
            String customDelimiter = m.group(1);
            String[] splitInput = m.group(2).split(customDelimiter);
            return sum(splitInput);
        } else {
            throw new RuntimeException("커스텀 구분자가 들어오지 않았습니다.");
        }
    }

    @Override
    public boolean canCalculate(String input) {
        return CUSTOM_SEPARATOR_PATTERN.matcher(input).find();
    }

    private int sum(String[] splitInput) {
        return Arrays.stream(splitInput)
                .mapToInt(this::convertToInt)
                .sum();
    }

    private int convertToInt(String a) {
        if (a.isEmpty()) {
            return 0;
        }
        try {
            return convertToPositiveNumber(a);
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자의 형태가 아닙니다.", e);
        }

    }

    private int convertToPositiveNumber(String a) {
        int number = Integer.parseInt(a);
        if (number < 0) {
            throw new IllegalArgumentException("음수는 들어올 수 없습니다.");
        }
        return number;
    }
}
