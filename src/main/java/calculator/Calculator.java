package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

    private static final int ZERO = 0;
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String NEGATIVE_SYMBOL = "-";

    public int add(String value) {
        if (isNullAndBlank(value)) {
            return ZERO;
        }

        String[] numbers = splitByDelimiter(value);

        return addCalculate(numbers);
    }

    private Integer addCalculate(String[] numbers) {
        return Arrays.stream(numbers)
                .peek(this::validateNumberAndNegative)
                .mapToInt(Integer::parseInt)
                .sum();
    }

    private String[] splitByDelimiter(String value) {
        Matcher matched = CUSTOM_DELIMITER_PATTERN.matcher(value);

        if (matched.find()) {
            String customDelimiter = matched.group(1);
            String input = matched.group(2);

            return input.split(customDelimiter);
        }

        return value.split(DEFAULT_DELIMITER);
    }

    public void validateNumberAndNegative(String value) {
        if (value.chars().anyMatch(Character::isLetter)) {
            throw new RuntimeException("숫자가 아닌 값이 포함되어 있습니다.");
        }

        if (value.contains(NEGATIVE_SYMBOL)) {
            throw new RuntimeException("음수는 입력할 수 없습니다.");
        }
    }

    private boolean isNullAndBlank(String value) {
        return value == null || value.isBlank();
    }
}
